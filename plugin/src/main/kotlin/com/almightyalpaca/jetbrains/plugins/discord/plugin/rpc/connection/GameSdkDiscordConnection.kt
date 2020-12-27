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
import gamesdk.api.Core
import gamesdk.api.DiscordObjectResult
import gamesdk.api.ThreadedCore
import gamesdk.api.events.subscribe
import gamesdk.api.types.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GameSdkDiscordConnection(
    override val appId: Long,
    private val userCallback: (User?) -> Unit
) : DiscordConnection, DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    private var core: Core? = null

    private var updateJob: Job? = null

    override val running: Boolean
        get() = core != null

    private val mutex = Mutex()

    override suspend fun connect(): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Starting new rpc connection")

        if (core != null) {
            throw IllegalStateException("Already connected")
        }

        @OptIn(ExperimentalUnsignedTypes::class)
        when (val result = ThreadedCore.create(appId.toULong(), DiscordCreateFlags.NoRequireDiscord)) {
            is DiscordObjectResult.Failure -> throw RuntimeException("Error creating connection: " + result.code)
            is DiscordObjectResult.Success -> result.value.let { core ->
                this.core = core
                val userManager = core.userManager

                userManager.currentUserUpdates.subscribe {
                    when (val user = userManager.getCurrentUser()) {
                        is DiscordObjectResult.Success -> userCallback(user.value.toApplicationUser())
                        is DiscordObjectResult.Failure -> println("Error getting user: " + user.code)
                    }
                }
            }
        }
    }

    override suspend fun send(presence: RichPresence?): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Sending new presence")

        updateJob?.cancel()

        updateJob = launch {
            delay(UPDATE_DELAY)

            when (presence) {
                null -> core?.activityManager?.clearActivity()
                else -> core?.activityManager?.updateActivity(presence.toLibraryActivity())
            }
        }
    }

    override suspend fun disconnect(): Unit = mutex.withLock {
        DiscordPlugin.LOG.debug("Stopping rpc connection")

        core?.use {
            core = null
        }
    }

    override fun dispose() {
        runBlocking { disconnect() }

        super.dispose()

    }
}

private fun DiscordUser.toApplicationUser(): User = User.Normal(username, discriminator, id, avatar)

internal fun RichPresence.toLibraryActivity(): DiscordActivity = DiscordActivity(
    type = DiscordActivityType.Playing, // this.type.toDiscordActivityType(),
    applicationId = 0, // this.appId?.toLong()?:0,
    name = "", //  this.name,
    state = this.state ?: "",
    details = this.details ?: "",
    timestamps = DiscordActivityTimestamps(
        start = startTimestamp?.toInstant()?.toEpochMilli() ?: 0,
        end = endTimestamp?.toInstant()?.toEpochMilli() ?: 0
    ),
    assets = DiscordActivityAssets(
        large_image = this.largeImage?.key ?: "",
        large_text = this.largeImage?.text ?: "",
        small_image = this.smallImage?.key ?: "",
        small_text = this.smallImage?.text ?: ""
    ),
    party = DiscordActivityParty(
        id = this.partyId ?: "",
        size = DiscordPartySize(
            currentSize = this.partySize,
            maxSize = this.partyMax
        ),
        privacy = DiscordActivityPartyPrivacy.Private // , this.partyPrivacy.toDiscordActivityPartyPrivacy()
    ),
    secrets = DiscordActivitySecrets(
        match = this.matchSecret ?: "",
        join = this.joinSecret ?: "",
        spectate = this.spectateSecret ?: ""
    ),
    instance = this.instance
)
