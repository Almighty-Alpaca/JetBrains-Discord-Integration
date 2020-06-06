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
import java.io.InputStream
import java.io.OutputStream

plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.palantir.baseline-exact-dependencies")
}

val dockerDevContainerName = "${project.group}-dev"

val dockerTag = "almightyalpaca/jetbrains-discord-integration-bot"

application {
    mainClassName = "com.almightyalpaca.jetbrains.plugins.discord.bot.MainKt"
}

repositories {
    jcenter()
}

dependencies {
    val versionJda: String by project
    val versionOkHttp: String by project
    val versionJdaUtilities: String by project
    val versionKonf: String by project
    val versionLogback: String by project

    // Kotlin standard library
    implementation(kotlin(module = "stdlib"))

    // implementation script engine
    implementation(kotlin(module = "script-util"))
    implementation(kotlin(module = "compiler-embeddable"))
    implementation(kotlin(module = "scripting-compiler-embeddable"))

    // JDA (without audio)
    implementation(group = "net.dv8tion", name = "JDA", version = versionJda) {
        exclude(group = "club.minnced", module = "opus-java")
    }

    // JDA-Utilities
    implementation(group = "com.jagrosh", name = "jda-utilities-command", version = versionJdaUtilities)

    // Konf (support for unused formats removed)
    implementation(group = "com.uchuhimo", name = "konf", version = versionKonf) {
        exclude(group = "com.moandjiezana.toml", module = "toml4j")
        exclude(group = "org.dom4j", module = "dom4j")
        exclude(group = "org.eclipse.jgit", module = "org.eclipse.jgit")

        // TODO: exclude more modules from Konf
        // exclude(group = "", module = "")
    }

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp)

    // Logback Classic
    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)
}

tasks {
    val dockerBuildDir = File(buildDir, "docker")

    val dockerCopy by registering(Sync::class) {
        group = "docker"
        description = "Copies sources and Dockerfile to build directory"

        val shadowJar = project.tasks["shadowJar"] as ShadowJar

        dependsOn(shadowJar)

        from("Dockerfile")

        from(shadowJar.archiveFile) {
            rename { "app.jar" }
        }

        into(dockerBuildDir)
    }

    val dockerContextCreate by registering(Exec::class) {
        group = "docker"
        description = "Creates Docker build context"

        errorOutput = OutputStream.nullOutputStream()
        standardOutput = OutputStream.nullOutputStream()
        standardInput = InputStream.nullInputStream()

        commandLine = listOf(
            "docker", "buildx",
            "create",
            "--name", "${project.name}-Builder"
        )

        isIgnoreExitValue = true
    }

    val dockerContextUse by registering(Exec::class) {
        group = "docker"
        description = "Selects Docker build context as active one"

        dependsOn(dockerContextCreate)

        commandLine = listOf(
            "docker", "buildx",
            "use", "${project.name}-Builder"
        )
    }

    create<Exec>("dockerContextDelete") {
        group = "docker"
        description = "Deletes Docker build context"

        commandLine = listOf(
            "docker", "buildx",
            "rm", "${project.name}-Builder"
        )
    }

    val dockerPrepare by registering {
        group = "docker"
        description = "Copies files into build directory and prepares Docker build context"

        dependsOn(dockerCopy)
        dependsOn(dockerContextUse)
    }

    create<Exec>("dockerBuild") {
        group = "docker"
        description = "Builds Docker image"

        dependsOn(dockerPrepare)

        workingDir = dockerBuildDir

        commandLine = listOf(
            "docker", "buildx",
            "build",
            "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
            "--tag", dockerTag,
            "."
        )
    }

    val dockerBuildLoad by registering(Exec::class) {
        group = "docker"
        description = "Builds Docker image and loads it into local daemon"

        dependsOn(dockerPrepare)

        workingDir = dockerBuildDir

        commandLine = listOf(
            "docker", "buildx",
            "build",
            // TODO: build and export multi-arch manifest as soon as Docker supports it
            // "--platform", "linux/amd64,linux/arm/v7",
            "--platform", "linux/amd64",
            "--tag", dockerTag,
            "--load",
            "."
        )
    }

    val dockerBuildPush by registering(Exec::class) {
        group = "docker"
        description = "Builds Docker image and pushes it to Docker Hub"

        dependsOn(dockerPrepare)

        workingDir = dockerBuildDir

        commandLine = listOf(
            "docker", "buildx",
            "build",
            "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
            "--tag", dockerTag,
            "--push",
            "."
        )
    }

    create<Exec>("dockerRun") {
        group = "docker"
        description = "Runs Docker image"

        dependsOn(dockerBuildLoad)

        commandLine = listOf(
            "docker", "run",
            "--name", dockerDevContainerName,
            "--rm",
            "--mount", "type=bind,source=${project.file("config.yaml").absolutePath},target=/config/config.yaml",
            dockerTag
        )
    }

    create<Exec>("dockerRunDaemon") {
        group = "docker"
        description = "Runs Docker image as daemon"

        dependsOn(dockerBuildLoad)

        commandLine = listOf(
            "docker", "run",
            "-d",
            "--name", dockerDevContainerName,
            "--rm",
            "--mount", "type=bind,source=${project.file("config.yaml").absolutePath},target=/config/config.yaml",
            dockerTag
        )
    }

    create<Exec>("dockerStatus") {
        group = "docker"
        description = "Shows stats about running Docker image"

        commandLine = listOf(
            "docker", "container", "ls", "--filter", "name=$dockerDevContainerName"
        )
    }

    create<Exec>("dockerStop") {
        group = "docker"
        description = "Stops Docker image"

        commandLine = listOf(
            "docker", "container", "stop", dockerDevContainerName
        )
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    checkUnusedDependencies {
        // Logging backend
        ignore("ch.qos.logback", "logback-classic")

        // Kotlin compiler & scripting engine
        ignore("org.jetbrains.kotlin", "kotlin-compiler-embeddable")
        ignore("org.jetbrains.kotlin", "kotlin-script-util")
        ignore("org.jetbrains.kotlin", "kotlin-scripting-compiler-embeddable")
    }

    checkImplicitDependencies {
        // Nullability annotations
        ignore("org.jetbrains", "annotations")
    }
}
