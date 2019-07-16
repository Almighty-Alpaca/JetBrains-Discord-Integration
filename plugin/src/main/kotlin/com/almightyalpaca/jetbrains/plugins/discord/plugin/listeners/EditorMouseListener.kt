/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.time.OffsetDateTime

class EditorMouseListener : com.intellij.openapi.editor.event.EditorMouseListener {
    override fun mousePressed(event: EditorMouseEvent) {
        log { "EditorMouseListener#mousePressed($event)" }

        val project = event.editor.project
        val file = FileDocumentManager.getInstance().getFile(event.editor.document)

        ApplicationComponent.instance.app {
            update(project) {
                update(file) {
                    accessedAt = OffsetDateTime.now()
                }
            }
        }
    }

    override fun mouseClicked(event: EditorMouseEvent) {}

    override fun mouseReleased(event: EditorMouseEvent) {}

    override fun mouseEntered(event: EditorMouseEvent) {}

    override fun mouseExited(event: EditorMouseEvent) {}

    companion object : Logging()
}
