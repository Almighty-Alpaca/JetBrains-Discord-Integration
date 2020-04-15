/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.icons.uploader

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ResponseException
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

@KtorExperimentalAPI
@InternalAPI
suspend fun main() {
    val parallelism = 15

    val connectionPool = ConnectionPool(parallelism, 30, TimeUnit.SECONDS)
    val threadPool = Executors.newCachedThreadPool { run ->
        val thread = Thread(run)

        thread.isDaemon = true

        return@newCachedThreadPool thread
    }

    val user = "almightyalpaca"
    val repository = "JetBrains-Discord-Integration"
    val `package` = "Icons"

    val key = System.getenv("BINTRAY_KEY")!!

    val version = "Main"

    HttpClient(OkHttp) {
        engine {
            config {
                cache(Cache(File("build/cache/uploader/bintray"), 1024L * 1024L * 1024L)) // 1GiB
                connectionPool(connectionPool)
                dispatcher(Dispatcher(threadPool).apply {
                    maxRequests = parallelism
                    maxRequestsPerHost = parallelism
                })

                callTimeout(10, TimeUnit.SECONDS)
                connectTimeout(10, TimeUnit.SECONDS)
                readTimeout(10, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
            }
        }
        install(Auth) {
            basic {
                username = user
                password = key
            }
        }
    }.use { client ->
        runBlocking {

            client.createVersion(user, repository, `package`, version)

            val files = getLocalFiles()

            supervisorScope {
                for ((path, name) in files) {
                    launch {
                        client.uploadFile(user, repository, `package`, version, name, path)
                    }
                }
            }
        }
    }
}

private suspend fun HttpClient.createVersion(user: String, repository: String, `package`: String, version: String) {
    try {
        post<Unit> {
            url("https://bintray.com/api/v1/packages/$user/$repository/$`package`/versions")

            val data = JsonNodeFactory(false).objectNode().apply {
                put("name", version)
                put("desc", "Version $version")
            }

            body = TextContent(ObjectMapper().writeValueAsString(data), ContentType.Application.Json)
        }
    } catch (e: ResponseException) {
        if (e.response.status.value != 409)
            throw e
    }
}

private suspend fun HttpClient.uploadFile(user: String, repository: String, `package`: String, version: String, target: String, source: Path) {
    put<HttpResponse> request@{
        url("https://bintray.com/api/v1/content/$user/$repository/$`package`/$version/$`package`/$version/$target")
        parameter("publish", 1)
        parameter("override", 1)

        body = object : OutgoingContent.ReadChannelContent() {
            override fun readFrom(): ByteReadChannel {
                val bytes = Files.readAllBytes(source)

                // Bintay has problems with small files so we just append some whitespace
                return if (bytes.size < 32) {
                    val buffer = ByteBuffer.allocate(32)

                    buffer.put(bytes)
                    while (buffer.position() < 32) {
                        buffer.put(' '.toByte())
                    }
                    buffer.flip()

                    ByteReadChannel(buffer)
                } else {
                    ByteReadChannel(bytes)
                }
            }
        }
    }
}

private fun getLocalFiles(): Set<Pair<Path, String>> {
    val base = Paths.get(".")

    val languages = Files.list(base.resolve("languages"))
    val themes = Files.list(base.resolve("themes"))

    return Stream.concat(languages, themes)
        .filter { p -> Files.isRegularFile(p) }
        .filter { p -> p.name.endsWith(".yaml") }
        .map { p -> p to FilenameUtils.separatorsToUnix(base.relativize(p).toString()) }
        .toSet()
}
