package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.intellij.openapi.components.ServiceManager

interface RichPresenceService {
    fun update(presence: RichPresence?)

    val user: User

    companion object : Logging() {
        inline val instance: RichPresenceService
            get() = ServiceManager.getService(RichPresenceService::class.java)
    }
}
