package com.almightyalpaca.jetbrains.plugins.discord.app.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager

interface AppSettings : SharedSettings, PersistentStateComponent<AppSettings> {
    companion object {
        inline val instance: AppSettings
            get() = ServiceManager.getService(AppSettings::class.java)
    }
}
