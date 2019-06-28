import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jsoup.Jsoup

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    id("com.github.johnrengelman.shadow")
}

configure<IntelliJPluginExtension> {
    // https://www.jetbrains.com/intellij-repository/releases
    version = "2018.2"
    // version = "191.7479.19"

    downloadSources = true

    updateSinceUntilBuild = false

    sandboxDirectory = "${project.rootDir.canonicalPath}/.sandbox"

    // For testing with a custom theme
    // setPlugins("com.chrisrm.idea.MaterialThemeUI:3.10.0")
}

tasks {
    patchPluginXml {
        changeNotes(readInfoFile(rootProject.file("CHANGELOG.md")))
        pluginDescription(readInfoFile(rootProject.file("DESCRIPTION.md")))
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.2.2")

    compile(project(":shared")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compile(group = "club.minnced", name = "java-discord-rpc", version = "2.0.2")

    compile(group = "com.squareup.okhttp3", name = "okhttp", version = "4.0.0")

    compile(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
}

project.setProperty("archivesBaseName", "${rootProject.name}-${project.name.capitalize()}")

tasks {
    withType<RunIdeTask> {
        environment["ICONS"] = "local:${project(":icons").parent!!.projectDir.absolutePath}"
        // environment["ICONS"] = "bintray:almightyalpaca/JetBrains-Discord-Integration/Icons"
    }

    prepareSandbox task@{
        setLibrariesToIgnore(*configurations.filter { it.isCanBeResolved }.toTypedArray())

        dependsOn(shadowJar)

        pluginJar(shadowJar.get().archiveFile)
    }

    build {
        dependsOn(buildPlugin)
    }

    buildSearchableOptions {
        enabled = false // TODO: re-enable buildSearchableOptions before release (disabled for faster compilation)
    }

    fun ShadowJar.prefix(pkg: String, configure: Action<SimpleRelocator>? = null) = relocate(pkg, "${project.group}.dependencies.$pkg", configure)

    shadowJar task@{
        mergeServiceFiles()

        prefix("org.yaml.snakeyaml")
        prefix("org.scijava.nativelib")
        prefix("org.newsclub") {
            exclude("org.newsclub.net.unix.*")
        }
        prefix("org.kohsuke.github")
        prefix("org.json")
        prefix("org.jetbrains.annotations")
        prefix("org.intellij.lang.annotations")
        prefix("org.apache.logging.slf4j")
        prefix("org.apache.logging.log4j")
        prefix("org.apache.commons.lang3")
        prefix("org.apache.commons.io")
        prefix("org.apache.commons.collections")
        prefix("org.apache.commons.codec")
        prefix("okio")
        prefix("okhttp3")
        prefix("kotlinx.coroutines")
        prefix("kotlin")
        prefix("com.jagrosh.discordipc")
        prefix("com.fasterxml.jackson.dataformat.yaml")
        prefix("com.fasterxml.jackson.databind")
        prefix("com.fasterxml.jackson.core")
        prefix("com.fasterxml.jackson.annotation")
        prefix("club.minnced.discord.rpc")
    }

    processResources {
        filesMatching("/discord/changes.html") {
            val document = Jsoup.parse(readInfoFile(rootProject.file("CHANGELOG.md")))
            val body = document.getElementsByTag("body")[0]
            val list = body.getElementsByTag("ul")[0]

            expand("changes" to list.toString())
        }
    }
}

operator fun MatchResult.get(i: Int) = groupValues[i]

val github = "https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration/"

fun readInfoFile(file: File) = file.readText()
    // Remove unnecessary whitespace
    .trim()

    // Replace headlines
    .replace(Regex("(\\r?\\n|^)##(.*)(\\r?\\n|\$)")) { match -> "${match[1]}<b>${match[2]}</b>${match[3]}" }

    // Replace issue links
    .replace(Regex("\\[([^\\[]+)\\]\\(([^\\)]+)\\)")) { match -> "<a href=\"${match[2]}\">${match[1]}</a>" }
    .replace(Regex("\\(#([0-9]+)\\)")) { match -> "(<a href=\"${github}/issues/${match[1]}\">#${match[1]}</a>)" }

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

tasks {
    //fun showHtml(html: String) = JFrame("Changelog").apply {
    //    defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    //    contentPane = JScrollPane(JLabel("<html>" + html))
    //    java.awt.Dimension screenSize = Toolkit . defaultToolkit . screenSize
    //            preferredSize = java.awt.Dimension((screenSize.width / 2).intValue(), (screenSize.height / 2).intValue())
    //    location = java.awt.Point((screenSize.width / 4).intValue(), (screenSize.height / 4).intValue())
    //    alwaysOnTop = true
    //    pack()
    //    visible = true
    //}

    //tasks.create(name: "showChangelog") {
    //    group = "markdown"
    //
    //    doLast {
    //        showHtml(readInfoFile("CHANGELOG.md") as String)
    //    }
    //}

    val printChangelog by registering {
        group = "markdown"

        doLast {
            println(readInfoFile(rootProject.file("CHANGELOG.md")))
        }
    }

    //tasks.create(name: "showDescription") {
    //    group = "markdown"
    //
    //    doLast {
    //        showHtml(readInfoFile("DESCRIPTION.md") as String)
    //    }
    //}

    val printDescription by registering {
        group = "markdown"

        doLast {
            println(readInfoFile(rootProject.file("DESCRIPTION.md")))
        }
    }
}
