package com.almightyalpaca.jetbrains.plugins.discord.app.rpc

import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logging
import com.intellij.openapi.components.ServiceManager

interface RichPresenceService {
    fun update(data: RichPresenceData?)

    companion object : Logging() {
        inline val instance: RichPresenceService
            get() = ServiceManager.getService(RichPresenceService::class.java)
    }
}
