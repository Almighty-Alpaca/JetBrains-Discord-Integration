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
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("docker")
}

docker {
    "bot" {
        tag = "almightyalpaca/jetbrains-discord-integration-bot"
        devContainerName = "${project.group}.dev"
        buildContainerName = "${project.group}.builder"
    }
}

application {
    mainClass.set("com.almightyalpaca.jetbrains.plugins.discord.bot.MainKt")
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    val versionJda: String by project
    val versionOkHttp: String by project
//    val versionJdaUtilities: String by project
    val versionKonf: String by project
    val versionLogback: String by project

    // Kotlin BOM
    implementation(platform(kotlin(module = "bom")))

    // Kotlin standard library
    implementation(kotlin(module = "stdlib"))

    // implementation script engine
    implementation(kotlin(module = "script-util"))
    implementation(kotlin(module = "compiler-embeddable"))
    implementation(kotlin(module = "scripting-compiler-embeddable"))

    // JDA (without audio)
    implementation(group = "net.dv8tion", name = "JDA", version = versionJda) {
        exclude(group = "club.minnced", module = "opus-java")
    }

    // JDA-Utilities
//    implementation(group = "com.jagrosh", name = "jda-utilities-command", version = versionJdaUtilities)

    // Konf (dependencies for unused formats removed to reduce size)
    implementation(group = "com.uchuhimo", name = "konf", version = versionKonf) {
        exclude(group = "com.moandjiezana.toml", module = "toml4j")
        exclude(group = "org.dom4j", module = "dom4j")
        exclude(group = "org.eclipse.jgit", module = "org.eclipse.jgit")

        // TODO: exclude more modules from Konf
        // exclude(group = "", module = "")
    }

    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = versionOkHttp)

    // Logback Classic
    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)
}
