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
import org.jsoup.Jsoup

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellij)

    antlr
}

val github = "https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration"

dependencies {
    implementation(project(path = ":icons", configuration = "minimizedJar"))

    implementation(libs.discord.ipc)

    implementation(libs.commons.io)

    implementation(libs.jackson.dataformat.yaml)

    antlr(libs.antlr)
    implementation(libs.antlr.runtime)
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
    pluginName(rootProject.name)

    version(libs.versions.ide)

    downloadSources(!isCI)

    updateSinceUntilBuild(false)

    sandboxDir("${project.rootDir.absolutePath}/.sandbox")

    instrumentCode(false)

    plugins("vcs-git")
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    }
}

configurations {
    // https://github.com/gradle/gradle/issues/820
    api {
        setExtendsFrom(extendsFrom.filter { it != antlr.get() })
    }

    implementation {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-common")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.jetbrains.kotlin", "kotlin-test")
        exclude("org.jetbrains.kotlin", "kotlin-test-common")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core-common")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8")
        exclude("org.slf4j", "slf4j-api")
    }
}

testing {
    @Suppress("UnstableApiUsage")
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit.jupiter)

            targets.configureEach {
                testTask {
                    enableAssertions = true

                    maxHeapSize = "1G"
                }
            }

            dependencies {
                implementation(libs.kotlin.test)
            }
            project.tasks {
                compileTestKotlin {
                    dependsOn(generateTestGrammarSource)
                }
            }
        }
    }
}

tasks {
    val minimizedJar by registering(ShadowJar::class) {
        group = "build"

        archiveClassifier("minimized")

        from(sourceSets.main.map(org.gradle.api.tasks.SourceSet::getOutput))

        val iconPaths = arrayOf(
            Regex("""/?discord/images/.*\.png""")
        )

        transform(PngOptimizingTransformer(128, *iconPaths))
    }

    patchPluginXml {
        changeNotes(readInfoFile(project.file("changelog.md")))
        pluginDescription(readInfoFile(project.file("description.md")))
    }

    runIde {
        // Force a specific icon source
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "local:${projects.icons.parent!!.projectDir.absolutePath}"
        // environment["com.almightyalpaca.jetbrains.plugins.discord.plugin.source"] = "classpath:discord"

        enableAssertions = true
    }

    publishPlugin {
        if (project.extra.has("JETBRAINS_TOKEN")) {
            token(project.extra["JETBRAINS_TOKEN"] as String)
        } else {
            enabled = false
        }

        if (!(version as String).matches(Regex("""\d+\.\d+\.\d+"""))) {
            channels("eap")
        } else {
            channels(listOf("default", "eap"))
        }
    }

    buildPlugin {
        archiveBaseName(rootProject.name)
    }

    buildSearchableOptions {
        enabled = false;
    }

    jarSearchableOptions {
        archiveBaseName(project.name)
        archiveClassifier("options")
    }

    prepareSandbox {
        dependsOn(minimizedJar)

        pluginJar(minimizedJar.flatMap { it.archiveFile })
    }

    build {
        dependsOn(buildPlugin)
    }

    check {
        dependsOn(verifyPlugin)
    }

    generateGrammarSource {
        val packageName = "com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr"

        arguments = arguments + listOf("-package", packageName, "-no-listener")
        outputDirectory = generatedJavaSourceDir.resolve(packageName.replace('.', File.separatorChar))
    }

    compileKotlin {
        dependsOn(generateGrammarSource)
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
