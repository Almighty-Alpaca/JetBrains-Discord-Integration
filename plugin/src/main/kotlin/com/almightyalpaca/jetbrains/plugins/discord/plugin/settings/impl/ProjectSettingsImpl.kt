package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ProjectSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.PersistentStateOptionHolderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.check
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.text
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.toggle
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.toggleable
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "DiscordApplicationSettings", storages = [Storage("discord.xml")])
class ProjectSettingsImpl(override val project: Project) : ProjectSettings, PersistentStateOptionHolderImpl() {
    override val hide by check("Hide this project in Rich Presence", false)

    private val nameOverrideToggle by toggleable<Boolean>()
    override val nameOverrideEnabled by nameOverrideToggle.toggle.check("Override project name", false)
    override val nameOverrideText by nameOverrideToggle.option.text("", "")

    override val description by text("Project description", "")
}
