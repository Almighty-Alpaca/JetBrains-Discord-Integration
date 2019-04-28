package com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic
import java.time.OffsetDateTime

class FileEditorManagerListener : FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        Logger.Level.TRACE { "FileEditorManagerListener#fileOpened($source, $file)" }

        val project = source.project

        ApplicationComponent.instance.app {
            update(project) {
                add(file)
            }
        }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        Logger.Level.TRACE { "FileEditorManagerListener#fileClosed($source, $file)" }

        val project = source.project

        ApplicationComponent.instance.app {
            update(project) {
                remove(file)
            }
        }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        Logger.Level.TRACE { "FileEditorManagerListener#selectionChanged($event)" }

        val project = event.manager.project

        ApplicationComponent.instance.app {
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
