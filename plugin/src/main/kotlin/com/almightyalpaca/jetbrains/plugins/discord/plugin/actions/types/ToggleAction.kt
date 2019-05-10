package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.toggle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.project.Project
import javax.swing.Icon

class ToggleAction(private val value: (Project) -> BooleanValue, private val enabled: View, private val disabled: View) : AnAction() {
    constructor(value: BooleanValue, enabled: View, disabled: View) : this({ value }, enabled, disabled)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return

        value(project).toggle()
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return

        if (value(project).get()) {
            enabled.apply(e.presentation)
        } else {
            disabled.apply(e.presentation)
        }
    }

    class View(
        var text: String = "",
        var description: String = "",
        var icon: Icon? = null,
        var hoveredIcon: Icon? = null
    ) {
        constructor(block: View.() -> Unit) : this() {
            apply(block)
        }

        fun apply(presentation: Presentation) {
            presentation.text = text
            presentation.description = description
            presentation.icon = icon
            // presentation.hoveredIcon = hoveredIcon
            presentation.selectedIcon = hoveredIcon // For some reason this is actually the hover icon ¯\_(ツ)_/¯
        }
    }
}
