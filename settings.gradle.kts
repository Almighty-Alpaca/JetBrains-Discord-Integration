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

includeBuild("gradle/plugins")

include("icons")
include("plugin")
include("uploader")
include("bot")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
//        maven(url = "https://palantir.bintray.com/releases")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

plugins {
    // Version Catalogs are not supported in settings.gradle.kts
    // alias(libs.plugins.foojay.resolver.convention)
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}

// TODO: use when https://github.com/JetBrains/gradle-intellij-plugin/issues/776 is fixed
//@Suppress("UnstableApiUsage")
//dependencyResolutionManagement {
//    repositoriesMode = RepositoriesMode.PREFER_SETTINGS
//    repositories {
//        mavenCentral()
//        maven("https://jitpack.io")
//    }
//}
