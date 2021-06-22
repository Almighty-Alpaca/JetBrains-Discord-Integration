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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    jniHeaders
}

base {
    archivesBaseName = "discord-game-sdk"
}

repositories {
    mavenCentral()
}

dependencies {
    val versionCoroutines: String by project
    val versionJavaxValidation: String by project
    val versionJUnit: String by project

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(platform(kotlinx("coroutines-bom", versionCoroutines)))
    implementation(kotlinx("coroutines-core"))

    compileOnly(group = "javax.validation", name = "validation-api", version = versionJavaxValidation)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = versionJUnit)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = versionJUnit)

    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = "0.23")
}

kotlin {
    explicitApi()
}

sourceSets {
    main {
        resources {
            // TODO: use release binaries
            srcDir(project(":discord-game-sdk:native").layout.buildDirectory.dir("lib/main/debug"))
            srcDir(project(":discord-game-sdk:native").layout.projectDirectory.dir("lib/discord_game_sdk"))
        }
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                "-Xjvm-default=enable",
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }

    processResources {
        dependsOn(":discord-game-sdk:native:assemble")

        filesNotMatching(listOf("**/*.dll", "**/*.dylib", "**/*.so")) {
            exclude()
        }

        filesMatching("shared/**/*.*") {
            path = path.substringAfter('/')
        }
    }

    test {
        useJUnitPlatform()

        // For JNI debugging
        jvmArgs = listOf(
            // "-Xcheck:jni",
            // "-verbose:jni"
        )
    }

    val jniHeaders by registering(JavaExec::class) task@{
        val toolsProject = project(":tools:jniheaders")

        val toolsSources = toolsProject.sourceSets.main.get()
        val projectCompileKotlin = compileKotlin.get() as KotlinCompile

        group = "build"

        classpath = toolsSources.runtimeClasspath + projectCompileKotlin.outputs.files + projectCompileKotlin.classpath

        main = "tools.jniheaders.JniHeadersKt"

        args = listOf(
            project.layout.buildDirectory.dir("generated/headers").get().asFile.absolutePath,
            // Including everything produces a huge amount of headers that aren't needed currently
            // "gamesdk.api.*",
            // "gamesdk.api.types.*",
            // "gamesdk.impl.*",
            // "gamesdk.impl.types.*",
            "gamesdk.impl.Events",
            "gamesdk.impl.events.*",
            "gamesdk.impl.NativeCallback",
            "gamesdk.impl.NativeDiscordObjectResult.*",
            "gamesdk.impl.types.NativeDiscordActivity",
            "gamesdk.impl.types.NativeDiscordImage*",
            "gamesdk.impl.types.NativeDiscordOAuth2Token",
            "gamesdk.impl.types.NativeDiscordPresence",
            "gamesdk.impl.types.NativeDiscordRelationship",
            "gamesdk.impl.types.NativeDiscordUser"
        )
    }
}
