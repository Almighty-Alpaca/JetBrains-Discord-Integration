package com.almightyalpaca.jetbrains.plugins.discord.icons.uploader

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.Map
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.UserAgent
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readBytes
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.*
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

@ExperimentalCoroutinesApi
suspend fun main() {

    val connectionPool = ConnectionPool(150, 10, TimeUnit.SECONDS)
    val threadPool = Executors.newCachedThreadPool()

    HttpClient(OkHttp) {
        engine {
            config {
                cache(Cache(File("build/cache/uploader/discord"), 1024L * 1024L * 1024L)) // 1GiB
                connectionPool(connectionPool)
                dispatcher(Dispatcher(threadPool).apply {
                    maxRequests = 150
                    maxRequestsPerHost = 150
                })
            }
        }
        install(UserAgent)
    }.use { client ->
        runBlocking {
            val token = System.getenv("DISCORD_TOKEN")!!

            val source = LocalSource(Paths.get("../"))
            val themes = source.getThemes()

            for (theme in themes.values) {
                val changes = calculateChangesAsync(client, theme)

                supervisorScope {
                    for (change in changes.await()) {
                        if (change is DiscordChange.Delete) {
                            deleteIcon(client, token, change.appId, change.iconId)
                        }
                    }
                }

                supervisorScope {
                    for (change in changes.await()) {
                        if (change is DiscordChange.Override) {
                            coroutineScope {
                                deleteIcon(client, token, change.appId, change.iconId)
                                createIcon(client, token, change.appId, change.name, change.source)
                            }
                        }
                    }
                }

                supervisorScope {
                    for (change in changes.await()) {
                        if (change is DiscordChange.Create) {
                            createIcon(client, token, change.appId, change.name, change.source)
                        }
                    }
                }
            }
        }
    }

    threadPool.shutdown() // TODO: remove once https://github.com/square/okhttp/issues/4029 has been fixed
    connectionPool.evictAll()
}

private fun CoroutineScope.createIcon(client: HttpClient, token: String, appId: Long, name: String, source: Path) = launch {
    client.post<Unit> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets"))
        headers["authorization"] = token

        val data = JsonNodeFactory(false).objectNode().apply {
            put("image", "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(source)))
            put("name", name)
            put("type", 1)
        }

        body = TextContent(ObjectMapper().writeValueAsString(data), ContentType.Application.Json)
    }
}

private fun CoroutineScope.deleteIcon(client: HttpClient, token: String, appId: Long, iconId: Long) = launch(Dispatchers.IO) {
    client.delete<Unit> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets/$iconId"))
        headers["authorization"] = token
    }
}

private sealed class DiscordChange {
    class Create(val appId: Long, val source: Path, val name: String) : DiscordChange()
    class Delete(val appId: Long, val iconId: Long) : DiscordChange()
    class Override(val appId: Long, val iconId: Long, val source: Path, val name: String) : DiscordChange()
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.calculateChangesAsync(client: HttpClient, theme: Theme) = async(Dispatchers.IO) {
    supervisorScope {
        val changes = Collections.newSetFromMap<DiscordChange>(ConcurrentHashMap())

        for (application in theme.applications) {
            val appCode = application.key
            val appId = application.value

            val local = getLocalIconsAsync(appCode, theme.id)
            val discord = getDiscordIconsAsync(client, appId)

            val all = local.await().keys + discord.await().keys

            for (icon in all) {
                println("Comparing ${theme.id}/$icon ($appCode)")
                when (icon) {
                    !in discord.await() -> {
                        println("Create ${theme.id}/$icon ($appCode)")
                        changes += DiscordChange.Create(appId, local.await().getValue(icon), icon)
                    }
                    !in local.await() -> {
                        println("Delete ${theme.id}/$icon ($appCode)")
                        changes += DiscordChange.Delete(appId, discord.await().getValue(icon))
                    }
                    else -> {
                        launch {
                            if (!contentEqualsAsync(client, local.await()[icon], getAssetUrl(appId, discord.await()[icon])).await()) {
                                println("Override ${theme.id}/$icon ($appCode)")
                                changes += DiscordChange.Override(appId, discord.await().getValue(icon), local.await().getValue(icon), icon)
                            } else {
                                println("Nothing ${theme.id}/$icon ($appCode)")
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
private fun CoroutineScope.contentEqualsAsync(client: HttpClient, local: Path?, remote: URL) = async(Dispatchers.IO) {
    val response = client.get<HttpResponse> {
        url(remote)
    }

    if (response.headers["content-length"]?.toLong() == Files.size(local))
        IOUtils.contentEquals(response.content.toInputStream(), Files.newInputStream(local))
    else
        false
}

private fun getAssetUrl(appId: Long, iconId: Long?) = URL("https://cdn.discordapp.com/app-assets/$appId/$iconId.png?size=1024")

private fun CoroutineScope.getLocalIconsAsync(appCode: String, theme: String) = async(Dispatchers.IO) {
    val application = Stream.of("application" to Paths.get("applications/$appCode.png"))
    val icons = Files.list(Paths.get("themes/$theme"))
            .filter { p -> Files.isRegularFile(p) }
            .filter { p -> p.name.endsWith(".png") }
            .map { p -> p.name.substring(0, p.name.length - 4) to p }

    Stream.concat(application, icons).toMap()
}

private fun CoroutineScope.getDiscordIconsAsync(client: HttpClient, appId: Long) = async(Dispatchers.IO) {
    val response = client.get<HttpResponse> {
        url(URL("https://discordapp.com/api/oauth2/applications/$appId/assets"))
    }

    val array = ObjectMapper().readTree(response.readBytes()) as ArrayNode

    Map(array.size()) { i ->
        val node = (array[i] as ObjectNode)
        node["name"].asText() to node["id"].asLong()
    }
}
