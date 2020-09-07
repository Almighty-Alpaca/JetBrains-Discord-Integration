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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.uploader

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.mapWith
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN")!!

    val parallelism = 15

    val connectionPool = ConnectionPool(parallelism, 30, TimeUnit.SECONDS)
    val threadPool = Executors.newCachedThreadPool { run ->
        val thread = Thread(run)

        thread.isDaemon = true

        return@newCachedThreadPool thread
    }

    HttpClient(OkHttp) {
        engine {
            config {
                cache(Cache(File("build/cache/uploader/discord"), 1024L * 1024L * 1024L)) // 1GiB
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
        install(UserAgent)
        defaultRequest {
            headers["authorization"] = token
        }
    }.use { client ->
        runBlocking {
            withContext(Dispatchers.IO) {
                val source = ClasspathSource("discord", retry = false)
                val themes = source.getThemes()

                for (theme in themes.values) {
                    println("Starting with ${theme.name}")

                    val changes = calculateChangesAsync(client, source, theme)

                    supervisorScope {
                        for (change in changes.await()) {
                            if (change is DiscordChange.Delete) {
                                deleteIcon(client, change.appId, change.iconId)
                            }
                        }
                    }

                    supervisorScope {
                        for (change in changes.await()) {
                            if (change is DiscordChange.Override) {
                                coroutineScope {
                                    deleteIcon(client, change.appId, change.iconId)
                                    createIcon(client, change.appId, change.name, source, change.path)
                                }
                            }
                        }
                    }

                    supervisorScope {
                        for (change in changes.await()) {
                            if (change is DiscordChange.Create) {
                                createIcon(client, change.appId, change.name, source, change.source)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun CoroutineScope.createIcon(client: HttpClient, appId: Long, name: String, source: ClasspathSource, path: String) = launch {
    client.post<Unit> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets"))

        val data = JsonNodeFactory(false).objectNode().apply {
            put("image", "data:image/png;base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(source.loadResource(path)!!)))
            put("name", name)
            put("type", 1)
        }

        body = TextContent(ObjectMapper().writeValueAsString(data), ContentType.Application.Json)
    }
}

private fun CoroutineScope.deleteIcon(client: HttpClient, appId: Long, iconId: Long) = launch(Dispatchers.IO) {
    client.delete<Unit> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets/$iconId"))
    }
}

private sealed class DiscordChange {
    class Create(val appId: Long, val source: String, val name: String) : DiscordChange()
    class Delete(val appId: Long, val iconId: Long) : DiscordChange()
    class Override(val appId: Long, val iconId: Long, val source: ClasspathSource, val path: String, val name: String) : DiscordChange()
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.calculateChangesAsync(client: HttpClient, source: ClasspathSource, theme: Theme) = async(Dispatchers.IO) {
    supervisorScope {
        val changes = Collections.newSetFromMap<DiscordChange>(ConcurrentHashMap())

        for ((applicationName, applicationId) in theme.applications) {
            if (applicationName.toUpperCase() == applicationName) { // skip old application code based entries
                continue
            }

            println("Starting with ${theme.name} ($applicationName)")

            val local = getClasspathIconsAsync(source, applicationName, theme.id)
            val discord = getDiscordIconsAsync(client, applicationId)

            val all = local.await().keys + discord.await().keys

            for (icon in all) {
                // println("Comparing ${theme.id}/$icon ($applicationName)")
                when (icon) {
                    !in discord.await() -> {
                        println("Create ${theme.id}/$icon ($applicationName)")
                        changes += DiscordChange.Create(applicationId, local.await().getValue(icon), icon)
                    }
                    !in local.await() -> {
                        println("Delete ${theme.id}/$icon ($applicationName)")
                        changes += DiscordChange.Delete(applicationId, discord.await().getValue(icon))
                    }
                    else -> {
                        launch {
                            if (!contentEqualsAsync(client, source, local.await()[icon], getAssetUrl(applicationId, discord.await()[icon])).await()) {
                                println("Override ${theme.id}/$icon ($applicationName)")
                                changes += DiscordChange.Override(applicationId, discord.await().getValue(icon), source, local.await().getValue(icon), icon)
                            } else {
                                // println("Nothing ${theme.id}/$icon ($applicationName)")
                            }
                        }
                    }
                }
            }
        }

        changes
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalCoroutinesApi
private fun CoroutineScope.contentEqualsAsync(client: HttpClient, source: ClasspathSource, local: String?, remote: URL) = async(Dispatchers.IO) {
    if (local == null)
        return@async false

    val response = client.get<HttpResponse> {
        url(remote)
    }

    try {
        IOUtils.contentEquals(response.content.toInputStream(), source.loadResource(local))
    } catch (e: Exception) {
        println("Error comparing $local with $remote")
        e.printStackTrace()

        true
    }
}

private fun getAssetUrl(appId: Long, iconId: Long?) = URL("https://cdn.discordapp.com/app-assets/$appId/$iconId.png?size=1024")

private fun CoroutineScope.getClasspathIconsAsync(source: ClasspathSource, appCode: String, theme: String) = async(Dispatchers.IO) {
    var application = "${source.pathApplications}/$theme/$appCode.png"
    if (!source.checkResourceExists(application)) {
        application = "${source.pathApplications}/$appCode.png"
    }

    val applicationStream = sequenceOf("application" to application)

    val iconStream = source.listResources("${source.pathThemes}/$theme", ".png")
        .map { p -> FilenameUtils.getBaseName(p) to p }

    (applicationStream + iconStream).toMap()
}

private fun CoroutineScope.getDiscordIconsAsync(client: HttpClient, appId: Long) = async(Dispatchers.IO) {
    val response = client.get<HttpResponse> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets"))
    }

    val array = ObjectMapper().readTree(response.readBytes()) as ArrayNode

    mapWith(array.size()) { i ->
        val node = (array[i] as ObjectNode)
        node["name"].asText() to node["id"].asLong()
    }
}
