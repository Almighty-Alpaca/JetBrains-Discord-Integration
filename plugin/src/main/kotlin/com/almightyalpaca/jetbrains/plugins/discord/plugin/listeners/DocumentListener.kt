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
