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

import java.nio.file.Files
import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

val properties = Properties().apply {
    this.load(Files.newBufferedReader(rootDir.toPath().resolve("../gradle.properties")))
}

dependencies {
    val versionAsm: String by properties
    val versionCommonsIo: String by properties
    val versionDockerJava: String by properties
    val versionGuava: String by properties
    val versionKotlin: String by properties
    val versionPngtastic: String by properties
    val versionShadow: String by properties
    val versionZeroAllocationHashing: String by properties

    implementation(group = "gradle.plugin.com.github.jengelman.gradle.plugins", name = "shadow", version = versionShadow)

    implementation(group = "com.github.depsypher", name = "pngtastic", version = versionPngtastic)

    implementation(group = "net.openhft", name = "zero-allocation-hashing", version = versionZeroAllocationHashing)

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(group = "com.github.docker-java", name = "docker-java", version = versionDockerJava)

    // Because docker-java includes an ancient version of guava
    implementation(group = "com.google.guava", name = "guava", version = versionGuava)

    implementation(group = "org.ow2.asm", name = "asm", version = versionAsm)
    implementation(group = "org.ow2.asm", name = "asm-tree", version = versionAsm)

    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-metadata-jvm", version = "0.2.0")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = versionKotlin)
}

gradlePlugin {
    plugins {
        create("DockerPlugin") {
            id = "docker"
            implementationClass = "DockerPlugin"
        }

        create("FileIndicesPlugin") {
            id = "fileIndices"
            implementationClass = "FileIndicesPlugin"
        }

        create("GenerateJniHeadersPlugin") {
            id = "jniHeaders"
            implementationClass = "GenerateJniHeadersPlugin"
        }
    }
}
