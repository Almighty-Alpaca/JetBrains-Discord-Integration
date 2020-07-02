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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.analytics

import com.almightyalpaca.jetbrains.plugins.discord.analytics.model.Analytics
import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.authorization
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.scheduleWithFixedDelay
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.AppExecutorUtil
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

val analyticsService: AnalyticsService
    get() = service()

// TODO: make server url a build time property with environment variable override
private const val ANALYTICS_URL = "http://localhost:8080/api/v1/analytics"

@Service
class AnalyticsService : DisposableCoroutineScope {
    override val parentJob: Job
        get() = SupervisorJob()

    private val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private val collector: Collector = LoggingCollector(DirectCollector()).also {
        Disposer.register(this, it)
    }

    fun reportIcon(time: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC), language: String, theme: String, applicationName: String, iconWanted: String, iconUsed: String) {
        val icon = Analytics.Icon(time, language, theme, applicationName, iconWanted, iconUsed)
        collector.collectIcon(icon)
    }

    fun reportFile(time: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC), editor: String, type: String, extension: String, language: String) {
        val file = Analytics.File(time, editor, type, extension, language)
        collector.collectFile(file)
    }

    fun reportVersion(time: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC), applicationVersion: String, applicationCode: String) {
        val version = Analytics.Version(time, applicationVersion, applicationCode)
        collector.collectVersion(version)
    }

    private inline fun postAsync(crossinline block: suspend HttpRequestBuilder.() -> Unit = {}) {
        if (!Disposer.isDisposed(this)) {
            launch {
                client.post<Unit>(ANALYTICS_URL) {
                    authorization("abcdefghijklmnopqrstuvwxyz")
                    contentType(ContentType.Application.Json)

                    block()
                }
            }
        }
    }

    override fun dispose() {
        super.dispose()

        client.close()
    }

    private interface Collector : Disposable {
        fun collectIcon(icon: Analytics.Icon)
        fun collectFile(file: Analytics.File)
        fun collectVersion(version: Analytics.Version)
    }

    private inner class DirectCollector : Collector {
        override fun collectIcon(icon: Analytics.Icon) {
            postAsync {
                body = Analytics(icons = listOf(icon))
            }
        }

        override fun collectFile(file: Analytics.File) {
            postAsync {
                body = Analytics(files = listOf(file))
            }
        }

        override fun collectVersion(version: Analytics.Version) {
            postAsync {
                body = Analytics(version = version)
            }
        }

        override fun dispose() = Unit
    }

    private inner class BatchCollector(private val delay: Long = 15, private val unit: TimeUnit = TimeUnit.MINUTES) : Collector {
        private val sendJob: ScheduledFuture<*>

        private val version = atomic<Analytics.Version?>(null)
        private val files = atomic<MutableList<Analytics.File>>(mutableListOf())
        private val icons = atomic<MutableList<Analytics.Icon>>(mutableListOf())

        init {
            val executor = AppExecutorUtil.getAppScheduledExecutorService()

            this.sendJob = executor.scheduleWithFixedDelay(delay = delay, unit = unit) {
                try {
                    sendData()
                } catch (e: ProcessCanceledException) {
                    throw e
                } catch (e: Exception) {
                    DiscordPlugin.LOG.error("Error sending analytics", e)
                }
            }
        }

        private fun sendData() {
            val version = this.version.getAndSet(null)
            val files = this.files.getAndSet(mutableListOf())
            val icons = this.icons.getAndSet(mutableListOf())

            if (version != null || files.isNotEmpty() || icons.isNotEmpty()) {
                postAsync {
                    body = Analytics(files, icons, version)
                }
            }
        }

        override fun collectIcon(icon: Analytics.Icon) {
            this.icons.value.add(icon)
        }

        override fun collectFile(file: Analytics.File) {
            this.files.value.add(file)
        }

        override fun collectVersion(version: Analytics.Version) {
            this.version.value = version
        }

        override fun dispose() {
            sendJob.cancel(false)

            sendData()
        }
    }

    private class LoggingCollector(
        private val collector: Collector,
        private val log: (() -> String) -> Unit = { DiscordPlugin.LOG.trace(it) } // to easily allow printing to System.out
    ) : Collector {
        override fun collectIcon(icon: Analytics.Icon) {
            log { "Collecting icon analytics: $icon" }
            collector.collectIcon(icon)
        }

        override fun collectFile(file: Analytics.File) {
            log { "Collecting file analytics: $file" }
            collector.collectFile(file)
        }

        override fun collectVersion(version: Analytics.Version) {
            log { "Collecting version analytics: $version" }
            collector.collectVersion(version)
        }

        override fun dispose() {
            collector.dispose()
        }
    }
}
