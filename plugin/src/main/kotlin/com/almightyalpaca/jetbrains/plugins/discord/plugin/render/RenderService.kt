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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.rpcService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val renderService: RenderService
    get() = service()

private const val RENDER_INTERVAL: Long = 5_000

@Service
class RenderService : DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var renderJob: Job? = null
//    private var timeoutJob: Job? = null

    private var isTimeout = false

    private var forceUpdate = false

    @Synchronized
    fun render(force: Boolean = false) {
        if (Disposer.isDisposed(this)) {
            return
        }

        if (force)
            forceUpdate = true

        renderJob?.cancel()
        renderJob = launch {
//            timeoutJob?.cancel()

            val data = dataService.getData() ?: return@launch

            val context = RenderContext(sourceService.source, data, Renderer.Mode.NORMAL)

            if (force)
                forceUpdate = true

            var shouldRender = true

            if (!settings.show.get()) {
                shouldRender = false
            }

//            if (settings.timeoutEnabled.get()) {
//                // TODO: fix timeout
//                val lastAccessedAt =
//                    OffsetDateTime.now() //(context.file ?: context.project ?: context.application).accessedAt
//                val durationUntilTimeout = Duration.between(lastAccessedAt, OffsetDateTime.now())
//
//                if (durationUntilTimeout.toMinutes() >= settings.timeoutMinutes.get()) {
//                    shouldRender = false
//                    isTimeout = true
//                } else {
//                    if (!isTimeout) {
//                        timeoutJob = launch {
//                            val delay =
//                                Duration.ofMinutes(settings.timeoutMinutes.get().toLong()).minus(durationUntilTimeout)
//                                    .toMillis()
//                            delay(delay)
//
//                            timeoutJob = null
//                            render()
//                        }
//                    }
//                }
//            }

            if (isTimeout && shouldRender && !forceUpdate) {
                isTimeout = false

                // TODO: fix resetting timeout
//                if (settings.timeoutResetTimeEnabled.get()) {
//                    launch {
//                        dataService.app {
//                            openedAt = OffsetDateTime.now()
//                        }
//                    }
//
//                    return@launch
//                }
            }

            if (shouldRender) {
                val renderer = context.createRenderer()
                val presence = renderer.render()

                rpcService.update(presence, forceUpdate = forceUpdate)
            } else {
                rpcService.update(null, forceUpdate = forceUpdate)
            }

            forceUpdate = false

            delay(RENDER_INTERVAL)

            render()
        }
    }
}
