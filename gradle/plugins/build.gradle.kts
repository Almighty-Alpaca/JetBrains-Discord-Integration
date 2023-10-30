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

plugins {
    `kotlin-dsl`
    alias(libs.plugins.versions)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
//    implementation(libs.gradle.shadow)
    implementation("com.github.johnrengelman:shadow:8.1.0") // TODO
    implementation("org.apache.ant:ant:1.10.14")

    implementation(libs.pngtastic)
    implementation(libs.zeroAllocationHashing)

    implementation(libs.commons.io)
    implementation(libs.commons.lang)

    implementation(libs.docker)
    // Because docker-java includes an ancient version of guava
    implementation(libs.guava)
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

        create("BuildUtils") {
            id = "buildUtils"
            implementationClass = "BuildUtils"
        }
    }
}
