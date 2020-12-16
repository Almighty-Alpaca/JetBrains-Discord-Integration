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

@file:Suppress("SuspiciousCollectionReassignment")

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

    implementation(project(path = ":icons", configuration = "minimizedJar")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    }

    implementation(group = "club.minnced", name = "java-discord-rpc", version = versionRpc)

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    }

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = versionJackson)

    antlr("org.antlr", name = "antlr4", version = versionAntlr)
    implementation("org.antlr", name = "antlr4-runtime", version = versionAntlr)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = versionJUnit)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = versionJUnit)
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

// https://github.com/gradle/gradle/issues/820
configurations {
    compile {
        setExtendsFrom(extendsFrom.filter { it != antlr.get() })
    }
}

val isCI by lazy { System.getenv("CI") != null }

intellij {
    val versionIde: String by project

    version = versionIde

    downloadSources = !isCI

    updateSinceUntilBuild = false

    sandboxDirectory = "${project.rootDir.absolutePath}/.sandbox"

    instrumentCode = false

    setPlugins("git4idea")

    // For testing with a custom theme
    // setPlugins("git4idea", "com.chrisrm.idea.MaterialThemeUI:3.10.0")
}

tasks {
    val minimizedJar by registering(ShadowJar::class) {
        group = "build"

        archiveClassifier.set("minimized")

        from(sourceSets.main.map(org.gradle.api.tasks.SourceSet::getOutput))

        val iconPaths = arrayOf(
            Regex("""/?discord/images/.*\.png""")
        )

        transform(PngOptimizingTransformer(128, *iconPaths))
    }

    checkUnusedDependencies {
        ignore("com.jetbrains", "ideaIU")
    }

    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    patchPluginXml {
        changeNotes(readInfoFile(project.file("changelog.md")))
        pluginDescription(readInfoFile(project.file("description.md")))
    }

    runIde {
        // use local icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "local:${project(":icons").parent!!.projectDir.absolutePath}"

        // use icons from specific bintray repo
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "bintray:almightyalpaca/JetBrains-Discord-Integration/Icons"

        // use classpath icons
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "classpath:discord"
    }

    publishPlugin {
        if (project.extra.has("JETBRAINS_TOKEN")) {
            token(project.extra["JETBRAINS_TOKEN"])
        } else {
            enabled = false
        }

        if (!(version as String).matches(Regex("""\d+\.\d+\.\d+"""))) {
            channels("eap")
        } else {
            channels("default", "eap")
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

        pluginJar(minimizedJar.map { it.archiveFile }.get())
    }

    build {
        dependsOn(buildPlugin)
    }

    check {
        dependsOn(verifyPlugin)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    generateGrammarSource {
        val packageName = "com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr"

        arguments = arguments + listOf("-package", packageName, "-no-listener")
        outputDirectory = generatedJavaSourceDir.resolve(packageName.replace('.', File.separatorChar))
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
