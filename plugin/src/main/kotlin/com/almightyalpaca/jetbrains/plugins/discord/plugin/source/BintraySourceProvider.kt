package com.almightyalpaca.jetbrains.plugins.discord.plugin.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSourceSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.SourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSourceSet
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.intellij.openapi.application.PathManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("BlockingMethodInNonBlockingContext")
class BintraySourceProvider(location: String) : SourceProvider {
    override val languages: LanguageSourceSet
    override val themes: ThemeSourceSet

    init {
        val appConfigHash = Paths.get(PathManager.getConfigPath()).toAbsolutePath().toString().hashCode()
        val cacheDir = Paths.get(FileUtils.getTempDirectoryPath(), "JetBrains-Discord-Integration/$appConfigHash/bintray")
        val cacheSize = 1024L * 1024L * 16L  // 16MiB

        val cache = Cache(cacheDir.toFile(), cacheSize)
        val connectionPool = ConnectionPool(150, 10, TimeUnit.SECONDS)
        val threadPool = Executors.newCachedThreadPool()

        val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(cache)
                .connectionPool(connectionPool)
                .dispatcher(Dispatcher(threadPool).apply {
                    maxRequests = 150
                    maxRequestsPerHost = 150
                })
                .build()

        val (user, repository, `package`) = location.split('/', limit = 3)

        val mapper = ObjectMapper(YAMLFactory())

        try {
            val version = runBlocking(Dispatchers.IO) { client.getLatestVersion(user, repository, `package`) }
            val files = runBlocking(Dispatchers.IO) { client.getFiles(user, repository, `package`, version) }

            val (languageFiles, themeFiles) = files.partition { path -> path.startsWith("languages") }

            languages = runBlocking(Dispatchers.IO) {
                languageFiles.map { path ->
                    try {
                        async {
                            client.getFile(user, repository, `package`, version, path) { body ->
                                val node: JsonNode = mapper.readTree(body.string())
                                SourceProvider.Source(FilenameUtils.getBaseName(path).toLowerCase(), node)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
                        .map { async -> async.await() }
                        .map { p -> p.id to p }
                        .toMap()
            }

            println(languages)

            themes = runBlocking(Dispatchers.IO) {
                themeFiles.map { path ->
                    try {
                        async {
                            client.getFile(user, repository, `package`, version, path) { body ->
                                val node: JsonNode = mapper.readTree(body.charStream())
                                SourceProvider.Source(FilenameUtils.getBaseName(path).toLowerCase(), node)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
                        .map { async -> async.await() }
                        .map { p -> p.id to p }
                        .toMap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        } finally {
            threadPool.shutdown() // TODO: remove once https://github.com/square/okhttp/issues/4029 has been fixed
            connectionPool.evictAll()
        }

        println(themes)
    }
}

private suspend fun <T> OkHttpClient.get(url: String, handler: (ResponseBody) -> T) =
        suspendCancellableCoroutine<T> { continuation ->
            val request = Request.Builder()
                    .get()
                    .url(url)
                    .build()

            val call = newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(RuntimeException(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    when (response.code()) {
                        in 200..299 -> when (val body = response.body()) {
                            null -> {
                                response.close()
                                continuation.resumeWithException(RuntimeException())
                            }
                            else -> body.use { continuation.resume(handler(body)) }
                        }
                        else -> {
                            response.close()
                            continuation.resumeWithException(RuntimeException("${response.code()}"))
                        }
                    }
                }
            })
        }

private suspend fun OkHttpClient.getLatestVersion(user: String, repository: String, `package`: String) =
        get("https://bintray.com/api/v1/packages/$user/$repository/$`package`/versions/_latest") { body ->
            ObjectMapper().readTree(body.byteStream())
                    .get("name")
                    .asText()
        }

private suspend fun OkHttpClient.getFiles(user: String, repository: String, `package`: String, version: String) =
        get("https://bintray.com/api/v1/packages/$user/$repository/$`package`/versions/$version/files") { body ->
            ObjectMapper().readTree(body.byteStream())
                    .map { node -> node["path"].asText() }
                    .map { path -> path.substring(`package`.length + 1 + version.length + 1) }
        }

private suspend fun <T> OkHttpClient.getFile(user: String, repository: String, `package`: String, version: String, path: String, handler: (ResponseBody) -> T) =
        get("https://dl.bintray.com/$user/$repository/$`package`/$version/$path", handler)
