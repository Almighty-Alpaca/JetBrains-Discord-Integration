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

package gamesdk.api.managers

import gamesdk.api.DiscordResult
import gamesdk.api.DiscordResultCallback
import gamesdk.api.NativeObject
import gamesdk.api.types.*

public interface ActivityManager : NativeObject {
    public fun registerCommand(command: String): DiscordResult

    public fun registerSteam(steamId: SteamId): DiscordResult

    public suspend fun updateActivity(activity: DiscordActivity): DiscordResult
    public fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback)

    public suspend fun clearActivity(): DiscordResult
    public fun clearActivity(callback: DiscordResultCallback)

    public suspend fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply): DiscordResult
    public fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback)

    public suspend fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String = ""): DiscordResult
    public fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String = "", callback: DiscordResultCallback)

    public suspend fun acceptInvite(userId: DiscordUserId): DiscordResult
    public fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback)
}
