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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server

import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.routes.main
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Configuration
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

class KPostgreSQLContainer(dockerImageName: String?) : PostgreSQLContainer<KPostgreSQLContainer>(dockerImageName)

@Testcontainers
class ApplicationTest {
    private val databaseImage = "timescale/timescaledb:latest-pg12"
    private val databaseUser = "postgres"
    private val databasePassword = "12345678"
    private val databaseName = "analytics"

    @Container
    val postgres: KPostgreSQLContainer = KPostgreSQLContainer(databaseImage)
        .withUsername(databaseUser)
        .withPassword(databasePassword)
        .withDatabaseName(databaseName)

    val config: Configuration
        get() {
            val databaseUrl = "jdbc:pgsql://${postgres.host}:${postgres.firstMappedPort}/$databaseName"

            val database = Configuration.Database(databaseUrl, databaseUser, databasePassword)

            val deployment = Configuration.Ktor.Deployment(null, 8080, null, null, null)

            val ktor = Configuration.Ktor(null, deployment, null)

            return Configuration(database, ktor, null)
        }

    @Test
    fun testRoot(): Unit = withTestApplication({ main(config) }) {
        with(handleRequest(HttpMethod.Get, "/test")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello World!", response.content)
        }
    }
}
