package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService

class RichPresenceServiceImpl : RichPresenceService {
    private var connection: RPCConnection? = null

    @Synchronized
    override fun update(data: RichPresenceData?) {
        Logger.Level.TRACE { "RichPresenceServiceImpl#update()" }

        if (data == null) { // Stop connection
            if (connection != null) {
                connection?.disconnect()
                connection = null
            }
        } else {
            if (connection?.appId != data.appId) {
                if (connection == null)
                    connection?.disconnect()
                connection = RPCConnection(appId = data.appId)
                connection?.connect()
            }

            connection?.send(data.presence)
        }
    }

    companion object : Logging()
}
