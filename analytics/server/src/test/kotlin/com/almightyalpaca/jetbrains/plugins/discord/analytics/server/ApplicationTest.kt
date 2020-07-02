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
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.routes.main
import com.almightyalpaca.jetbrains.plugins.discord.analytics.server.services.Configuration
import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.ExpectedDataSet
import com.github.database.rider.core.api.exporter.ExportDataSet
import com.github.database.rider.junit5.api.DBRider
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.images.PullPolicy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

class KPostgreSQLContainer(dockerImageName: String?) : PostgreSQLContainer<KPostgreSQLContainer>(dockerImageName)

private val databaseImage = "postgres:12"
private val databaseUser = "postgres"
private val databasePassword = "12345678"
private val databaseName = "analytics"

private val token = "abcdefghijklmnopqrstuvwxyz"

@Testcontainers
@DBRider
@DBUnit(cacheConnection = false, caseSensitiveTableNames = true)
class ApplicationTest {
    @Container
    val postgres: KPostgreSQLContainer = KPostgreSQLContainer(databaseImage)
        .withImagePullPolicy(PullPolicy.alwaysPull())
        .withUsername(databaseUser)
        .withPassword(databasePassword)
        .withDatabaseName(databaseName)

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

    private lateinit var connection: Connection

    @Suppress("unused")
    private val connectionHolder = ConnectionHolder { connection }

    @BeforeEach
    fun setUpConnection() {
        connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword)
    }

    @AfterEach
    fun tearDownConnection() {
        connection.close()
    }

    @Test
    @ExpectedDataSet("empty.yml")
    //@ExportDataSet(
    //    outputName = "src/test/resources/datasets/empty.yml",
    //    includeTables = ["application_codes", "application_names", "editors", "extensions", "file_stats", "icon_stats", "icons", "languages", "themes", "types", "version_stats", "versions"]
    //)
    fun `correct test endpoint response`(): Unit = withTestApplication({ main(config) }) {
        val call = handleRequest(HttpMethod.Get, "/test")

        assertEquals(true, call.requestHandled)

        with(call.response) {
            assertEquals(HttpStatusCode.OK, status())
            assertEquals("Hello World!", content)
        }
    }

    @Test
    @ExpectedDataSet("files.yml")
    //@ExportDataSet(
    //    outputName = "src/test/resources/datasets/files.yml",
    //    includeTables = ["application_codes", "application_names", "editors", "extensions", "file_stats", "icon_stats", "icons", "languages", "themes", "types", "version_stats", "versions"]
    //)
    fun `receive and store valid files`(): Unit = withTestApplication({ main(config) }) {
        val json = Json(JsonConfiguration.Stable)

        val testObject = Analytics(
            listOf(
                Analytics.File(
                    LocalDateTime.parse("2020-07-01T21:51:09.015229800"),
                    "com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl",
                    "kotlin",
                    ".kt",
                    "kotlin/0"
                ),
                Analytics.File(
                    LocalDateTime.parse("2020-07-02T21:51:09.015229800"),
                    "com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl",
                    "JavaScript",
                    ".js",
                    "javascript"
                )
            ),
            listOf(),
            null
        )

        val call = handleRequest(HttpMethod.Post, "api/v1/analytics") {
            addHeader(HttpHeaders.Authorization, token)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.stringify(Analytics.serializer(), testObject))
        }

        assertEquals(true, call.requestHandled)

        with(call.response) {
            assertEquals(HttpStatusCode.OK, status())
        }
    }

    @Test
    @ExpectedDataSet("icons.yml")
    //@ExportDataSet(
    //    outputName = "src/test/resources/datasets/icons.yml",
    //    includeTables = ["application_codes", "application_names", "editors", "extensions", "file_stats", "icon_stats", "icons", "languages", "themes", "types", "version_stats", "versions"]
    //)
    fun `receive and store valid icons`(): Unit = withTestApplication({ main(config) }) {
        val json = Json(JsonConfiguration.Stable)

        val testObject = Analytics(
            listOf(),
            listOf(
                Analytics.Icon(
                    LocalDateTime.parse("2020-07-01T21:51:09.015229800"),
                    "kotlin/0",
                    "classic",
                    "intellij_idea_community",
                    "kotlin",
                    "kotlin"
                ),
                Analytics.Icon(
                    LocalDateTime.parse("2020-07-02T21:32:09.015229800"),
                    "react/0",
                    "classic",
                    "intellij_idea_community",
                    "reactts",
                    "typescript"
                )
            ),
            null
        )

        val call = handleRequest(HttpMethod.Post, "api/v1/analytics") {
            addHeader(HttpHeaders.Authorization, token)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.stringify(Analytics.serializer(), testObject))
        }

        assertEquals(true, call.requestHandled)

        with(call.response) {
            assertEquals(HttpStatusCode.OK, status())
        }
    }

    @Test
    @ExpectedDataSet("versions.yml")
    //@ExportDataSet(
    //    outputName = "src/test/resources/datasets/versions.yml",
    //    includeTables = ["application_codes", "application_names", "editors", "extensions", "file_stats", "icon_stats", "icons", "languages", "themes", "types", "version_stats", "versions"]
    //)
    fun `receive and store valid version`(): Unit = withTestApplication({ main(config) }) {
        val json = Json(JsonConfiguration.Stable)

        val testObject = Analytics(
            listOf(),
            listOf(),
            Analytics.Version(
                LocalDateTime.parse("2020-07-02T21:51:09.015229800"),
                "1.4.2",
                "IC"
            )
        )

        val call = handleRequest(HttpMethod.Post, "api/v1/analytics") {
            addHeader(HttpHeaders.Authorization, token)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(json.stringify(Analytics.serializer(), testObject))
        }

        assertEquals(true, call.requestHandled)

        with(call.response) {
            assertEquals(HttpStatusCode.OK, status())
        }
    }
}
