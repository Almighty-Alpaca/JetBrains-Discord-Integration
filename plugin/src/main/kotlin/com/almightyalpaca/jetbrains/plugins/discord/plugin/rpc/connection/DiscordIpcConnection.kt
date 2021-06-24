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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.UserCallback
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence.Builder
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import com.jagrosh.discordipc.exceptions.NoDiscordClientException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import com.jagrosh.discordipc.entities.User as UserEntity

class DiscordIpcConnection(override val appId: Long, private val userCallback: UserCallback) :
    DiscordConnection, DisposableCoroutineScope, IPCListener {
    override val parentJob: Job = SupervisorJob()

    private val mutex = Mutex()

    private var ipcClient: IPCClient = IPCClient(appId).apply {
        setListener(this@DiscordIpcConnection)
    }

    override val running
        get() = ipcClient.status == PipeStatus.CONNECTED

    override suspend fun connect(): Unit = mutex.withLock {
        if (ipcClient.status != PipeStatus.CONNECTED) {
            DiscordPlugin.LOG.debug("Starting new ipc connection")

            try {
                ipcClient.connect()
            } catch (ignored: NoDiscordClientException) {
                // Closed client can be ignored
            }
        }
    }

    @Synchronized
    override suspend fun send(presence: RichPresence?): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Sending new presence")

        if (running)
            ipcClient.sendRichPresence(presence?.toNative())
    }

    @Synchronized
    override suspend fun disconnect(): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Closing IPC connection")

        if (ipcClient.status == PipeStatus.CONNECTED)
            ipcClient.close()
    }

    override fun dispose() {
        runBlocking { disconnect() }

        super.dispose()
    }

    override fun onReady(client: IPCClient, user: UserEntity) {
        DiscordPlugin.LOG.info("IPC connected")

        userCallback(user.toGeneric())
    }

    override fun onClose(client: IPCClient, json: JSONObject) {
        onIPCDisconnect(json.toString())
    }

    override fun onDisconnect(client: IPCClient, t: Throwable) {
        onIPCDisconnect(t.localizedMessage)
    }

    private fun onIPCDisconnect(reason: String) {
        DiscordPlugin.LOG.info("IPC disconnected: $reason")

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
