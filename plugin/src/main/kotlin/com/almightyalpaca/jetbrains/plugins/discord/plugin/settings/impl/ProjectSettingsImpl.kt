package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ProjectSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionHolderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.check
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.text
import com.intellij.openapi.project.Project

class ProjectSettingsImpl(override val project: Project) : ProjectSettings, OptionHolderImpl() {
    override val hide by check("Hide Rich Presence for this project", false)
    override val description by text("Project description", "")
}
