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
import kotlinx.coroutines.runBlocking
import java.awt.event.HierarchyEvent
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.Timer

class JPreview : JLabel() {

    private val preview = PreviewRenderer()

    private val worker = Timer(100) { update() }

    var type: Renderer.Type.Application = Renderer.Type.Application
        set(value) {
            field = value
            update()
        }

    init {
        icon = ImageIcon(preview.dummy)

        update(true)

        worker.start()

        addHierarchyListener { e ->
            if (e.hasChangeFlag(HierarchyEvent.SHOWING_CHANGED)) {
                if (isShowing) {
                    worker.start()
                } else {
                    worker.stop()
                }
            }
        }
    }

    @Synchronized
    fun update(force: Boolean = false) {
        if (isShowing) {
            val (modified, image) = runBlocking { preview.draw(type, force) }

            if (modified) {
                icon = ImageIcon(image)
            }
        }
    }
}

private fun HierarchyEvent.hasChangeFlag(flag: Int) = (changeFlags and flag.toLong()) != 0L
