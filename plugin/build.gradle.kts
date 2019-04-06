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
    version = "191.5849.21"

    downloadSources = true

    updateSinceUntilBuild = false

    // For testing with custom themes
    // setPlugins("com.chrisrm.idea.MaterialThemeUI:3.8.0.2")
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

    buildSearchableOptions {
        enabled = false // TODO: re-enable buildSearchableOptions (disabled for faster compilation)
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

dependencies {
    // compileOnly(group = "org.swinglabs.swingx", name = "swingx-core", version = "1.6.5-1")

    compile(project(":shared")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // compile(group = "com.jagrosh", name = "DiscordIPC", version = "0.4") {
    //     exclude(group = "org.slf4j", module = "slf4j-api")
    //     exclude(group = "log4j", module = "log4j")
    // }
    // compile(group = "com.kohlschutter.junixsocket", name = "junixsocket-core", version = "2.2.0")
    //
    // compile(group = "org.apache.logging.log4j", name = "log4j-to-slf4j", version = "2.11.2") {
    //     exclude(group = "org.slf4j", module = "slf4j-api")
    //     exclude(group = "log4j", module = "log4j")
    // }

    compile(group = "club.minnced", name = "java-discord-rpc", version = "2.0.2")

    // compile(group = "org.kohsuke", name = "github-api", version = "1.95") {
    //     exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
    //     exclude(group = "commons-codec", module = "commons-codec")
    //     exclude(group = "commons-io", module = "commons-io")
    //     exclude(group = "org.apache.commons", module = "commons-lang3")
    // }
    //
    // compile(group = "com.squareup.okhttp3", name = "okhttp-urlconnection", version = "3.13.1")

    compile(kotlin("stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.1.1")
}
