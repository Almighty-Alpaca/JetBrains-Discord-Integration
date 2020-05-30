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
    val versionHikariCp: String by project
    val versionKoin: String by project
    val versionKtor: String by project
    val versionKtorm: String by project
    val versionLogback: String by project
    val versionPgjdbc: String by project

    implementation(project(":analytics:interface"))

    implementation(kotlin("stdlib-jdk8"))

    implementation(platform(ktor("bom", versionKtor)))
    implementation(ktor("server-netty"))
    implementation(ktor("server-core"))
    implementation(ktor("locations"))
    implementation(ktor("auth"))
    implementation(ktor("serialization"))

    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)

    implementation(group = "com.impossibl.pgjdbc-ng", name = "pgjdbc-ng", version = versionPgjdbc)

    implementation(group = "com.zaxxer", name = "HikariCP", version = versionHikariCp)

    implementation(group = "me.liuwj.ktorm", name = "ktorm-core", version = versionKtorm)
    implementation(group = "me.liuwj.ktorm", name = "ktorm-support-postgresql", version = versionKtorm)

    implementation(group = "org.koin", name = "koin-ktor", version = versionKoin)
    implementation(group = "org.koin", name = "koin-logger-slf4j", version = versionKoin)

    testImplementation(ktor("server-tests"))
}

fun ktor(module: String, version: String? = null): Any = "io.ktor:ktor-$module:${version ?: ""}"
