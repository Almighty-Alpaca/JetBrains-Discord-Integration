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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.NativeRpcConnection
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection.RpcConnection
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val rpcService: RpcService
    get() = service()

@Service
class RpcService : DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var _user: User? = null
    val user: User
        get() = _user ?: User.CLYDE

    private var connection: RpcConnection? = null

    private var lastPresence: RichPresence? = null

    private var connectionChecker: Job? = null

    private fun checkConnected(): Job = launch {
        delay(20_000)

        synchronized(this@RpcService) {
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

    fun update(presence: RichPresence?) = update(presence, forceUpdate = false, forceReconnect = false)

    @Synchronized
    fun update(presence: RichPresence?, forceUpdate: Boolean = false, forceReconnect: Boolean = false) {
        if (Disposer.isDisposed(this) || (!forceUpdate && !forceReconnect && lastPresence == presence)) {
            return
        }

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
                    connection?.run(Disposer::dispose)
                    connection = null
                }

                connection = NativeRpcConnection(presence.appId) { user -> onReady(user) }.apply {
                    Disposer.register(this@RpcService, this@apply)
                    connect()
                }
                connectionChecker = checkConnected()

            }

            connection?.send(presence)
        }
    }

    override fun dispose() {
        update(null)

        super.dispose()
    }
}
