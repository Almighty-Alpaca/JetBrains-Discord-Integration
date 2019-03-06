package com.almightyalpaca.jetbrains.plugins.discord.app.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.app.components.AppComponent
import com.almightyalpaca.jetbrains.plugins.discord.app.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.app.data.ApplicationDataBuilder
import com.almightyalpaca.jetbrains.plugins.discord.app.listeners.*
import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.app.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.app.rpc.renderer.getContext
import com.almightyalpaca.jetbrains.plugins.discord.app.source.FileSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.app.source.GitHubSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.app.utils.failingLazy
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

class AppComponentImpl : AppComponent, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private val documentListener: DocumentListener by lazy { DocumentListener() }
    private val editorMouseListener: EditorMouseListener by lazy { EditorMouseListener() }
    private val virtualFileListener: VirtualFileListener by lazy { VirtualFileListener() }

    private val connection: MessageBusConnection by lazy { ApplicationManager.getApplication().messageBus.connect() }

    private var updateJob: Job? = null

    // TODO: get sources from github
    private val provider: SourceProvider? by failingLazy<SourceProvider?>(null) {
        val icons = System.getenv("ICONS")
        when {
            icons == null -> GitHubSourceProvider("Almighty-Alpaca/JetBrains-Discord-Integration")
            icons.matches(Regex("[\\d\\w-]+\\/[\\d\\w-]+:[\\d\\w-\\/]+")) -> GitHubSourceProvider(icons)
            else -> FileSourceProvider(Paths.get(icons))
        }
    }

    private val languages: LanguageMap by failingLazy(LanguageMap.EMPTY) { provider!!.languages.toLanguageMap() }
    private val themes: ThemeMap by failingLazy(ThemeMap.EMPTY) { provider!!.themes.toThemeMap() }

    private var applicationData: ApplicationData = ApplicationData.EMPTY
        @Synchronized
        set(value) {
            Logger.Level.TRACE { "AppComponentImpl#setAppData($value)" }

            field = value

            updateJob?.cancel()

            updateJob = launch {
                Logger.Level.TRACE { "AppComponentImpl#setAppData()\$async start" }

                RichPresenceService.instance.update(applicationData.getContext(languages, themes).render())

                Logger.Level.TRACE { "AppComponentImpl#setAppData()\$async end" }
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
