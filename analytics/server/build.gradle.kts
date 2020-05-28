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
    id("com.palantir.baseline-exact-dependencies")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    val versionLogback: String by project

    implementation(project(":analytics:model"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(ktor("server-netty"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)
    implementation(ktor("server-core"))
    implementation(ktor("locations"))
    implementation(ktor("auth"))
    implementation(ktor("serialization"))

    testImplementation(ktor("server-tests"))
}

fun ktor(module: String): Any = "io.ktor:ktor-$module:${project.property("versionKtor")}"
