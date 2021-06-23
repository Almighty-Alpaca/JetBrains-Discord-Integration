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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence.Builder
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference
import com.jagrosh.discordipc.entities.User as UserEntity

private var CONNECTED: AtomicReference<NativeRpcConnection?> = AtomicReference(null)

class NativeRpcConnection(override val appId: Long, private val userCallback: (User?) -> Unit) :
    RpcConnection, DisposableCoroutineScope, IPCListener {
    override val parentJob: Job = SupervisorJob()
    private var updateJob: Job? = null

    private lateinit var ipcClient: IPCClient

    override var running: Boolean = false
        get() = field && CONNECTED.get() == this

    @Synchronized
    override fun connect() {
        DiscordPlugin.LOG.debug("Starting new rpc connection")

        if (!CONNECTED.compareAndSet(null, this)) {
            DiscordPlugin.LOG.error("Another rpc connection is already running")
            throw IllegalStateException("Another rpc connection is already running")
        }

        ipcClient = IPCClient(appId)
        ipcClient.setListener(this)
        ipcClient.connect()
    }

    @Synchronized
    override fun send(presence: RichPresence?) {
        DiscordPlugin.LOG.debug("Sending new presence")

        if (CONNECTED.get() != this) {
            return DiscordPlugin.LOG.error("Can't send presence to inactive connection")
        }

        updateJob?.cancel()

        updateJob = launch {
            delay(UPDATE_DELAY)

            when (presence) {
                null -> ipcClient.sendRichPresence(null)
                else -> ipcClient.sendRichPresence(presence.toNative())
            }
        }
    }

    @Synchronized
    override fun disconnect() {
        DiscordPlugin.LOG.debug("Stopping rpc connection")

        if (CONNECTED.get() != this) {
            return
        }

        ipcClient.close()
        CONNECTED.set(null)
    }

    override fun dispose() {
        disconnect()

        super.dispose()
    }

    override fun onReady(client: IPCClient, user: UserEntity) {
        super.onReady(client, user)

        DiscordPlugin.LOG.info("Rpc connected")

        running = true
        userCallback(user.toGeneric())
    }

    override fun onClose(client: IPCClient, json: JSONObject) {
        super.onClose(client, json)

        DiscordPlugin.LOG.info("Rpc disconnected: $json")

        running = false
        userCallback(null)
    }

    override fun onDisconnect(client: IPCClient, t: Throwable) {
        super.onDisconnect(client, t)

        DiscordPlugin.LOG.info("Rpc disconnected ", t)

        running = false
        userCallback(null)
    }
}

private fun UserEntity.toGeneric() = User.Normal(name, discriminator, idLong, avatarId)

private fun RichPresence.toNative() = Builder().apply {
    this@toNative.state?.let { setState(it) }
    this@toNative.details?.let { setDetails(it) }
    this@toNative.startTimestamp?.let { setStartTimestamp(it) }
    this@toNative.endTimestamp?.let { setEndTimestamp(it) }
    this@toNative.largeImage?.key?.let { setLargeImage(it, this@toNative.largeImage?.text ?: "") }
    this@toNative.smallImage?.key?.let { setSmallImage(it, this@toNative.smallImage?.text ?: "") }

    setInstance(this@toNative.instance)
}.build()
