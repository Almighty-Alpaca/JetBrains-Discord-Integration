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

plugins {
    kotlin("jvm")
}

dependencies {
    val versionCoroutines: String by project
    val versionCommonsIo: String by project
    val versionJackson: String by project

    implementation(kotlin(module = "stdlib"))

    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:$versionCoroutines"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core")

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(platform("com.fasterxml.jackson:jackson-bom:$versionJackson"))
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml")
}

tasks {
    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }
}
