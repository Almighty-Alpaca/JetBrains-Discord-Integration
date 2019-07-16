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
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import java.time.OffsetDateTime

class FileDocumentManagerListener : com.intellij.openapi.fileEditor.FileDocumentManagerListener {

    override fun beforeAllDocumentsSaving() {}

    override fun beforeDocumentSaving(document: Document) {
        log { "FileDocumentManagerListener#beforeDocumentSaving($document)" }

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

    override fun beforeFileContentReload(file: VirtualFile, document: Document) {}

    override fun fileWithNoDocumentChanged(file: VirtualFile) {}

    override fun fileContentReloaded(file: VirtualFile, document: Document) {}

    override fun fileContentLoaded(file: VirtualFile, document: Document) {}

    override fun unsavedDocumentsDropped() {}

    companion object : Logging()
}
