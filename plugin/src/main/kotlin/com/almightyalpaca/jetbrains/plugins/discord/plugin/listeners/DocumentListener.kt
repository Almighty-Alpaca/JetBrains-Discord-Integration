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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.ProjectManagerListener
import java.time.OffsetDateTime

class DocumentListener : com.intellij.openapi.editor.event.DocumentListener, ProjectManagerListener {

    override fun beforeDocumentChange(event: DocumentEvent) {}

    override fun documentChanged(event: DocumentEvent) {
        log { "DocumentListener#documentChanged($event)" }

        val document = event.document
        val editors = EditorFactory.getInstance().getEditors(document)
        val file = FileDocumentManager.getInstance().getFile(document)

        ApplicationComponent.instance.app {
            for (editor in editors) {
                update(editor.project) {
                    update(file) {
                        accessedAt = OffsetDateTime.now()
                    }
                }
            }
        }
    }

    companion object : Logging()
}
