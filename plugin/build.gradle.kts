import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    id("com.github.johnrengelman.shadow")
}

configure<IntelliJPluginExtension> {
    // https://www.jetbrains.com/intellij-repository/releases
    // version = "2018.1"
    version = "191.5849.21"

    downloadSources = true

    updateSinceUntilBuild = false

    sandboxDirectory = "${project.rootDir.canonicalPath}/.sandbox"

    // For testing with custom themes
    // setPlugins("com.chrisrm.idea.MaterialThemeUI:3.9.1")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.2.1")

    compile(project(":shared")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compile(group = "club.minnced", name = "java-discord-rpc", version = "2.0.2")

    compile(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.1")

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

        dependsOn("shadowJar")

        pluginJar(shadowJar.get().archiveFile)
    }

    buildSearchableOptions {
        enabled = false // TODO: re-enable buildSearchableOptions before release (disabled for faster compilation)
    }

    fun ShadowJar.prefix(pkg: String, configure: Action<SimpleRelocator>? = null) = relocate(pkg, "${project.group}.dependencies.$pkg", configure)

    shadowJar task@{
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
}
