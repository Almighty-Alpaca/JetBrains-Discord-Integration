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
    application

    alias(libs.plugins.kotlin)
    com.github.johnrengelman.shadow

    docker
}

docker {
    "bot" {
        tag = "almightyalpaca/jetbrains-discord-integration-bot"
        devContainerName = "${project.group}.dev"
        buildContainerName = "${project.group}.builder"
    }
}

application {
    mainClass("com.almightyalpaca.jetbrains.plugins.discord.bot.MainKt")
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    // Kotlin standard library
    implementation(libs.kotlin.stdlib)

    // implementation script engine
    implementation(libs.kotlin.script.util)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlin.scripting.compiler.embeddable)
    implementation(libs.kotlin.script.runtime)

    // JDA (without audio)
    implementation(libs.jda) {
        exclude(group = "club.minnced", module = "opus-java")
    }

    // Konf
    implementation(libs.konf.core)
    implementation(libs.konf.yaml)

    // Logback Classic
    implementation(libs.logback)
}
