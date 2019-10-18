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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.richPresenceRenderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.richPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray.BintraySource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.messages.MessageBusConnection
import java.nio.file.Paths

class ApplicationComponentImpl : ApplicationComponent {
    private val documentListener: DocumentListener by lazy { DocumentListener() }
    private val editorMouseListener: EditorMouseListener by lazy { EditorMouseListener() }
    private val virtualFileListener: VirtualFileListener by lazy { VirtualFileListener() }

    private val connection: MessageBusConnection by lazy { ApplicationManager.getApplication().messageBus.connect() }

    override val source: Source

    init {
        val icons: String? = System.getenv("com.almightyalpaca.jetbrains.plugins.discord.plugin.source")
        val (platform, location) = icons?.split(':', limit = 2) ?: listOf("", "")
        source = when (platform.toLowerCase()) {
            "bintray" -> BintraySource(location)
            "local" -> LocalSource(Paths.get(location))
            else -> BintraySource("almightyalpaca/JetBrains-Discord-Integration/Icons")
        }
    }

    override var data: ApplicationData = ApplicationData.DEFAULT
        @Synchronized
        private set(value) {
            field = value

            richPresenceRenderService.render()
        }

    override fun initComponent() {
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, FileDocumentManagerListener())
        connection.subscribe(FileEditorManagerListener.TOPIC, FileEditorManagerListener())

        with(EditorFactory.getInstance().eventMulticaster) {
            addDocumentListener(documentListener)
            addEditorMouseListener(editorMouseListener)
        }

        VirtualFileManager.getInstance().addVirtualFileListener(this.virtualFileListener)

    }

    @Synchronized
    override fun disposeComponent() {
        richPresenceService.update(null)

        this.connection.disconnect()

        with(EditorFactory.getInstance().eventMulticaster) {
            removeDocumentListener(documentListener)
            removeEditorMouseListener(editorMouseListener)
        }

        VirtualFileManager.getInstance().removeVirtualFileListener(this.virtualFileListener)

    }

    @Synchronized
    override fun app(builder: ApplicationDataBuilder.() -> Unit) {
        val applicationDataBuilder = data.builder()

        applicationDataBuilder.builder()

        data = applicationDataBuilder.build()
    }

    companion object : Logging()
}
