package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.components.ServiceManager

interface RichPresenceService {
    fun update(data: RichPresenceData?)

    companion object : Logging() {
        inline val instance: RichPresenceService
            get() = ServiceManager.getService(RichPresenceService::class.java)
    }
}
