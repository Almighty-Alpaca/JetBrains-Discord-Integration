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

package gamesdk.impl

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.DeconstructedDiscordActivity
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.deconstruct
import gamesdk.api.ActivityManager
import gamesdk.api.DiscordResultCallback
import gamesdk.api.SteamId
import gamesdk.impl.utils.Native
import gamesdk.impl.utils.NativeObject

internal class NativeActivityManagerImpl(pointer: NativePointer, core: NativeCoreImpl) : NativeObject.Delegate(pointer, core), ActivityManager {
    override fun registerCommand(command: String): DiscordResult =
        native { pointer -> registerCommand(pointer, command).toDiscordResult() }

    override fun registerSteam(steamId: SteamId): DiscordResult =
        native { pointer -> registerSteam(pointer, steamId).toDiscordResult() }

    override suspend fun updateActivity(activity: DiscordActivity): DiscordResult =
        suspendCallback { callback -> updateActivity(activity, callback) }

    override fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback) =
        native { pointer -> updateActivity(pointer, activity.deconstruct(), callback.toNativeDiscordResultCallback()) }

    override suspend fun clearActivity(): DiscordResult =
        suspendCallback { callback -> clearActivity(callback) }

    override fun clearActivity(callback: DiscordResultCallback) =
        native { pointer -> clearActivity(pointer, callback.toNativeDiscordResultCallback()) }

    override suspend fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply): DiscordResult =
        suspendCallback { callback -> sendRequestReply(userId, reply, callback) }

    override fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback): Unit =
        native { pointer -> sendRequestReply(pointer, userId, reply.toNativeDiscordActivityJoinRequestReply(), callback.toNativeDiscordResultCallback()) }

    override suspend fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String): DiscordResult =
        suspendCallback { callback -> sendInvite(userId, type, content, callback) }

    override fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: DiscordResultCallback): Unit =
        native { pointer -> sendInvite(pointer, userId, type.toNativeDiscordActivityActionType(), content, callback.toNativeDiscordResultCallback()) }

    override suspend fun acceptInvite(userId: DiscordUserId): DiscordResult =
        suspendCallback { callback -> acceptInvite(userId, callback) }

    override fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback) =
        native { pointer -> acceptInvite(pointer, userId, callback.toNativeDiscordResultCallback()) }
}

private external fun Native.registerCommand(activityManagerPointer: NativePointer, command: String): Int

private external fun Native.registerSteam(activityManagerPointer: NativePointer, steamId: SteamId): Int

private external fun Native.updateActivity(activityManagerPointer: NativePointer, deconstructedActivity: DeconstructedDiscordActivity, callback: NativeDiscordResultCallback)

private external fun Native.clearActivity(activityManagerPointer: NativePointer, callback: NativeDiscordResultCallback)

private external fun Native.sendRequestReply(activityManagerPointer: NativePointer, userId: DiscordUserId, reply: NativeDiscordActivityJoinRequestReply, callback: NativeDiscordResultCallback)

private external fun Native.sendInvite(activityManagerPointer: NativePointer, userId: DiscordUserId, type: NativeDiscordActivityActionType, content: String, callback: NativeDiscordResultCallback)

private external fun Native.acceptInvite(activityManagerPointer: NativePointer, userId: DiscordUserId, callback: NativeDiscordResultCallback)
