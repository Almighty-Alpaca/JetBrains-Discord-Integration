package com.almightyalpaca.jetbrains.plugins.discord.app.listeners

import com.almightyalpaca.jetbrains.plugins.discord.app.components.AppComponent
import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logging
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic
import java.time.OffsetDateTime

class FileEditorManagerListener : com.intellij.openapi.fileEditor.FileEditorManagerListener {

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        Logger.Level.TRACE { "FileEditorManagerListener#fileOpened($source, $file)" }

        val project = source.project

        AppComponent.instance.app {
            update(project) {
                add(file)
            }
        }
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        Logger.Level.TRACE { "FileEditorManagerListener#fileClosed($source, $file)" }

        val project = source.project

        AppComponent.instance.app {
            update(project) {
                remove(file)
            }
        }
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        Logger.Level.TRACE { "FileEditorManagerListener#selectionChanged($event)" }

        val project = event.manager.project

        AppComponent.instance.app {
            update(project) {
                update(event.newFile) {
                    accessedAt = OffsetDateTime.now()
                }
            }
        }
    }

    companion object : Logging() {
        inline val TOPIC: Topic<FileEditorManagerListener>
            get() = com.intellij.openapi.fileEditor.FileEditorManagerListener.FILE_EDITOR_MANAGER
    }
}
