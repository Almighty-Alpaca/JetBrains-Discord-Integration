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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.rpcService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.scheduleWithFixedDelay
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

val renderService: RenderService
    get() = service()

private const val RENDER_INTERVAL: Long = 5_000

@Service
class RenderService : DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var renderJob: ScheduledFuture<*>? = null

    @Synchronized
    fun render(force: Boolean = false) = runBlocking async@{
        DiscordPlugin.LOG.info("Scheduling render, force=$force")

        if (Disposer.isDisposed(this@RenderService)) {
            DiscordPlugin.LOG.info("Skipping render, service already disposed")
            return@async
        }

        val data = dataService.getData() ?: return@async

        val context = RenderContext(sourceService.source, data, Renderer.Mode.NORMAL)

        var shouldRender = true

        // TODO: check for timeout

        if (!settings.show.get()) {
            shouldRender = false
        }

        if (shouldRender) {
            DiscordPlugin.LOG.info("Render result: visible")

            val renderer = context.createRenderer()
            val presence = renderer.render()

            rpcService.update(presence, force)
        } else {
            DiscordPlugin.LOG.info("Render result: hidden")

            rpcService.update(null, force)
        }
    }

    fun startRenderClock() {
        val executor = AppExecutorUtil.getAppScheduledExecutorService()

        this.renderJob = executor.scheduleWithFixedDelay(delay = 5, unit = TimeUnit.SECONDS) {
            try {
                render()
            } catch (e: Exception) {
                DiscordPlugin.LOG.error("Error rendering presence", e)
            }
        }
    }

    override fun dispose() {
        renderJob?.cancel(true)
    }
}
