package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager

interface PluginSettings : SharedSettings, PersistentStateComponent<PluginSettings> {
    companion object {
        inline val instance: PluginSettings
            get() = ServiceManager.getService(PluginSettings::class.java)
    }
}
