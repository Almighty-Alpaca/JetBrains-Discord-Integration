package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence

const val UPDATE_DELAY = 2000L

interface RPCConnection {
    val appId: Long

    val running: Boolean

    fun connect()
    fun disconnect()

    fun send(presence: RichPresence?)
}
