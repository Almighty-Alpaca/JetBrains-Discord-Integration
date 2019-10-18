/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.User
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.NativeRPCConnection
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.RPCConnection
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RichPresenceServiceImpl : RichPresenceService, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private var _user: User? = null
    override val user: User
        get() = _user ?: User.CLYDE

    private var connection: RPCConnection? = null

    private var lastPresence: RichPresence? = null

    private var connectionChecker: Job? = null

    private fun checkConnected(): Job = launch {
        delay(20_000)

        synchronized(this@RichPresenceServiceImpl) {
            val connection = connection ?: return@launch

            if (!connection.running) {
                update(lastPresence, forceReconnect = true)
            } else {
                checkConnected()
            }
        }
    }

    private fun onReady(user: User) {
        _user = user
        update(lastPresence, forceUpdate = true)
    }

    override fun update(presence: RichPresence?) = update(presence, forceUpdate = false, forceReconnect = false)

    @Synchronized
    fun update(presence: RichPresence?, forceUpdate: Boolean = false, forceReconnect: Boolean = false) {
        log { "RichPresenceServiceImpl#update()" }

        if (!forceUpdate && !forceReconnect && lastPresence == presence)
            return

        lastPresence = presence

        if (presence?.appId == null) { // Stop connection
            if (connection != null) {
                connectionChecker?.cancel()
                connectionChecker = null
                connection?.disconnect()
                connection = null
            }
        } else {
            if (forceReconnect || connection?.appId != presence.appId) {
                if (connection != null) {
                    connectionChecker?.cancel()
                    connectionChecker = null
                    connection?.disconnect()
                    connection = null
                }
                connection = NativeRPCConnection(presence.appId) { user -> onReady(user) }
                connection?.connect()
                connectionChecker = checkConnected()
            }

            connection?.send(presence)
        }
    }

    companion object : Logging()
}
