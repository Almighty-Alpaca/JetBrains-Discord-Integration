import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
    id("com.github.johnrengelman.shadow")
}

intellij {
    // https://www.jetbrains.com/intellij-repository/releases
    version = "191.5701.16"

    downloadSources = true

    updateSinceUntilBuild = false
}

project.setProperty("archivesBaseName", "${rootProject.name}-${project.name.capitalize()}")

tasks {
    withType<RunIdeTask> {
        environment["ICONS"] = "local:${rootProject.projectDir.absolutePath}"
//        environment["ICONS"] = "github:Almighty-Alpaca/JetBrains-Discord-Integration:rewrite"
//        environment["ICONS"] = "bintray:almightyalpaca/JetBrains-Discord-Integration/Icons"
    }

    prepareSandbox task@{
        setLibrariesToIgnore(*configurations.filter { it.isCanBeResolved }.toTypedArray())

        dependsOn("shadowJar")

        pluginJar(shadowJar.get().archiveFile)
    }

    fun ShadowJar.prefix(pkg: String) = relocate(pkg, "com.almightyalpaca.jetbrains.plugins.discord.dependencies.$pkg")

    shadowJar task@{
        prefix("org.yaml.snakeyaml")
        prefix("org.scijava.nativelib")
        prefix("org.newsclub")
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
    }
}

dependencies {
    compile(project(":shared")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    compile(group = "com.jagrosh", name = "DiscordIPC", version = "0.4") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "log4j", module = "log4j")
    }

    compile(group = "org.apache.logging.log4j", name = "log4j-to-slf4j", version = "2.11.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "log4j", module = "log4j")
    }

//    compile(group = "org.kohsuke", name = "github-api", version = "1.95") {
//        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
//        exclude(group = "commons-codec", module = "commons-codec")
//        exclude(group = "commons-io", module = "commons-io")
//        exclude(group = "org.apache.commons", module = "commons-lang3")
//    }

    compile(group = "com.squareup.okhttp3", name = "okhttp-urlconnection", version = "3.13.1")

    compile(kotlin("stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.1.1")
}
