package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

interface ProjectSettings : OptionHolder {
    val project: Project

    val hide: BooleanValue
    val description: StringValue
}

val Project.settings: ProjectSettings
    get() = ServiceManager.getService(this, ProjectSettings::class.java)
