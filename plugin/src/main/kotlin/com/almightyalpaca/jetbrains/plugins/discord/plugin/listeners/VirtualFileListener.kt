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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.isReadOnly
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.*
import java.time.OffsetDateTime

class VirtualFileListener : com.intellij.openapi.vfs.VirtualFileListener {
    override fun propertyChanged(event: VirtualFilePropertyEvent) {
        log { "VirtualFileListener#propertyChanged($event)" }

        val file = event.file

        val documentManager = FileDocumentManager.getInstance()

        val document = documentManager.getDocument(file)

        document?.let {
            val editors = EditorFactory.getInstance().getEditors(document)

            ApplicationComponent.instance.app {
                for (editor in editors) {
                    update(editor.project) {
                        update(file) {
                            accessedAt = OffsetDateTime.now()
                            if (event.propertyName == VirtualFile.PROP_NAME) {
                                path = file.filePath
                            } else if (event.propertyName == VirtualFile.PROP_WRITABLE) {
                                readOnly = file.isReadOnly
                            }
                        }
                    }
                }
            }
        }
    }

    override fun contentsChanged(event: VirtualFileEvent) {}

    override fun fileCreated(event: VirtualFileEvent) {}

    override fun fileDeleted(event: VirtualFileEvent) {
        log { "VirtualFileListener#fileDeleted($event)" }

        val file = event.file
        val documentManager = FileDocumentManager.getInstance()

        val document = documentManager.getDocument(file)

        document?.let {
            val editors = EditorFactory.getInstance().getEditors(document)

            ApplicationComponent.instance.app {
                for (editor in editors) {
                    update(editor.project) {
                        remove(file)
                    }
                }
            }
        }
    }

    override fun fileMoved(event: VirtualFileMoveEvent) {
        log { "VirtualFileListener#fileMoved($event)" }

        val file = event.file
        val documentManager = FileDocumentManager.getInstance()

        val document = documentManager.getDocument(file)

        document?.let {
            val editors = EditorFactory.getInstance().getEditors(document)

            ApplicationComponent.instance.app {
                for (editor in editors) {
                    update(editor.project) {
                        update(file) {
                            path = file.filePath
                            accessedAt = OffsetDateTime.now()
                        }
                    }
                }
            }
        }
    }

    override fun fileCopied(event: VirtualFileCopyEvent) {}

    override fun beforePropertyChange(event: VirtualFilePropertyEvent) {}

    override fun beforeContentsChange(event: VirtualFileEvent) {}

    override fun beforeFileDeletion(event: VirtualFileEvent) {}

    override fun beforeFileMovement(event: VirtualFileMoveEvent) {}

    companion object : Logging()
}
