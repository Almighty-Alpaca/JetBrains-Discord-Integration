import nu.studer.gradle.jooq.JooqEdition

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
    id("nu.studer.jooq")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    val versionDatasourceProxy: String by project
    val versionHikariCp: String by project
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

    // implementation(group = "com.impossibl.pgjdbc-ng", name = "pgjdbc-ng", version = versionPgjdbc)

    implementation(group = "org.postgresql", name = "postgresql", version = versionPostgres)

    implementation(group = "net.ttddyy", name = "datasource-proxy", version = versionDatasourceProxy)

    implementation(group = "com.zaxxer", name = "HikariCP", version = versionHikariCp)

    implementation(jooq())
    implementation(jooq(module = "meta"))
    implementation(jooq(module = "codegen"))
    // Not yet released
    // implementation(jooq("kotlin"))

    implementation(koin("ktor", version = versionKoin))
    implementation(koin("logger-slf4j", version = versionKoin))

    testImplementation(ktor(module = "server-tests"))

    jooqRuntime(group = "org.postgresql", name = "postgresql", version = versionPostgres)
    jooqRuntime(jooq("meta-extensions"))
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

            generate {
                // TODO: customize generation
            }
        }
    }
}
