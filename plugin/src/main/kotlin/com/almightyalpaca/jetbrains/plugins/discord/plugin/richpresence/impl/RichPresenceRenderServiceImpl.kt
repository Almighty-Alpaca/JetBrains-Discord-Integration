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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.accessedAt
import com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.openedAt
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.RichPresenceRenderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.renderer.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.richPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.application
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class RichPresenceRenderServiceImpl : RichPresenceRenderService {
    private val executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable).apply {
            isDaemon = true
            name = this::class.java.name
        }
    }

    private var isTimeout = false

    private val started = AtomicBoolean(false)

    private val delay = 5L

    @Synchronized
    override fun render() {
        if (!started.getAndSet(true)) {
            executor.scheduleAtFixedRate(this::runRenderJob, 0, delay, TimeUnit.SECONDS)
        }
    }

    private fun runRenderJob(): Unit = try {
        val context = RenderContext(Renderer.Mode.NORMAL)

        var shouldRender = true

        if (!settings.show.get()) {
            shouldRender = false
        }

        if (settings.timeoutEnabled.get()) {
            val durationUntilTimeout = System.currentTimeMillis() - context.accessedAt

            if (TimeUnit.MILLISECONDS.toMinutes(durationUntilTimeout) >= settings.timeoutMinutes.get()) {
                shouldRender = false
                isTimeout = true
            }
        }

        if (isTimeout && shouldRender) {
            isTimeout = false

            if (settings.timeoutResetTimeEnabled.get()) {
                application.openedAt = System.currentTimeMillis()
                application.accessedAt = System.currentTimeMillis()

                runRenderJob()
            }
        }

        if (shouldRender) {
            val renderer = context.createRenderer()
            val presence = renderer.render()

            richPresenceService.update(presence)
        } else {
            richPresenceService.update(null)
        }
    } catch (e: Exception) {
        log(e)
    }

    companion object : Logging()
}
