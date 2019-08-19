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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.applicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic
import java.time.OffsetDateTime

class FileEditorManagerListener : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        log { "FileEditorManagerListener#fileOpened($source, $file)" }

        val project = source.project

        applicationComponent.app {
            update(project) {
                add(file)
            }
        }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        log { "FileEditorManagerListener#fileClosed($source, $file)" }

        val project = source.project

        applicationComponent.app {
            update(project) {
                remove(file)
            }
        }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        log { "FileEditorManagerListener#selectionChanged($event)" }

        val project = event.manager.project

        applicationComponent.app {
            update(project) {
                update(event.newFile) {
                    accessedAt = OffsetDateTime.now()
                }
            }
        }
    }

    companion object : Logging() {
        inline val TOPIC: Topic<FileEditorManagerListener>
            get() = FileEditorManagerListener.FILE_EDITOR_MANAGER
    }
}
