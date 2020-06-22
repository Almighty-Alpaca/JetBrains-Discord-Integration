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

import com.almightyalpaca.jetbrains.plugins.discord.gradle.*
import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqTask

plugins {
    application
    kotlin("jvm")
    id("com.palantir.baseline-exact-dependencies")
    nu.studer.jooq
    docker
    `docker-compose`
    secrets
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
    val versionFlyway: String by project
    val versionHikariCp: String by project
    val versionHoplite: String by project
    val versionJunit: String by project
    val versionKoin: String by project
    val versionKtor: String by project
    val versionLogback: String by project
    val versionPgjdbcNg: String by project
    val versionTestcontainers: String by project

    implementation(project(":analytics:interface"))

    implementation(kotlin(module = "stdlib-jdk8"))

    implementation(platform(ktor(module = "bom", version = versionKtor)))
    implementation(ktor(module = "server-netty"))
    implementation(ktor(module = "server-core"))
    implementation(ktor(module = "locations"))
    implementation(ktor(module = "auth"))
    implementation(ktor(module = "serialization"))

    implementation(group = "ch.qos.logback", name = "logback-classic", version = versionLogback)

    implementation(group = "com.impossibl.pgjdbc-ng", name = "pgjdbc-ng", version = versionPgjdbcNg)

    implementation(group = "net.ttddyy", name = "datasource-proxy", version = versionDatasourceProxy)

    implementation(group = "com.zaxxer", name = "HikariCP", version = versionHikariCp)

    implementation(group = "org.flywaydb", name = "flyway-core", version = versionFlyway)

    implementation(jooq())
    implementation(jooq(module = "meta"))
    // TODO: Add once released
    // implementation(jooq("kotlin"))

    implementation(koin(module = "ktor", version = versionKoin))
    implementation(koin(module = "logger-slf4j", version = versionKoin))

    implementation(hoplite(module = "core", version = versionHoplite))
    implementation(hoplite(module = "hocon", version = versionHoplite))
    implementation(hoplite(module = "yaml", version = versionHoplite))
    implementation(hoplite(module = "ktor", version = versionHoplite))

    implementation(group = "com.github.ajalt", name = "clikt", version = versionClikt)

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = versionJunit)

    testImplementation(ktor(module = "server-test-host"))

    testImplementation(platform(testcontainers(module = "testcontainers-bom", version = versionTestcontainers)))
    testImplementation(testcontainers(module = "junit-jupiter")) {
        exclude(group = "junit", module = "junit")
    }
    testImplementation(testcontainers(module = "postgresql")) {
        exclude(group = "junit", module = "junit")
    }

    jooqRuntime(jooq(module = "meta-extensions"))
    jooqRuntime(project(":analytics:server:jooq"))
}

docker {
    "server" {
        tag = "almightyalpaca/jetbrains-discord-integration-analytics-server"
        devContainerName = "${project.group}.dev"
        buildContainerName = "${rootProject.group}.docker.builder"

        mount("type=bind,source=${project.file("application.conf").absolutePath},target=/config/application.conf")
    }
}

dockerCompose {
    "server" {
        projectContainer = "server"

        val dockerImage = docker["server"]!!

        val properties: Map<String, String> = mutableMapOf<String, String>().apply {
            put("dockerImage", dockerImage.tag)

            secrets.server.database.user?.let { user -> put("databaseUser", user) }
            secrets.server.database.password?.let { password -> put("databasePassword", password) }
            secrets.server.database.name?.let { name ->
                put("databaseName", name)
                put("databaseUrl", "jdbc:pgsql://database/$name")
            }
            secrets.server.authentication.token?.let { token -> put("authenticationToken", token) }
        }

        inputs {
            property("properties", properties)
        }

        copySpec {
            from("application.conf")

            expand(properties)
        }

        copyDependsOn(secrets.checkTask)

        runDependsOn(dockerImage)
    }
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
                    property(key = "scripts", value = "src/main/resources/db/migration/")
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
    val runSystemProperties: Map<String, String> = mutableMapOf<String, String>().apply {
        secrets.server.database.user?.let { user -> put("databaseUser", user) }
        secrets.server.database.password?.let { password -> put("databasePassword", password) }
        val jdbcUrl = when (val name = secrets.server.database.name) {
            null -> when (val url = secrets.server.database.url) {
                null -> "jdbc:pgsql:"
                else -> "jdbc:pgsql://$url/"
            }
            else -> when (val url = secrets.server.database.url) {
                null -> "jdbc:pgsql:$name"
                else -> "jdbc:pgsql://$url/$name"
            }
        }
        put("databaseUrl", jdbcUrl)
        secrets.server.authentication.token?.let { token -> put("authenticationToken", token) }
    }

    "run"(JavaExec::class) {
        dependsOn(secrets.checkTask)

        systemProperties.putAll(runSystemProperties)
    }

    "runShadow"(JavaExec::class) {
        dependsOn(secrets.checkTask)

        systemProperties.putAll(runSystemProperties)
    }

    test {
        useJUnitPlatform()
    }

    withType<JooqTask> {
        inputs.apply {
            property("normalizedConfiguration", normalizedConfiguration)
            files(jooqClasspath)

            if (normalizedConfiguration.generator.database.name == "org.jooq.meta.extensions.ddl.DDLDatabase") {
                normalizedConfiguration.generator.database.properties.find { it.key == "scripts" }?.value?.let { files(it) }
            }
        }

        outputs.apply {
            dir(outputDirectory)
        }

        outputs.cacheIf { true }
    }

    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }
}
