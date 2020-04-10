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

rootProject.name = "JetBrains-Discord-Integration"

include("plugin")
include("icons")
include("shared")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = java.net.URI("https://palantir.bintray.com/releases")
        }
    }

    val properties = java.util.Properties().apply {
        this.load(java.nio.file.Files.newBufferedReader(settingsDir.toPath().resolve("gradle.properties")))
    }

    val versionKotlin: String by properties
    val versionGradleVersions: String by properties
    val versionGradleIntelliJ: String by properties
    val versionGradleShadow: String by properties
    val versionGradleGitVersions: String by properties
    val versionGradleExactDependencies: String by properties

    plugins {
        kotlin("jvm") version versionKotlin
        id("com.github.ben-manes.versions") version versionGradleVersions
        id("org.jetbrains.intellij") version versionGradleIntelliJ
        id("com.github.johnrengelman.shadow") version versionGradleShadow
        id("com.palantir.git-version") version versionGradleGitVersions
        id("com.palantir.baseline-exact-dependencies") version versionGradleExactDependencies
    }
}
