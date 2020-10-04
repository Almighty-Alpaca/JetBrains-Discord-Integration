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

import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jsoup.Jsoup

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    id("com.github.johnrengelman.shadow")
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

    implementation(project(":icons")) {
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

    prepareSandbox task@{
        setLibrariesToIgnore(*configurations.filter { it.isCanBeResolved }.toTypedArray())

        dependsOn(shadowJar)

        pluginJar(shadowJar.get().archiveFile)
    }

    build {
        dependsOn(buildPlugin)
    }

    check {
        dependsOn(verifyPlugin)
    }

    shadowJar task@{
        fun prefix(pkg: String, configure: Action<SimpleRelocator>? = null) =
            relocate(pkg, "${rootProject.group}.dependencies.$pkg", configure)

        mergeServiceFiles()

        prefix("club.minnced.discord.rpc")
        prefix("com.fasterxml.jackson.annotation")
        prefix("com.fasterxml.jackson.core")
        prefix("com.fasterxml.jackson.databind")
        prefix("com.fasterxml.jackson.dataformat.yaml")
        prefix("com.jagrosh.discordipc")
        prefix("javassist")
        prefix("okhttp3")
        prefix("okio")
        prefix("org.apache.commons.io")
        prefix("org.yaml.snakeyaml")

        val iconPaths = arrayOf(
            Regex("""/?discord/applications/.*\.png"""),
            Regex("""/?discord/themes/.*\.png""")
        )

        transform(PngOptimizingTransformer(128, *iconPaths))
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }

    withType<AbstractArchiveTask> {
        archiveBaseName.set("${rootProject.name}-${project.name.capitalize()}")
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
