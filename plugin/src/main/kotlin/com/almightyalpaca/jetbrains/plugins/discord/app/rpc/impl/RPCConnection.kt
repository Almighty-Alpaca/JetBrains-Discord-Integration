package com.almightyalpaca.jetbrains.plugins.discord.app.rpc.impl

import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.app.logging.Logging
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.entities.DiscordBuild
import com.jagrosh.discordipc.entities.RichPresence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import kotlin.coroutines.CoroutineContext

private val UPDATE_DELAY = Duration.ofSeconds(2)!!

class RPCConnection(val appId: Long) : CoroutineScope {
    private val parentJob: Job by lazy { Job() }
    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Default + parentJob }

    private var rpc: IPCClient = IPCClient(appId)

    private var updateJob: Job? = null

    fun connect() {
        Logger.Level.TRACE { "RPCConnection($appId)#connect()" }

        rpc.connect(DiscordBuild.ANY)
    }

    @Synchronized
    fun send(presence: RichPresence?) {
        Logger.Level.TRACE { "RPCConnection($appId)#send()" }

        updateJob?.cancel()

        updateJob = launch {
            delay(UPDATE_DELAY)

            rpc.sendRichPresence(presence)
        }
    }

    fun disconnect() {
        Logger.Level.TRACE { "RPCConnection($appId)#disconnect()" }

        parentJob.cancel()

        rpc.close()
    }

    companion object : Logging()
}
