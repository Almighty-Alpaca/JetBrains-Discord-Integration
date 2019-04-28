package com.almightyalpaca.jetbrains.plugins.discord.plugin.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.renderType
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray.BintraySource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.messages.MessageBusConnection
import kotlinx.coroutines.*
import java.nio.file.Paths
import kotlin.coroutines.CoroutineContext

class ApplicationComponentImpl : ApplicationComponent, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private val documentListener: DocumentListener by lazy { DocumentListener() }
    private val editorMouseListener: EditorMouseListener by lazy { EditorMouseListener() }
    private val virtualFileListener: VirtualFileListener by lazy { VirtualFileListener() }

    private val connection: MessageBusConnection by lazy { ApplicationManager.getApplication().messageBus.connect() }

    private var updateJob: Job? = null

    override val source: Source

    init {
        val icons: String? = System.getenv("ICONS")
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

            update()
        }

    @Synchronized
    override fun update() {
        updateJob?.cancel()

        updateJob = launch {
            val context = RenderContext(source, data, Renderer.Mode.NORMAL)
            val renderer = context.renderType.createRenderer(context)
            val presence = renderer.render()

            RichPresenceService.instance.update(presence)

            updateJob = launch {
                delay(20 * 1000)

                update()
            }
        }
    }

    override fun initComponent() {
        data = ApplicationData.DEFAULT

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
        parentJob.cancel()

        RichPresenceService.instance.update(null)

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
