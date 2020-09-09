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

rootProject.name = "JetBrains-Discord-Integration"

include("icons")
include("plugin")
include("uploader")
include("bot")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://palantir.bintray.com/releases")
    }

    val properties = java.util.Properties().apply {
        this.load(java.nio.file.Files.newBufferedReader(settingsDir.toPath().resolve("gradle.properties")))
    }

    val versionExactDependencies: String by properties
    val versionGitVersions: String by properties
    val versionIntelliJ: String by properties
    val versionShadow: String by properties
    val versionVersions: String by properties
    val versionKotlin: String by properties

    plugins {
        kotlin("jvm") version versionKotlin
        id("com.github.ben-manes.versions") version versionVersions
        id("org.jetbrains.intellij") version versionIntelliJ
        id("com.github.johnrengelman.shadow") version versionShadow
        id("com.palantir.git-version") version versionGitVersions
        id("com.palantir.baseline-exact-dependencies") version versionExactDependencies
    }
}
