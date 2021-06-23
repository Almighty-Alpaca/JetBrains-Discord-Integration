/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jsoup.Jsoup

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    antlr
}

val github = "https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration"

dependencies {
    val versionCommonsIo: String by project
    val versionJackson: String by project
    val versionOkHttp: String by project
    val versionRpc: String by project
    val versionJUnit: String by project
    val versionAntlr: String by project

    implementation(project(path = ":icons", configuration = "minimizedJar"))

    // Not sure why selecting the default configuration explicitly is required here but whatever
    implementation(project(path = ":discord-game-sdk:jvm", configuration = "default"))

    implementation(group = "club.minnced", name = "java-discord-rpc", version = versionRpc)

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp)

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = versionJackson)

    antlr(group = "org.antlr", name = "antlr4", version = versionAntlr)
    implementation(group = "org.antlr", name = "antlr4-runtime", version = versionAntlr)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = versionJUnit)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = versionJUnit)
}

repositories {
    jcenter() // TODO: remove once using GameSDK
}

val generatedSourceDir = project.file("src/generated")
val generatedJavaSourceDir = generatedSourceDir.resolve("java")

sourceSets {
    main {
        java {
            srcDir(generatedJavaSourceDir)
        }
    }
}

val isCI by lazy { System.getenv("CI") != null }

intellij {
    val versionIde: String by project

    version.set(versionIde)

    downloadSources.set(!isCI)

    updateSinceUntilBuild.set(false)

    sandboxDir.set("${project.rootDir.absolutePath}/.sandbox")

    instrumentCode.set(false)

    plugins.add("git4idea")

    // For testing with a custom theme
    // setPlugins("git4idea", "com.chrisrm.idea.MaterialThemeUI:3.10.0")
}

configurations {
    // https://github.com/gradle/gradle/issues/820
    compile {
        setExtendsFrom(extendsFrom.filter { it != antlr.get() })
    }

    all {
        if (name.contains("kotlin", ignoreCase = true) || name.contains("idea", ignoreCase = true)) {
            return@all
        }

        resolutionStrategy.dependencySubstitution {
            val ideaDependency = intellij.getIdeaDependency(project).let { "com.jetbrains:${it.name}:${it.version}" }

            val ideaModules = listOf(
                "org.jetbrains.kotlin:kotlin-reflect",
                "org.jetbrains.kotlin:kotlin-stdlib",
                "org.jetbrains.kotlin:kotlin-stdlib-common",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
                "org.jetbrains.kotlin:kotlin-test",
                "org.jetbrains.kotlin:kotlin-test-common",
                "org.jetbrains.kotlinx:kotlinx-coroutines-core",
                "org.jetbrains.kotlinx:kotlinx-coroutines-core-common",
                "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8",
                "org.slf4j:slf4j-api"
            )

            all action@{
                val requested = requested as? ModuleComponentSelector ?: return@action

                if ("${requested.group}:${requested.module}" in ideaModules) {
                    useTarget(ideaDependency)
                }
            }
        }
    }
}

tasks {
    val minimizedJar by registering(ShadowJar::class) {
        group = "build"

        archiveClassifier.set("minimized")

        from(sourceSets.main.map(SourceSet::getOutput))

        val iconPaths = arrayOf(
            Regex("""/?discord/images/.*\.png""")
        )

        transform(PngOptimizingTransformer(128, *iconPaths))
    }

    patchPluginXml {
        changeNotes.set(readInfoFile(project.file("changelog.md")))
        pluginDescription.set(readInfoFile(project.file("description.md")))
    }

    runIde {
        // Use local icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "local:${project(":icons").parent!!.projectDir.absolutePath}"

        // Use icons from specific bintray repo
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "bintray:almightyalpaca/JetBrains-Discord-Integration/Icons"

        // Use classpath icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "classpath:discord"

        // Use Discord GameSDK
        environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection"] = "gamesdk"
    }

    publishPlugin {
        if (project.extra.has("JETBRAINS_TOKEN")) {
            token.set(project.extra["JETBRAINS_TOKEN"] as String?)
        } else {
            enabled = false
        }

        if (!(version as String).matches(Regex("""\d+\.\d+\.\d+"""))) {
            channels.set(listOf("eap"))
        } else {
            channels.set(listOf("default", "eap"))
        }
    }

    buildPlugin {
        archiveBaseName.set(rootProject.name)
    }

    jarSearchableOptions {
        archiveBaseName.set(project.name)
        archiveClassifier.set("options")
    }

    prepareSandbox task@{
        dependsOn(minimizedJar)

        pluginJar.set(minimizedJar.flatMap { it.archiveFile })
    }

    build {
        dependsOn(buildPlugin)
    }

    check {
        dependsOn(verifyPlugin)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    generateGrammarSource {
        val packageName = "com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr"

        arguments = arguments + listOf("-package", packageName, "-no-listener")
        outputDirectory = generatedJavaSourceDir.resolve(packageName.replace('.', File.separatorChar))
    }

    compileKotlin {
        dependsOn(generateGrammarSource)
    }

    compileTestKotlin {
        dependsOn(generateTestGrammarSource)
    }

    clean {
        delete(generatedSourceDir)
    }

    processResources {
        filesMatching("/discord/changes.html") {
            val document = Jsoup.parse(readInfoFile(project.file("changelog.md")))
            val body = document.getElementsByTag("body")[0]
            val list = body.getElementsByTag("ul")[0]

            expand("changes" to list.toString())
        }
    }

    create("printChangelog") {
        group = "markdown"

        doLast {
            println(readInfoFile(project.file("changelog.md")))
        }
    }

    create("printDescription") {
        group = "markdown"

        doLast {
            println(readInfoFile(project.file("description.md")))
        }
    }

    check {
        dependsOn(":uploader:check")
    }

    test {
        useJUnitPlatform()

        maxHeapSize = "1G"
    }
}

fun readInfoFile(file: File): String {
    operator fun MatchResult.get(i: Int) = groupValues[i]

    return file.readText()
        // Remove unnecessary whitespace
        .trim()

        // Replace headlines
        .replace(Regex("(\\r?\\n|^)##(.*)(\\r?\\n|\$)")) { match -> "${match[1]}<b>${match[2]}</b>${match[3]}" }

        // Replace issue links
        .replace(Regex("\\[([^\\[]+)\\]\\(([^\\)]+)\\)")) { match -> "<a href=\"${match[2]}\">${match[1]}</a>" }
        .replace(Regex("\\(#([0-9]+)\\)")) { match -> "(<a href=\"$github/issues/${match[1]}\">#${match[1]}</a>)" }

        // Replace inner lists
        .replace(Regex("\r?\n  - (.*)")) { match -> "<li>${match[1]}</li>" }
        .replace(Regex("((?:<li>.*</li>)+)")) { match -> "<ul>${match[1]}</ul>" }

        // Replace lists
        .replace(Regex("\r?\n- (.*)")) { match -> "<li>${match[1]}</li>" }
        .replace(Regex("((?:<li>.*</li>)+)")) { match -> "<ul>${match[1]}</ul>" }
        .replace(Regex("\\s*<li>\\s*"), "<li>")
        .replace(Regex("\\s*</li>\\s*"), "</li>")
        .replace(Regex("\\s*<ul>\\s*"), "<ul>")
        .replace(Regex("\\s*</ul>\\s*"), "</ul>")

        // Replace newlines
        .replace("\n", "<br>")
}
