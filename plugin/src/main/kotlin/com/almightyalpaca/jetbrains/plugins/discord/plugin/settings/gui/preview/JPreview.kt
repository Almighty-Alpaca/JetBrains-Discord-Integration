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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.gui.preview

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import kotlinx.coroutines.*
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.coroutines.CoroutineContext

class JPreview : JLabel(), CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private val preview = PreviewRenderer()

    var type: Renderer.Type.Application = Renderer.Type.Application
        set(value) {
            field = value
            update()
        }

    private var updateJob: Job? = null

    init {
        icon = ImageIcon(preview.dummy)

        update(true)
    }

    @Synchronized
    fun update(force: Boolean = false) {
        updateJob?.cancel()

        updateJob = launch {
            if (isShowing) {
                val (modified, image) = preview.draw(type, force)

                if (modified) {
                    icon = ImageIcon(image)
                }
            }

            updateJob = launch {
                delay(1000)
                update()
            }
        }
    }
}
