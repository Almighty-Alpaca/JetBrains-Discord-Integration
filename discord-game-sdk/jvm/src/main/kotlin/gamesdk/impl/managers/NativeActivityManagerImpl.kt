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

package gamesdk.impl.managers

import gamesdk.api.DiscordResult
import gamesdk.api.DiscordResultCallback
import gamesdk.api.managers.ActivityManager
import gamesdk.api.types.*
import gamesdk.impl.*
import gamesdk.impl.types.*

internal class NativeActivityManagerImpl(core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), ActivityManager {
    override fun registerCommand(command: String): DiscordResult =
        native { pointer -> registerCommand(pointer, command.toNativeString()).toDiscordResult() }

    override fun registerSteam(steamId: SteamId): DiscordResult =
        native { pointer -> registerSteam(pointer, steamId).toDiscordResult() }

    override suspend fun updateActivity(activity: DiscordActivity): DiscordResult =
        suspendCallback { callback -> updateActivity(activity, callback) }

    override fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback) =
        native { pointer -> updateActivity(pointer, activity.toNativeDiscordActivity(), callback.toNativeDiscordResultCallback()) }

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
        native { pointer -> sendInvite(pointer, userId, type.toNativeDiscordActivityActionType(), content.toNativeString(), callback.toNativeDiscordResultCallback()) }

    override suspend fun acceptInvite(userId: DiscordUserId): DiscordResult =
        suspendCallback { callback -> acceptInvite(userId, callback) }

    override fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback) =
        native { pointer -> acceptInvite(pointer, userId, callback.toNativeDiscordResultCallback()) }
}

private external fun Native.registerCommand(pointer: NativePointer, command: NativeString): Int

private external fun Native.registerSteam(pointer: NativePointer, steamId: SteamId): Int

private external fun Native.updateActivity(pointer: NativePointer, deconstructedActivity: NativeDiscordActivity, callback: NativeDiscordResultCallback)

private external fun Native.clearActivity(pointer: NativePointer, callback: NativeDiscordResultCallback)

private external fun Native.sendRequestReply(pointer: NativePointer, userId: DiscordUserId, reply: NativeDiscordActivityJoinRequestReply, callback: NativeDiscordResultCallback)

private external fun Native.sendInvite(pointer: NativePointer, userId: DiscordUserId, type: NativeDiscordActivityActionType, content: NativeString, callback: NativeDiscordResultCallback)

private external fun Native.acceptInvite(pointer: NativePointer, userId: DiscordUserId, callback: NativeDiscordResultCallback)
