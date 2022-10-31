/*
 * Copyright 2017.2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE.2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

version = "1.0.0.SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(project(":icons"))

    implementation(platform(libs.kotlin.bom.latest))
    implementation(libs.kotlin.stdlib)

    implementation(platform(libs.kotlinx.coroutines.bom.latest))
    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.auth.jvm)
    implementation(libs.ktor.client.core.jvm)
    implementation(libs.ktor.http.jvm)
    implementation(libs.ktor.utils.jvm)
    implementation(libs.ktor.io.jvm)

    implementation(libs.okhttp3)

    implementation(libs.commons.text)
    implementation(libs.commons.io)

    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            apiVersion = kotlinLanguageVersion(libs.versions.kotlin.latest())
            languageVersion = kotlinLanguageVersion(libs.versions.kotlin.latest())
        }
    }

    val graphsDot by registering(JavaExec::class) task@{
        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.graphs.GraphsKt")
    }

    create("graphs") {
        group = "icons"

        dependsOn(graphsDot)

        doLast {
            project.file("build/graphs").listFiles()!!
                .filter { f -> f.isFile }
                .map { f -> f.nameWithoutExtension }
                .forEach { file ->
                    exec {
                        workingDir = file("build/graphs")
                        commandLine = listOf("dot", "-Tpng", "$file.dot", "-o", "$file.png")
                    }
                }
        }
    }

    val checkLanguages by registering(JavaExec::class) task@{
        group = "verification"

        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.LanguageValidatorKt")
    }

    val checkExtensions by registering(JavaExec::class) task@{
        group = "verification"

        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.FileExtensionDuplicateFinderKt")
    }

    val checkIcons by registering(JavaExec::class) task@{
        group = "verification"

        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.IconValidatorKt")
    }

    check {
        dependsOn(checkLanguages)
        dependsOn(checkExtensions)
        dependsOn(checkIcons)
    }

    create<JavaExec>("checkUnusedIcons") task@{
        group = "verification"

        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.find.UnusedIconFinderKt")
    }

    val uploadDiscord by registering(JavaExec::class) task@{
        if (!project.extra.has("DISCORD_TOKEN")) {
            enabled = false
        }

        group = "upload"

        dependsOn(checkIcons)

        classpath = sourceSets.main().runtimeClasspath
        mainClass("com.almightyalpaca.jetbrains.plugins.discord.uploader.uploader.DiscordUploaderKt")

        if ("DISCORD_TOKEN" in project.extra) {
            environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
        } else {
            enabled = false
        }
    }

    create("upload") {
        group = "upload"

        dependsOn(check)

        dependsOn(uploadDiscord)
    }
}
