/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordEventHandlers.OnReady
import club.minnced.discord.rpc.DiscordEventHandlers.OnStatus
import club.minnced.discord.rpc.DiscordRPC
import club.minnced.discord.rpc.DiscordRichPresence
import club.minnced.discord.rpc.DiscordUser
import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.UserCallback
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.scheduleWithFixedDelay
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class DiscordRpcConnection(
    override val appId: Long,
    private val userCallback: UserCallback
) : DiscordEventHandlers(), DiscordConnection, DisposableCoroutineScope {

    companion object {
        private var CONNECTED: AtomicReference<DiscordRpcConnection?> = AtomicReference(null)
        private val mutex = Mutex()
    }

    override val parentJob: Job = SupervisorJob()

    private var updateJob: Job? = null

    private lateinit var callbackRunner: ScheduledExecutorService

    init {
        ready = OnReady { user ->
            DiscordPlugin.LOG.info("Rpc connected, user: ${user.username}#${user.discriminator}")

            running = true
            userCallback(user.toGeneric())
        }
        disconnected = OnStatus { _, _ ->
            DiscordPlugin.LOG.info("Rpc disconnected")

            running = false
            userCallback(null)
        }
    }

    override var running: Boolean = false
        get() = field && CONNECTED.get() == this

    override suspend fun connect(): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Starting new rpc connection")

        if (DiscordRPC.INSTANCE == null) {
            DiscordPlugin.LOG.error("DiscordRPC library isn't loaded")
            throw IllegalStateException("DiscordRPC isn't loaded")
        }

        if (!CONNECTED.compareAndSet(null, this)) {
            DiscordPlugin.LOG.error("Another rpc connection is already running")
            throw IllegalStateException("Another rpc connection is already running")
        }

        DiscordRPC.INSTANCE.Discord_Initialize(appId.toString(), this, false, null)

        callbackRunner = Executors.newSingleThreadScheduledExecutor()
        callbackRunner.scheduleWithFixedDelay(delay = 2, unit = TimeUnit.SECONDS, action = this::runCallbacks)
    }

    private fun runCallbacks() {
        DiscordPlugin.LOG.debug("Running rpc callbacks")

        DiscordRPC.INSTANCE.Discord_RunCallbacks()
    }

    override suspend fun send(presence: RichPresence?): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Sending new presence")

        if (CONNECTED.get() != this) {
            DiscordPlugin.LOG.error("Can't send presence to inactive connection")

            return
        }

        updateJob?.cancel()

        updateJob = launch {
            delay(UPDATE_DELAY)

            when (presence) {
                null -> DiscordRPC.INSTANCE.Discord_ClearPresence()
                else -> DiscordRPC.INSTANCE.Discord_UpdatePresence(presence.toNative())
            }
        }
    }

    override suspend fun disconnect(): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Stopping rpc connection")

        if (CONNECTED.get() != this) {
            return
        }

        callbackRunner.shutdownNow()
        DiscordRPC.INSTANCE.Discord_Shutdown()

        CONNECTED.set(null)
    }

    override fun dispose() {
        runBlocking { disconnect() }

        super.dispose()
    }
}

private fun DiscordUser.toGeneric(): User = User.Normal(username, discriminator, userId.toLong(), avatar)

private fun RichPresence.toNative() = DiscordRichPresence().apply {
    this@toNative.state?.let { state = it }
    this@toNative.details?.let { details = it }
    this@toNative.startTimestamp?.toInstant()?.toEpochMilli()?.let { startTimestamp = it }
    this@toNative.endTimestamp?.toInstant()?.toEpochMilli()?.let { endTimestamp = it }
    this@toNative.largeImage?.key?.let { largeImageKey = it }
    this@toNative.largeImage?.text?.let { largeImageText = it }
    this@toNative.smallImage?.key?.let { smallImageKey = it }
    this@toNative.smallImage?.text?.let { smallImageText = it }
    this@toNative.partyId?.let { partyId = it }
    this@toNative.partySize.let { partySize = it }
    this@toNative.partyMax.let { partyMax = it }
    this@toNative.matchSecret?.let { matchSecret = it }
    this@toNative.joinSecret?.let { joinSecret = it }
    this@toNative.spectateSecret?.let { spectateSecret = it }
    this@toNative.instance.let {
        instance = when (it) {
            false -> 0
            true -> 1
        }
    }
}
