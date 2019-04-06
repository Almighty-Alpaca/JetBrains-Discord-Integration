package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.util.ui.components.BorderLayoutPanel

class ProjectConfigurable(val project: Project) : SearchableConfigurable {
    private val settings = project.settings

    override fun getId() = "discord-project"

    override fun isModified(): Boolean = settings.isModified

    override fun getDisplayName() = "Discord Integration Project Settings"

    override fun apply() {
        settings.apply()

        ApplicationComponent.instance.update()
    }

    override fun reset() {
        settings.reset()
    }

    override fun createComponent() = BorderLayoutPanel().addToCenter(settings.component)

    override fun getHelpTopic(): String? = null
}
