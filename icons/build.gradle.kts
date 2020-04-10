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

    implementation(project(":shared"))

    implementation(kotlin("stdlib"))

    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:$versionCoroutines"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core")

    implementation(platform("io.ktor:ktor-bom:$versionKtor"))
    implementation(group = "io.ktor", name = "ktor-client-okhttp")
    implementation(group = "io.ktor", name = "ktor-client-auth-jvm")
    implementation(group = "io.ktor", name = "ktor-client-core-jvm")
    implementation(group = "io.ktor", name = "ktor-http-jvm")
    implementation(group = "io.ktor", name = "ktor-utils-jvm")
    implementation(group = "io.ktor", name = "ktor-io-jvm")

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

        if ("DISCORD_TOKEN" in project.extra) {
            environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
        } else {
            enabled = false
        }
    }

    val uploadLanguages by registering(JavaExec::class) task@{
        group = "icons"

        dependsOn(checkLanguages)
        checkTokens()

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.BintrayUploaderKt"

        if ("BINTRAY_KEY" in project.extra) {
            environment("BINTRAY_KEY", project.extra["BINTRAY_KEY"] as String)
        } else {
            enabled = false
        }
    }

    create("upload") {
        group = "icons"

        dependsOn(check)

        dependsOn(uploadLanguages)
        dependsOn(uploadIcons)
    }
}

operator fun ExtraPropertiesExtension.contains(key: String) = has(key)
