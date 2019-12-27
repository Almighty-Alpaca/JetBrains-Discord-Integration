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

    plugins {
        kotlin("jvm") version "1.3.61"
        id("com.github.ben-manes.versions") version "0.27.0"
        id("org.jetbrains.intellij") version "0.4.15"
        id("com.github.johnrengelman.shadow") version "5.2.0"
        id("com.palantir.git-version") version "0.12.2"
        id("com.palantir.baseline-exact-dependencies") version "2.45.1"
    }
}
