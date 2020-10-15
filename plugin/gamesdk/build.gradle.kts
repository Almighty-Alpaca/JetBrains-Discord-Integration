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

@file:Suppress("SuspiciousCollectionReassignment")

import dev.nokee.language.cpp.internal.tasks.CppCompileTask
import dev.nokee.language.nativebase.HeaderSearchPath
import dev.nokee.runtime.nativebase.TargetMachine

plugins {
    kotlin("jvm")
    id("dev.nokee.cpp-language")
    id("dev.nokee.jni-library")
}

repositories {
    maven(url = "https://jitpack.io/")
}

dependencies {
    val versionJUnit: String by project

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = versionJUnit)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = versionJUnit)
    compileOnly(group = "javax.validation", name = "validation-api", version = "2.0.1.Final")
}

tasks {
    test {
        useJUnitPlatform()
    }

    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    withType<CppCompileTask> {
        headerSearchPaths.add(HeaderSearchPath { project.buildDir.resolve("generated/jni-headers") })
    }

    val generateJniHeadersKotlin by registering(KotlinJniHeadersTask::class)

    val generateJniHeaders by registering {
        group = "build"

        dependsOn(generateJniHeadersKotlin)
        dependsOn(compileJava)
    }

    withType<CCompile> {
        dependsOn(generateJniHeaders)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

library {
    targetMachines.set(
        listOf(
            machines.windows.x86_64,
            machines.windows.x86,
            machines.linux.x86_64,
            machines.macOS.x86_64
        )
    )

    variants.configureEach {
        val prebuiltLibraryFile = file(getLibraryFilePathFor(targetMachine))
        nativeRuntimeFiles.from(prebuiltLibraryFile)
    }
}

fun getLibraryFilePathFor(targetMachine: TargetMachine): String {
    val os = targetMachine.operatingSystemFamily

    return when {
        os.isWindows -> {
            val architecture = targetMachine.architecture
            when {
                architecture.is32Bit -> "lib/x86/discord_game_sdk.dll"
                architecture.is64Bit -> "lib/x86_64/discord_game_sdk.dll"
                else -> throw GradleException("Unknown architecture'${architecture}'.")
            }
        }
        os.isLinux -> "lib/x86_64/discord_game_sdk.so"
        os.isMacOs -> "lib/x86_64/discord_game_sdk.dylib"
        else -> throw GradleException("Unknown operating system family '${os}'.")
    }
}
