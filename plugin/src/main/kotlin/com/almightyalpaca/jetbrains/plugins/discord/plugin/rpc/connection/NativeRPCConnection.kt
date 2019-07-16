/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

private var CONNECTED: AtomicReference<NativeRPCConnection?> = AtomicReference(null)

class NativeRPCConnection(override val appId: Long, private val userCallback: (User) -> Unit) : DiscordEventHandlers(), RPCConnection, CoroutineScope {
    private val parentJob: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private var updateJob: Job? = null

    private lateinit var callbackRunner: ScheduledExecutorService

    init {
        ready = OnReady { user ->
            running = true
            userCallback(User.fromLibraryObject(user))
        }
        disconnected = OnStatus { _, _ ->
            running = false
        }
    }

    override var running: Boolean = false
        get() = field && CONNECTED.get() == this

    @Synchronized
    override fun connect() {
        log { "NativeRPCConnection($appId)#connect()" }

        if (!CONNECTED.compareAndSet(null, this)) {
            throw Exception("There can only be one connected RPC connection")
        }

        DiscordRPC.INSTANCE.Discord_Initialize(appId.toString(), this, false, null)

        callbackRunner = Executors.newSingleThreadScheduledExecutor()
        callbackRunner.scheduleAtFixedRate(DiscordRPC.INSTANCE::Discord_RunCallbacks, 2, 2, TimeUnit.SECONDS)
    }

    @Synchronized
    override fun send(presence: RichPresence?) {
        log { "NativeRPCConnection($appId)#send()" }

        if (CONNECTED.get() != this) {
            return
        }

        updateJob?.cancel()

        updateJob = launch {
            delay(UPDATE_DELAY)

            when (presence) {
                null -> DiscordRPC.INSTANCE.Discord_ClearPresence()
                else -> DiscordRPC.INSTANCE.Discord_UpdatePresence(presence.toLibraryObject())
            }
        }
    }

    @Synchronized
    override fun disconnect() {
        log { "NativeRPCConnection($appId)#disconnect()" }

        if (CONNECTED.get() != this) {
            throw Exception("RPC connection not connected")
        }

        parentJob.cancel()
        callbackRunner.shutdownNow()
        DiscordRPC.INSTANCE.Discord_Shutdown()

        CONNECTED.set(null)
    }

    companion object : Logging()
}

private fun User.Companion.fromLibraryObject(user: DiscordUser): User = User.Normal(user.username, user.discriminator, user.userId.toLong(), user.avatar)

private fun RichPresence.toLibraryObject() = DiscordRichPresence().apply {
    this@toLibraryObject.state?.let { state = it }
    this@toLibraryObject.details?.let { details = it }
    this@toLibraryObject.startTimestamp?.toInstant()?.toEpochMilli()?.let { startTimestamp = it }
    this@toLibraryObject.endTimestamp?.toInstant()?.toEpochMilli()?.let { endTimestamp = it }
    this@toLibraryObject.largeImage?.key?.let { largeImageKey = it }
    this@toLibraryObject.largeImage?.text?.let { largeImageText = it }
    this@toLibraryObject.smallImage?.key?.let { smallImageKey = it }
    this@toLibraryObject.smallImage?.text?.let { smallImageText = it }
    this@toLibraryObject.partyId?.let { partyId = it }
    this@toLibraryObject.partySize.let { partySize = it }
    this@toLibraryObject.partyMax.let { partyMax = it }
    this@toLibraryObject.matchSecret?.let { matchSecret = it }
    this@toLibraryObject.joinSecret?.let { joinSecret = it }
    this@toLibraryObject.spectateSecret?.let { spectateSecret = it }
    this@toLibraryObject.instance.let {
        instance = when (it) {
            false -> 0
            true -> 1
        }
    }
}
