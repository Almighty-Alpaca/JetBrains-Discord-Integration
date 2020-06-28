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

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Analytics
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.database.generated.Tables.FILE_STATS
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.routes.main
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Configuration
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.images.PullPolicy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.OffsetDateTime

class KPostgreSQLContainer(dockerImageName: String?) : PostgreSQLContainer<KPostgreSQLContainer>(dockerImageName)

@Testcontainers
class ApplicationTest {
    private val databaseImage = "timescale/timescaledb:latest-pg12"
    private val databaseUser = "postgres"
    private val databasePassword = "12345678"
    private val databaseName = "analytics"

    private val token = "abcdefghijklmnopqrstuvwxyz"

    @Container
    val postgres: KPostgreSQLContainer = KPostgreSQLContainer(databaseImage)
        .withUsername(databaseUser)
        .withPassword(databasePassword)
        .withDatabaseName(databaseName)
        .withImagePullPolicy(PullPolicy.alwaysPull())

    private val databaseUrl
        get() = "jdbc:pgsql://${postgres.host}:${postgres.firstMappedPort}/$databaseName"

    private val config: Configuration
        get() {
            val database = Configuration.Database(databaseUrl, databaseUser, databasePassword)

            val deployment = Configuration.Ktor.Deployment(null, 8080, null, null, null)

            val ktor = Configuration.Ktor(null, deployment, null)

            val authentication = Configuration.Authentication(setOf(token))

            return Configuration(database, ktor, authentication)
        }

    private val testObject = Analytics(
        listOf(
            Analytics.File(
                OffsetDateTime.now(),
                "IntelliJ Idea",
                "kotlin",
                ".kt",
                "kotlin"
            )
        ),
        listOf(
            Analytics.Icon(
                OffsetDateTime.now(),
                ".http",
                "Atom",
                "IntelliJ Idea",
                "http",
                "fallback"
            )
        ),
        Analytics.Version(
            OffsetDateTime.now(),
            "2020.2.1",
            "IJ"
        )
    )

    @Test
    fun `correct test endpoint response`(): Unit = withTestApplication({ main(config) }) {
        with(handleRequest(HttpMethod.Get, "/test")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello World!", response.content)
        }
    }

    @Test
    fun `receive and store valid analytics data`(): Unit = withTestApplication({ main(config) }) {
        val json = Json(JsonConfiguration.Stable)

        val call = handleRequest(HttpMethod.Post, "api/v1/analytics") {
            addHeader(HttpHeaders.Authorization, token)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.stringify(Analytics.serializer(), testObject))
        }

        with(call) {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        val dsl = DSL.using(databaseUrl, databaseUser, databasePassword)
        println(dsl.select(DSL.asterisk()).from(FILE_STATS).fetch())
    }
}
