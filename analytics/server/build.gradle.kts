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

import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqTask

plugins {
    application
    kotlin("jvm")
    id("com.palantir.baseline-exact-dependencies")
    id("nu.studer.jooq")
}

application {
    mainClassName = "com.almightyalpaca.jetbrains.plugins.discord.analytics.server.ApplicationKt"
}

repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://kotlin.bintray.com/ktor")
}

dependencies {
    val versionClikt: String by project
    val versionDatasourceProxy: String by project
    val versionHikariCp: String by project
    val versionHoplite: String by project
    val versionKoin: String by project
    val versionKtor: String by project
    val versionLogback: String by project
    val versionPostgres: String by project

    implementation(project(":analytics:interface"))

    implementation(kotlin(module = "stdlib-jdk8"))

    implementation(platform(ktor(module = "bom", version = versionKtor)))
    implementation(ktor(module = "server-netty"))
    implementation(ktor(module = "server-core"))
    implementation(ktor(module = "locations"))
    implementation(ktor(module = "auth"))
    implementation(ktor(module = "serialization"))

    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)

    implementation(group = "org.postgresql", name = "postgresql", version = versionPostgres)

    implementation(group = "net.ttddyy", name = "datasource-proxy", version = versionDatasourceProxy)

    implementation(group = "com.zaxxer", name = "HikariCP", version = versionHikariCp)

    implementation(jooq())
    implementation(jooq(module = "meta"))
    // Not yet released
    // implementation(jooq("kotlin"))

    implementation(koin(module = "ktor", version = versionKoin))
    implementation(koin(module = "logger-slf4j", version = versionKoin))

    implementation(hoplite(module = "core", version = versionHoplite))
    implementation(hoplite(module = "yaml", version = versionHoplite))
    implementation(hoplite(module = "hocon", version = versionHoplite))
    implementation(hoplite(module = "ktor", version = versionHoplite))

    implementation(group = "com.github.ajalt", name = "clikt", version = versionClikt)

    testImplementation(ktor(module = "server-tests"))

    jooqRuntime(jooq(module = "meta-extensions"))
    jooqRuntime(project(":analytics:server:jooq"))
}

jooq {
    val versionJooq: String by project

    version = versionJooq
    edition = JooqEdition.OSS

    generateSchemaSourceOnCompilation = true

    "database"(sourceSets.main.get()) {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"

                properties {
                    property(key = "scripts", value = "schema.sql")
                    property(key = "sort", value = "semantic")
                    property(key = "unqualifiedSchema", value = "none")
                    property(key = "defaultNameCase", value = "lower")
                }
            }

            target {
                packageName = "com.almightyalpaca.jetbrains.plugins.discord.analytics.server.database.generated"
            }

            strategy {
                name = "com.almightyalpaca.jetbrains.plugins.discord.analytics.server.jooq.codegen.SingularNameGeneratorStrategy"
            }

            generate {
                // TODO: customize generation
            }
        }
    }
}

tasks {
    "generateDatabaseJooqSchemaSource"(JooqTask::class) {
        inputs.apply {
            // property("javaExecSpec", javaExecSpec)
            property("normalizedConfiguration", normalizedConfiguration)
            normalizedConfiguration.generator.database.properties.find { it.key == "scripts" }?.value?.let { file(it) }
            dir(normalizedConfiguration.generator.target.directory)
            files(jooqClasspath)
        }

        outputs.apply {
            dir(outputDirectory)
        }

        outputs.cacheIf { true }
    }
}
