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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.*
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.mapValue
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.retryAsync
import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.debugLazy
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.intellij.openapi.application.PathManager
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import okhttp3.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BintraySource(location: String) : Source, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    private val user: String
    private val repository: String
    private val `package`: String

    private val client: OkHttpClient

    init {
        val (user, repository, `package`) = location.split('/', limit = 3)
        this.user = user
        this.repository = repository
        this.`package` = `package`

        val appConfigHash = Paths.get(PathManager.getConfigPath()).toAbsolutePath().toString().hashCode()
        val cacheDir =
            Paths.get(FileUtils.getTempDirectoryPath(), "JetBrains-Discord-Integration/$appConfigHash/bintray")
        val cacheSize = 1024L * 1024L * 16L  // 16MiB

        val cache = Cache(cacheDir.toFile(), cacheSize)
        val connectionPool = ConnectionPool(150, 10, TimeUnit.SECONDS)
        val threadPool = Executors.newCachedThreadPool()

        client = OkHttpClient.Builder()
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
    }

    private val versionJob: Deferred<String> = retryAsync(
        log = { DiscordPlugin.LOG.error("Error getting Bintray version", it) },
        block = { readVersion().also { DiscordPlugin.LOG.debugLazy { "Bintray version complete: $it" } } })
    private val filesJob: Deferred<Collection<String>> = retryAsync(
        log = { DiscordPlugin.LOG.error("Error getting Bintray files", it) },
        block = { readFiles().also { DiscordPlugin.LOG.debugLazy { "Bintray files complete: $it" } } })
    private val languageJob: Deferred<LanguageMap> = retryAsync(
        log = { DiscordPlugin.LOG.error("Error getting Bintray languages", it) },
        block = { readLanguages().also { DiscordPlugin.LOG.debugLazy { "Bintray languages complete: $it" } } })
    private val themeJob: Deferred<ThemeMap> = retryAsync(
        log = { DiscordPlugin.LOG.error("Error getting Bintray themes", it) },
        block = { readThemes().also { DiscordPlugin.LOG.debugLazy { "Bintray themes complete: $it" } } })
    private val applicationJob: Deferred<ApplicationMap> = retryAsync(
        log = { DiscordPlugin.LOG.error("Error getting Bintray applications", it) },
        block = { readApplications().also { DiscordPlugin.LOG.debugLazy { "Bintray applications complete: $it" } } })

    override fun getLanguagesAsync(): Deferred<LanguageMap> = languageJob
    override fun getThemesAsync(): Deferred<ThemeMap> = themeJob
    override fun getApplicationsAsync(): Deferred<ApplicationMap> = applicationJob

    private suspend fun readVersion(): String {
        DiscordPlugin.LOG.info("Getting Bintray repo version")

        return get("https://bintray.com/api/v1/packages/$user/$repository/$`package`/versions/_latest") { body ->
            ObjectMapper().readTree(body.byteStream())
                .get("name")
                .asText()
        }
    }

    private suspend fun readFiles(): Collection<String> {
        DiscordPlugin.LOG.info("Getting Bintray repo files")

        val version: String = versionJob.await()

        return get("https://bintray.com/api/v1/packages/$user/$repository/$`package`/versions/$version/files") { body ->
            ObjectMapper().readTree(body.byteStream())
                .map { node -> node["path"].asText() }
                .map { path -> path.substring(`package`.length + 1 + version.length + 1) }
        }
    }

    private suspend fun readLanguages() = BintrayLanguageSourceMap(read("languages", ::LanguageSource)).toLanguageMap()
    private suspend fun readThemes() = BintrayThemeSourceMap(this, read("themes", ::ThemeSource)).toThemeMap()
    private suspend fun readApplications() = BintrayApplicationSourceMap(read("applications", ::ApplicationSource)).toApplicationMap()

    private suspend fun <T> read(type: String, factory: (String, JsonNode) -> T): Map<String, T> = coroutineScope {
        DiscordPlugin.LOG.info("Getting Bintray repo $type")

        val mapper = ObjectMapper(YAMLFactory())

        filesJob.await()
            .asSequence()
            .filter { path -> path.startsWith(type) }
            .map { path ->
                val id = FilenameUtils.getBaseName(path)
                id to async {
                    getFile(user, repository, `package`, versionJob.await(), path) { body ->
                        val node: JsonNode = mapper.readTree(body.string())
                        factory(id, node)
                    }
                }
            }
            .mapValue { it.asCompletableFuture().get() }
            .toMap()
    }

    private suspend fun <T> get(url: String, handler: (ResponseBody) -> T) =
        suspendCancellableCoroutine<T> { continuation ->
            val request = Request.Builder()
                .get()
                .url(url)
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(RuntimeException(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    when (response.code) {
                        in 200..299 -> when (val body = response.body) {
                            null -> {
                                response.close()
                                continuation.resumeWithException(RuntimeException())
                            }
                            else -> body.use { continuation.resume(handler(body)) }
                        }
                        else -> {
                            response.close()
                            continuation.resumeWithException(RuntimeException("${response.code}"))
                        }
                    }
                }
            })
        }

    private suspend fun <T> getFile(user: String, repository: String, `package`: String, version: String, path: String, handler: (ResponseBody) -> T) =
        get("https://dl.bintray.com/$user/$repository/$`package`/$version/$path", handler)
}
