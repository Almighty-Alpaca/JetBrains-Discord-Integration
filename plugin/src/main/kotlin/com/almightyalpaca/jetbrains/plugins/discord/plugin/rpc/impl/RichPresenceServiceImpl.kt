package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.NativeRPCConnection
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.RPCConnection

class RichPresenceServiceImpl : RichPresenceService {
    private var _user: User? = null
    override val user: User
        get() = _user ?: User.CLYDE

    private var connection: RPCConnection? = null

    private var lastPresence: RichPresence? = null

    @Synchronized
    override fun update(presence: RichPresence?) {
        Logger.Level.TRACE { "RichPresenceServiceImpl#update()" }

        if (lastPresence == presence)
            return

        lastPresence = presence

        if (presence == null) { // Stop connection
            if (connection != null) {
                connection?.disconnect()
                connection = null
            }
        } else {
            if (connection?.appId != presence.appId) {
                if (connection == null)
                    connection?.disconnect()
                connection = NativeRPCConnection(presence.appId) { u -> _user = u }
                connection?.connect()
            }

            connection?.send(presence)
        }
    }

    companion object : Logging()
}
