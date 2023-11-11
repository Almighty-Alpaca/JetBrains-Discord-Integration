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
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.*
import okhttp3.*
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URL
import java.util.Collections
import java.util.concurrent.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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
                cache(Cache(File("build/cache/uploader/discord"), 1024L * 1024L * 1024L)) // 1 GiB
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

                    val changes = calculateChanges(client, source, theme)


                    val handler = CoroutineExceptionHandler { _, throwable ->
                        throwable.printStackTrace()
                    }

                    supervisorScope {
                        withContext(handler) {
                            for (change in changes) {
                                if (change is DiscordChange.Delete) {
                                    launch { deleteIcon(client, change.appId, change.iconId) }
                                }
                            }
                        }

                        supervisorScope {
                            withContext(handler) {
                                for (change in changes) {
                                    if (change is DiscordChange.Override) {
                                        launch {
                                            deleteIcon(client, change.appId, change.iconId)
                                            createIcon(client, change.appId, change.name, source, change.path)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    supervisorScope {
                        withContext(handler) {
                            for (change in changes) {
                                if (change is DiscordChange.Create) {
                                    launch { createIcon(client, change.appId, change.name, source, change.source) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
private suspend fun createIcon(client: HttpClient, appId: Long, name: String, source: ClasspathSource, path: String) = withContext(Dispatchers.IO) {
    client.post {
        url(URL("https://discordapp.com/api/v9/oauth2/applications/$appId/assets"))

        val data = JsonNodeFactory(false).objectNode().apply {
            put("image", "data:image/png;base64," + Base64.encode(source.loadResource(path)!!.readAllBytes()))
            put("name", name)
            put("type", 1)
        }

        setBody(TextContent(ObjectMapper().writeValueAsString(data), ContentType.Application.Json))
    }
}

private suspend fun deleteIcon(client: HttpClient, appId: Long, iconId: Long) = withContext(Dispatchers.IO) {
    try {
        client.delete {
            url(URL("https://discordapp.com/api/v9/oauth2/applications/$appId/assets/$iconId"))
        }
    } catch (e: ClientRequestException) {
        if (e.response.status.value == 404) {
            println("Icon was probably already deleted")
        } else {
            throw e
        }
    }
}

private sealed class DiscordChange {
    class Create(val appId: Long, val source: String, val name: String) : DiscordChange()
    class Delete(val appId: Long, val iconId: Long) : DiscordChange()
    class Override(val appId: Long, val iconId: Long, val source: ClasspathSource, val path: String, val name: String) : DiscordChange()
}

private suspend fun calculateChanges(client: HttpClient, source: ClasspathSource, theme: Theme) = withContext(Dispatchers.IO) method@{
    supervisorScope {
        val changes = Collections.newSetFromMap<DiscordChange>(ConcurrentHashMap())

        for ((applicationName, applicationId) in theme.applications) {
            if (applicationName.uppercase() == applicationName) { // skip old application code based entries
                continue
            }

            println("Starting with ${theme.name} ($applicationName)")

            val local = async { getClasspathIcons(source, applicationName, theme.id) }
            val discord = async { getDiscordIcons(client, applicationId) }

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
                            if (!contentEquals(client, source, local.await()[icon], getAssetUrl(applicationId, discord.await()[icon]))) {
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

private suspend fun contentEquals(client: HttpClient, source: ClasspathSource, local: String?, remote: URL) = withContext(Dispatchers.IO) method@{
    if (local == null)
        return@method false

    val response: HttpResponse = client.get {
        url(remote)
    }

    try {
        IOUtils.contentEquals(response.bodyAsChannel().toInputStream(), source.loadResource(local))
    } catch (e: Exception) {
        println("Error comparing $local with $remote")
        e.printStackTrace()

        return@method true
    }
}

private fun getAssetUrl(appId: Long, iconId: Long?) = URL("https://cdn.discordapp.com/app-assets/$appId/$iconId.png?size=1024")

private suspend fun getClasspathIcons(source: ClasspathSource, appCode: String, theme: String) = withContext(Dispatchers.IO) method@{
    return@method buildMap {
        var application = "${source.pathApplications}/$theme/$appCode.png"
        if (!source.checkResourceExists(application)) {
            application = "${source.pathApplications}/$appCode.png"
        }
        put("application", application)

        source.listResources("${source.pathThemes}/$theme", ".png").forEach { path ->
            put(FilenameUtils.getBaseName(path), path)
        }
    }
}

private suspend fun getDiscordIcons(client: HttpClient, appId: Long) = withContext(Dispatchers.IO) method@{
    val response = client.get {
        url(URL("https://discord.com/api/v9/oauth2/applications/$appId/assets"))
    }

    val array = ObjectMapper().readTree(response.readBytes()) as ArrayNode

    return@method buildMap {
        for (i in 0..<array.size()) {
            val node = (array[i] as ObjectNode)
            put(node["name"].asText(), node["id"].asLong())
        }
    }
}
