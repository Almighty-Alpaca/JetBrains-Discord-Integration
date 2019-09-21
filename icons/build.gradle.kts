/*
 * Copyright 2017-2019 Aljoscha Grebe
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

group = "com.almightyalpaca.jetbrains.plugins.discord.icons"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = URI("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation(project(":shared"))

    implementation(kotlin("stdlib"))

    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.1")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-io-jvm", version = "0.1.14")

    implementation(group = "io.ktor", name = "ktor-client-okhttp", version = "1.2.4")
    implementation(group = "io.ktor", name = "ktor-client-auth-jvm", version = "1.2.4")
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = "1.2.4")
    implementation(group = "io.ktor", name = "ktor-http-jvm", version = "1.2.4")
    implementation(group = "io.ktor", name = "ktor-utils-jvm", version = "1.2.4")

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.2.0")

    implementation(group = "org.apache.commons", name = "commons-text", version = "1.8")
    implementation(group = "commons-io", name = "commons-io", version = "2.6")

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.9.9.3")
}

tasks {
    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    val graphsDot by registering(JavaExec::class) task@{
        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.graphs.GraphsKt"
    }

    create("graphs") {
        group = "icons"

        dependsOn(graphsDot)

        doLast {
            val files = project.file("build/graphs").listFiles()!!
                .filter { f -> f.isFile }
                .map { f -> f.nameWithoutExtension }
            for (file in files) {
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
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.validator.LanguageValidatorKt"
    }

    val checkIcons by registering(JavaExec::class) task@{
        group = "verification"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.validator.IconValidatorKt"
    }

    check {
        dependsOn(checkLanguages)
        dependsOn(checkIcons)
    }

    create<JavaExec>("checkUnusedIcons") task@{
        group = "verification"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.find.UnusedIconFinderKt"
    }

    fun Task.checkTokens() {
        if (!project.extra.has("DISCORD_TOKEN") || !project.extra.has("BINTRAY_KEY")) {
            enabled = false
        }
    }

    val uploadIcons by registering(JavaExec::class) task@{
        group = "icons"

        dependsOn(checkIcons)
        checkTokens()

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.DiscordUploaderKt"

        environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
    }

    val uploadLanguages by registering(JavaExec::class) task@{
        group = "icons"

        dependsOn(checkLanguages)
        checkTokens()

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.BintrayUploaderKt"

        environment("BINTRAY_KEY", project.extra["BINTRAY_KEY"] as String)
    }

    create("upload") {
        group = "icons"

        dependsOn(check)

        dependsOn(uploadLanguages)
        dependsOn(uploadIcons)
    }
}
