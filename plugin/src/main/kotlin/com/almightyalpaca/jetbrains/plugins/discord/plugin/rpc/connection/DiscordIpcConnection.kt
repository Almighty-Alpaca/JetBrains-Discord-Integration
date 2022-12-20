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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.errorLazy
import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.core.event.impl.CurrentUserUpdateEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ErrorEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ReadyEvent
import dev.cbyrne.kdiscordipc.data.activity.activity
import dev.cbyrne.kdiscordipc.data.activity.largeImage
import dev.cbyrne.kdiscordipc.data.activity.smallImage
import dev.cbyrne.kdiscordipc.data.activity.timestamps
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import dev.cbyrne.kdiscordipc.data.user.User as NativeUser

class DiscordIpcConnection(override val appId: Long, private val userCallback: UserCallback) :
    DiscordConnection, DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var ipcClient: KDiscordIPC = KDiscordIPC(appId.toString()).apply {
        // listening to an event doesn't actually do anything async, so we can just block here
        runBlocking {
            on<ReadyEvent>(::onReady)
            on<ErrorEvent>(::onError)
            on<CurrentUserUpdateEvent>(::onCurrentUserUpdate)
        }
//        setListener(this@DiscordIpcConnection)
    }

    override val running by ipcClient::connected

    override suspend fun connect() {
        if (!ipcClient.connected) {
            DiscordPlugin.LOG.debug("Starting new ipc connection")

            launch { ipcClient.connect() }

            DiscordPlugin.LOG.debug("Started new ipc connection")
        }
    }

    override suspend fun send(presence: RichPresence?) {
        DiscordPlugin.LOG.debug("Sending new presence")

        if (running)
            ipcClient.activityManager.setActivity(presence?.toNative())
    }

    override suspend fun disconnect() = disconnectInternal()

    private fun disconnectInternal() {
        DiscordPlugin.LOG.debug("Closing IPC connection")

        ipcClient.disconnect()
    }

    override fun dispose() {
        disconnectInternal()

        super.dispose()
    }

    private fun onReady(event: ReadyEvent) {
        DiscordPlugin.LOG.info("IPC connected")

        userCallback(event.data.user.toGeneric())
    }

    private fun onError(event: ErrorEvent) {
        DiscordPlugin.LOG.errorLazy { "IPC error: ${event.data}" }
    }

    private fun onCurrentUserUpdate(event: CurrentUserUpdateEvent) {
        userCallback(event.data.toGeneric())
    }

    // TODO: Register once the library exposes this again
    // private fun onDisconnect(reason: String) {
    //     DiscordPlugin.LOG.info("IPC disconnected: $reason")
    //
    //     userCallback(null)
    // }
}

private fun NativeUser.toGeneric() = User.Normal(this.username, discriminator, this.id.toLong(), this.avatar)

private fun stateDetails(s: String?): String {
    if (s == null || s.length < 2) return "  "
    return s
}

private fun RichPresence.toNative() = activity(
    // kdiscordipc has them backwards
    stateDetails(this@toNative.details),
    stateDetails(this@toNative.state),
) {

    this@toNative.startTimestamp?.let {
        this.timestamps(
            start = it.toEpochSecond(),
            end = this@toNative.endTimestamp?.toEpochSecond()
        )
    }

    this@toNative.largeImage?.key?.let { this.largeImage(it, this@toNative.largeImage?.text ?: "") }
    this@toNative.smallImage?.key?.let { this.smallImage(it, this@toNative.smallImage?.text ?: "") }

    this.instance = this@toNative.instance
}
