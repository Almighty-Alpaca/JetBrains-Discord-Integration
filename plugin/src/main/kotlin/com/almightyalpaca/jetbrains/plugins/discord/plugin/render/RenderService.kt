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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.rpcService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.scheduleWithFixedDelay
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

val renderService: RenderService
    get() = service()

@Service
class RenderService : DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var renderClockJob: ScheduledFuture<*>? = null

    private var renderJob: Job? = null

    @Synchronized
    fun render(force: Boolean = false) {
        renderJob?.let {
            DiscordPlugin.LOG.debug("Canceling previous render due to new request")
            it.cancel()
        }

        renderJob = launch {
            DiscordPlugin.LOG.debug("Scheduling render, force=$force")

            if (Disposer.isDisposed(this@RenderService)) {
                DiscordPlugin.LOG.debug("Skipping render, service already disposed")
                return@launch
            }

            val data = dataService.getData(Renderer.Mode.NORMAL) ?: return@launch

            val context = RenderContext(sourceService.source, data, Renderer.Mode.NORMAL)

            val renderer = context.createRenderer()
            val presence = renderer?.render()

            if (presence == null) {
                DiscordPlugin.LOG.debug("Render result: visible")
            } else {
                DiscordPlugin.LOG.debug("Render result: hidden")
            }

            rpcService.update(presence, force)

            renderJob = null
        }
    }

    fun startRenderClock() {
        val executor = AppExecutorUtil.getAppScheduledExecutorService()

        this.renderClockJob = executor.scheduleWithFixedDelay(delay = 5, unit = TimeUnit.SECONDS) {
            try {
                render()
            } catch (e: ProcessCanceledException) {
                throw e
            } catch (e: Exception) {
                DiscordPlugin.LOG.error("Error rendering presence", e)
            }
        }
    }

    override fun dispose() {
        renderClockJob?.cancel(true)
    }
}
