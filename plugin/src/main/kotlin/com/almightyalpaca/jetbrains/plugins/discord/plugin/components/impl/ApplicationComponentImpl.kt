package com.almightyalpaca.jetbrains.plugins.discord.plugin.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.getContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.BintraySourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.FileSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.GitHubSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.failingLazy
import com.almightyalpaca.jetbrains.plugins.shared.languages.LanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.source.SourceProvider
import com.almightyalpaca.jetbrains.plugins.shared.source.toLanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.source.toThemeMap
import com.almightyalpaca.jetbrains.plugins.shared.themes.ThemeMap
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

    private val provider: SourceProvider? by failingLazy<SourceProvider?>(null) {
        val icons: String? = System.getenv("ICONS")
        val (platform, location) = icons?.split(':', limit = 2) ?: listOf("", "")
        when (platform.toLowerCase()) {
            "github" -> GitHubSourceProvider(location)
            "bintray" -> BintraySourceProvider(location)
            "local" -> FileSourceProvider(Paths.get(location))
            else -> BintraySourceProvider("almightyalpaca/JetBrains-Discord-Integration/Icons")
        }
    }

    // TODO: better error handling for download failures
    private val languages: LanguageMap by failingLazy(LanguageMap.EMPTY) { provider!!.languages.toLanguageMap() }
    private val themes: ThemeMap by failingLazy(ThemeMap.EMPTY) { provider!!.themes.toThemeMap() }

    private var applicationData: ApplicationData = ApplicationData.EMPTY
        @Synchronized
        set(value) {
            Logger.Level.TRACE { "ApplicationComponentImpl#setAppData($value)" }

            field = value

            updateJob?.cancel()

            updateJob = launch {
                Logger.Level.TRACE { "ApplicationComponentImpl#setAppData()\$async start" }

                RichPresenceService.instance.update(applicationData.getContext(languages, themes).render())

                Logger.Level.TRACE { "ApplicationComponentImpl#setAppData()\$async end" }
            }
            updateJob?.start()
        }

    override fun initComponent() {
        applicationData = ApplicationData.DEFAULT

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
        val applicationDataBuilder = applicationData.builder()

        applicationDataBuilder.builder()

        applicationData = applicationDataBuilder.build()
    }

    companion object : Logging()
}
