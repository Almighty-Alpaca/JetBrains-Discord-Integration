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
    jcenter()
}

val properties = Properties().apply {
    this.load(Files.newBufferedReader(rootDir.toPath().resolve("../gradle.properties")))
}

dependencies {
    val versionCommonsIo: String by properties
    val versionGuava: String by properties
    val versionJooq: String by properties
    val versionJooqGradle: String by properties
    val versionPngtastic: String by properties
    val versionShadow: String by properties
    val versionZeroAllocationHashing: String by properties

    implementation(group = "com.github.jengelman.gradle.plugins", name = "shadow", version = versionShadow)

    implementation(group = "com.github.depsypher", name = "pngtastic", version = versionPngtastic)
    implementation(group = "net.openhft", name = "zero-allocation-hashing", version = versionZeroAllocationHashing)

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(group = "nu.studer", name = "gradle-jooq-plugin", version = versionJooqGradle)

    implementation(group = "org.jooq", name = "jooq-codegen", version = versionJooq)

    implementation(group = "com.google.guava", name = "guava", version = versionGuava)
}

gradlePlugin {
    plugins {
        register("Docker Plugin") {
            id = "docker"
            implementationClass = "com.almightyalpaca.jetbrains.plugins.discord.gradle.DockerPlugin"
        }

        register("Docker-Compose Plugin") {
            id = "docker-compose"
            implementationClass = "com.almightyalpaca.jetbrains.plugins.discord.gradle.DockerComposePlugin"
        }

        register("Secrets Plugin") {
            id = "secrets"
            implementationClass = "com.almightyalpaca.jetbrains.plugins.discord.gradle.SecretsPlugin"
        }

        create("File Indices Plugin") {
            id = "fileIndices"
            implementationClass = "com.almightyalpaca.jetbrains.plugins.discord.gradle.FileIndicesPlugin"
        }
    }
}
