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

package gamesdk.api

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*

@OptIn(ExperimentalUnsignedTypes::class)
typealias SteamId = UInt

interface ActivityManager : DiscordActivityManager, NativeObject {
    override fun registerCommand(command: String): DiscordResult
    override fun registerSteam(steamId: SteamId): DiscordResult

    suspend fun updateActivity(activity: DiscordActivity): DiscordResult
    override fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback)

    suspend fun clearActivity(): DiscordResult
    override fun clearActivity(callback: DiscordResultCallback)

    suspend fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply): DiscordResult
    override fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback)

    suspend fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String): DiscordResult
    override fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: DiscordResultCallback)

    suspend fun acceptInvite(userId: DiscordUserId): DiscordResult
    override fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback)
}
