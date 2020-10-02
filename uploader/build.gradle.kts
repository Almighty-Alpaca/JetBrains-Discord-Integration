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

import java.net.URI

plugins {
    kotlin("jvm")
}

version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = URI("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    val versionCoroutines: String by project
    val versionCommonsIo: String by project
    val versionCommonsText: String by project
    val versionJackson: String by project
    val versionKtor: String by project
    val versionOkHttp: String by project

    implementation(project(":icons"))

    implementation(kotlin("stdlib"))

    implementation(platform(kotlinx("coroutines-bom", versionCoroutines)))
    implementation(kotlinx("coroutines-core"))

    implementation(platform(ktor("bom", versionKtor)))
    implementation(ktor("client-okhttp"))
    implementation(ktor("client-auth-jvm"))
    implementation(ktor("client-core-jvm"))
    implementation(ktor("http-jvm"))
    implementation(ktor("utils-jvm"))
    implementation(ktor("io-jvm"))

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp)

    implementation(group = "org.apache.commons", name = "commons-text", version = versionCommonsText)
    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(platform("com.fasterxml.jackson:jackson-bom:$versionJackson"))
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind")
}

tasks {
    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    val graphsDot by registering(JavaExec::class) task@{
        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.graphs.GraphsKt"
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

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.LanguageValidatorKt"
    }

    val checkExtensions by registering(JavaExec::class) task@{
        group = "verification"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.FileExtensionDuplicateFinderKt"
    }

    val checkIcons by registering(JavaExec::class) task@{
        group = "verification"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.validator.IconValidatorKt"
    }

    check {
        dependsOn(checkLanguages)
        dependsOn(checkExtensions)
        dependsOn(checkIcons)
    }

    create<JavaExec>("checkUnusedIcons") task@{
        group = "verification"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.find.UnusedIconFinderKt"
    }

    fun Task.checkTokens() {
        if (!project.extra.has("DISCORD_TOKEN") || !project.extra.has("BINTRAY_KEY")) {
            enabled = false
        }
    }

    val uploadDiscord by registering(JavaExec::class) task@{
        group = "upload"

        dependsOn(checkIcons)
        checkTokens()

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.uploader.DiscordUploaderKt"

        if ("DISCORD_TOKEN" in project.extra) {
            environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
        } else {
            enabled = false
        }
    }

    val uploadBintray by registering(JavaExec::class) task@{
        group = "upload"

        dependsOn(checkLanguages)
        checkTokens()

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.uploader.uploader.BintrayUploaderKt"

        if ("BINTRAY_KEY" in project.extra) {
            environment("BINTRAY_KEY", project.extra["BINTRAY_KEY"] as String)
        } else {
            enabled = false
        }
    }

    create("upload") {
        group = "upload"

        dependsOn(check)

        dependsOn(uploadBintray)
        dependsOn(uploadDiscord)
    }
}
