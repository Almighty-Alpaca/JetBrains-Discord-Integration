import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
}

intellij {
    // https://www.jetbrains.com/intellij-repository/releases
    version = "191.5701.16"

    downloadSources = true

    updateSinceUntilBuild = false
}

project.setProperty("archivesBaseName", "${rootProject.name}-${project.name.capitalize()}")

tasks.withType<RunIdeTask> {
    environment["REPOSITORY"] = rootProject.projectDir.absolutePath
}

dependencies {
    compile(project(":shared")) {
        exclude(group = "org.jetbrains.kotlin")

        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")

        exclude(group = "org.yaml", module = "snakeyaml")

        exclude(group = "org.slf4j", module = "slf4j-api")

        exclude(group = "commons-collections", module = "commons-collections")
        exclude(group = "commons-codec", module = "commons-codec")
        exclude(group = "commons-io", module = "commons-io")
        exclude(group = "org.apache.commons", module = "commons-lang3")
        exclude(group = "org.apache.httpcomponents", module = "httpclient")

        exclude(group = "com.google.guava", module = "guava")
    }

    testCompile(kotlin("stdlib-jdk8"))
    testCompile(group = "commons-io", name = "commons-io", version = "2.6")
    testCompile(group = "commons-collections", name = "commons-collections", version = "3.2.2")
    testCompile(group = "com.google.guava", name = "guava", version = "27.0.1-jre")

    compile(group = "com.jagrosh", name = "DiscordIPC", version = "0.4") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "log4j", module = "log4j")
    }

    compile(group = "org.apache.logging.log4j", name = "log4j-to-slf4j", version = "2.11.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "log4j", module = "log4j")
    }

    compile(group = "org.kohsuke", name = "github-api", version = "1.95") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
        exclude(group = "commons-codec", module = "commons-codec")
        exclude(group = "commons-io", module = "commons-io")
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }

    compile(group = "com.squareup.okhttp3", name = "okhttp-urlconnection", version = "3.13.1")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.4.0")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.4.0")
}
