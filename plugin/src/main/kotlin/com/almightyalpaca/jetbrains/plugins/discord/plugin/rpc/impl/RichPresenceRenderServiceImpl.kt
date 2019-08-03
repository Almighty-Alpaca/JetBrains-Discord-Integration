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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceRenderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import kotlinx.coroutines.*
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

class RichPresenceRenderServiceImpl : RichPresenceRenderService, CoroutineScope {

    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private var timeoutJob: Job? = null

    private var isTimeout = false

    @Synchronized
    override fun render() {
        timeoutJob?.cancel()

        val component = ApplicationComponent.instance

        val context = RenderContext(component.source, component.data, Renderer.Mode.NORMAL)

        var shouldRender = true

        if (!settings.show.get()) {
            shouldRender = false
        }

        if (settings.timeoutEnabled.get()) {
            val lastAccessedAt = (context.file ?: context.project ?: context.application).accessedAt
            val durationUntilTimeout = Duration.between(lastAccessedAt, OffsetDateTime.now())

            if (durationUntilTimeout.toMinutes() >= settings.timeoutMinutes.get()) {
                shouldRender = false
                isTimeout = true
            } else {
                if (!isTimeout) {
                    timeoutJob = launch {
                        val delay = Duration.ofMinutes(settings.timeoutMinutes.get().toLong()).minus(durationUntilTimeout).toMillis()
                        delay(delay)

                        timeoutJob = null
                        render()
                    }
                }
            }
        }

        if (isTimeout && shouldRender) {
            isTimeout = false

            if (settings.timeoutResetTimeEnabled.get()) {
                launch {
                    component.app {
                        openedAt = OffsetDateTime.now()
                    }
                }

                return
            }
        }

        if (shouldRender) {
            val renderer = context.createRenderer()
            val presence = renderer.render()

            RichPresenceService.instance.update(presence)
        } else {
            RichPresenceService.instance.update(null)
        }
    }

    companion object : Logging()
}
