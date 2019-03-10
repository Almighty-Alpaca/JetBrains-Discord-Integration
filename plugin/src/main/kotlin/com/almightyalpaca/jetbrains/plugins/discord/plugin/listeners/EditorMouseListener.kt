package com.almightyalpaca.jetbrains.plugins.discord.plugin.listeners

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.time.OffsetDateTime

class EditorMouseListener : com.intellij.openapi.editor.event.EditorMouseListener {
    override fun mousePressed(event: EditorMouseEvent) {
        Logger.Level.TRACE { "EditorMouseListener#mousePressed($event)" }

        val project = event.editor.project
        val file = FileDocumentManager.getInstance().getFile(event.editor.document)

        ApplicationComponent.instance.app {
            update(project) {
                update(file) {
                    accessedAt = OffsetDateTime.now()
                }
            }
        }
    }

    override fun mouseClicked(event: EditorMouseEvent) {}

    override fun mouseReleased(event: EditorMouseEvent) {}

    override fun mouseEntered(event: EditorMouseEvent) {}

    override fun mouseExited(event: EditorMouseEvent) {}

    companion object : Logging()
}
