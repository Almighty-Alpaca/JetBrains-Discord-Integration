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

@file:Suppress("FunctionName", "unused")

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Failure
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Result
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Success
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.mapFirst
import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.DiscordResultCallback
import gamesdk.impl.NativeDiscordResultCallback
import gamesdk.impl.toNativeDiscordResultCallback
import gamesdk.impl.utils.NativeLoader

class DiscordLobbyTransactionImpl internal constructor(private val internalThisPointer: Long) : DiscordLobbyTransaction {
    override fun setType(type: DiscordLobbyType) = DiscordResult.fromInt(native_setType(type.toInt()))
    override fun setOwner(ownerId: DiscordUserId) = DiscordResult.fromInt(native_setOwner(ownerId))
    override fun setCapacity(capacity: uint32_t) = DiscordResult.fromInt(native_setCapacity(capacity))
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = DiscordResult.fromInt(native_setMetadata(metadataKey, metadataValue))
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = DiscordResult.fromInt(native_deleteMetadata(metadataKey))
    override fun setLocked(locked: Boolean) = DiscordResult.fromInt(native_setLocked(locked))

    private external fun native_setType(type: Int): Int
    private external fun native_setOwner(ownerId: DiscordUserId): Int
    private external fun native_setCapacity(capacity: uint32_t): Int
    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
    private external fun native_setLocked(locked: Boolean): Int
}

class DiscordLobbyMemberTransactionImpl internal constructor(private val internalThisPointer: Long) : DiscordLobbyMemberTransaction {
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = DiscordResult.fromInt(native_setMetadata(metadataKey, metadataValue))
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = DiscordResult.fromInt(native_deleteMetadata(metadataKey))

    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
}

class DiscordLobbySearchQueryImpl internal constructor(private val internalThisPointer: Long) : DiscordLobbySearchQuery {
    override fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) =
        DiscordResult.fromInt(native_filter(key, comparison.toInt(), cast.toInt(), value))

    override fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) = DiscordResult.fromInt(native_sort(key, cast.toInt(), value))
    override fun limit(limit: uint32_t) = DiscordResult.fromInt(native_limit(limit))
    override fun distance(distance: DiscordLobbySearchDistance) = DiscordResult.fromInt(native_distance(distance.toInt()))

    private external fun native_filter(key: DiscordMetadataKey, comparison: Int, cast: Int, value: DiscordMetadataValue): Int
    private external fun native_sort(key: DiscordMetadataKey, cast: Int, value: DiscordMetadataValue): Int
    private external fun native_limit(limit: uint32_t): Int
    private external fun native_distance(distance: Int): Int
}

class DiscordApplicationManagerImpl internal constructor(private val internalThisPointer: Long) : DiscordApplicationManager {
    override fun validateOrExit(callback: DiscordResultCallback) = native_validateOrExit(callback.toNativeDiscordResultCallback())

    override fun getCurrentLocale() = native_getCurrentLocale()
    override fun getCurrentBranch() = native_getDiscordBranch()
    override fun getOAuth2Token() = native_getOAuth2Token()
    override fun getTicket(callback: (result: DiscordResult, ticket: String) -> Unit) = native_getTicked { result, ticket -> callback(DiscordResult.fromInt(result), ticket) }

    private external fun native_validateOrExit(callback: NativeDiscordResultCallback)
    private external fun native_getCurrentLocale(): DiscordLocale
    private external fun native_getDiscordBranch(): DiscordBranch
    private external fun native_getOAuth2Token(): DiscordOAuth2Token
    private external fun native_getTicked(callback: (result: Int, ticket: String) -> Unit)
}

class DiscordUserManagerImpl internal constructor(private val internalThisPointer: Long) : DiscordUserManager {
    override fun getCurrentUser() = native_getCurrentUser().mapFirst(DiscordResult.Companion::fromInt)
    override fun getUser(userId: DiscordUserId, callback: (result: DiscordResult, user: DiscordUser?) -> Unit) =
        native_getUser(userId) { result, user -> callback(DiscordResult.fromInt(result), user) }

    override fun getCurrentUserPremiumType() = native_getCurrentUserPremiumType().mapFirst(DiscordResult.Companion::fromInt)
    override fun currentUserHasFlag(flag: DiscordUserFlag) = native_currentUserHasFlag(flag).mapFirst(DiscordResult.Companion::fromInt)

    private external fun native_getCurrentUser(): Pair<Int, DiscordUser?>
    private external fun native_getUser(userId: DiscordUserId, callback: (result: Int, user: DiscordUser?) -> Unit)
    private external fun native_getCurrentUserPremiumType(): Pair<Int, DiscordPremiumType?>
    private external fun native_currentUserHasFlag(flag: DiscordUserFlag): Pair<Int, Boolean>
}

class DiscordImageManagerImpl internal constructor(private val internalThisPointer: Long) : DiscordImageManager {
    override fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: (result: DiscordResult, result_handle: DiscordImageHandle) -> Unit) =
        native_fetch(handle.deconstruct(), refresh) { result, resultHandle -> callback(DiscordResult.fromInt(result), resultHandle.construct()) }

    override fun getDimensions(handle: DiscordImageHandle) = native_getDimensions(handle.deconstruct())
    override fun getData(handle: DiscordImageHandle, dataLength: uint32_t) = native_getData(handle.deconstruct(), dataLength)

    private external fun native_fetch(
        handle: DeconstructedDiscordImageHandle, refresh: Boolean, callback: (
            result: Int, result_handle: DeconstructedDiscordImageHandle
        ) -> Unit
    )

    private external fun native_getDimensions(handle: DeconstructedDiscordImageHandle): DiscordImageDimensions
    private external fun native_getData(handle: DeconstructedDiscordImageHandle, dataLength: uint32_t): Array<uint8_t>
}

class DiscordActivityManagerImpl internal constructor(private val internalThisPointer: Long) : DiscordActivityManager {
    override fun registerCommand(command: String) = DiscordResult.fromInt(native_registerCommand(command))

    override fun registerSteam(steamId: uint32_t) = DiscordResult.fromInt(native_registerSteam(steamId))

    override fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback) = native_updateActivity(activity.deconstruct(), callback.toNativeDiscordResultCallback())

    override fun clearActivity(callback: DiscordResultCallback) = native_clearActivity(callback.toNativeDiscordResultCallback())

    override fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback) = native_sendRequestReply(userId, reply.toInt(), callback.toNativeDiscordResultCallback())

    override fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: DiscordResultCallback) =
        native_sendInvite(userId, type.toInt(), content, callback.toNativeDiscordResultCallback())

    override fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback) = native_acceptInvite(userId, callback.toNativeDiscordResultCallback())

    private external fun native_registerCommand(command: String): Int
    private external fun native_registerSteam(steamId: uint32_t): Int
    private external fun native_updateActivity(activity: DeconstructedDiscordActivity, callback: NativeDiscordResultCallback)
    private external fun native_clearActivity(callback: NativeDiscordResultCallback)
    private external fun native_sendRequestReply(userId: DiscordUserId, reply: Int, callback: NativeDiscordResultCallback)
    private external fun native_sendInvite(userId: DiscordUserId, type: Int, content: String, callback: NativeDiscordResultCallback)
    private external fun native_acceptInvite(userId: DiscordUserId, callback: NativeDiscordResultCallback)
}

class DiscordRelationshipManagerImpl internal constructor(private val internalThisPointer: Long) : DiscordRelationshipManager {
    override fun filter(filter: DiscordRelationshipFilter) = native_filter(filter)
    override fun count() = native_count()
    override fun get(userId: DiscordUserId): Pair<DiscordResult, DiscordRelationship?> = native_get(userId).mapFirst(DiscordResult.Companion::fromInt)
    override fun getAt(index: uint32_t): Pair<DiscordResult, DiscordRelationship?> = native_getAt(index).mapFirst(DiscordResult.Companion::fromInt)

    private external fun native_filter(filter: DiscordRelationshipFilter)
    private external fun native_count(): Pair<DiscordResult, int32_t>
    private external fun native_get(userId: DiscordUserId): Pair<Int, DiscordRelationship?>
    private external fun native_getAt(index: uint32_t): Pair<Int, DiscordRelationship?>
}

class DiscordCoreImpl internal constructor(private val internalThisPointer: Long) : DiscordCore {
    override fun isValid() = internalThisPointer != 0L
    override fun destroy() = native_destroy()
    override fun runCallbacks() = DiscordResult.fromInt(native_runCallbacks())
    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit) =
        native_setLogHook(minLevel.toInt()) { level, message -> hook(DiscordLogLevel.fromInt(level), message) }

    override fun getApplicationManager() = DiscordApplicationManagerImpl(native_getApplicationManager())
    override fun getUserManager() = DiscordUserManagerImpl(native_getUserManager())
    override fun getImageManager() = DiscordImageManagerImpl(native_getImageManager())
    override fun getActivityManager() = DiscordActivityManagerImpl(native_getActivityManager())
    override fun getRelationshipManager() = DiscordRelationshipManagerImpl(native_getRelationshipManager())

    override fun getLobbyManager(): DiscordLobbyManager = TODO("Not yet implemented")
    override fun getNetworkManager(): DiscordNetworkManager = TODO("Not yet implemented")
    override fun getOverlayManager(): DiscordOverlayManager = TODO("Not yet implemented")
    override fun getStorageManager(): DiscordStorageManager = TODO("Not yet implemented")
    override fun getStoreManager(): DiscordStoreManager = TODO("Not yet implemented")
    override fun getVoiceManager(): DiscordVoiceManager = TODO("Not yet implemented")
    override fun getAchievementManager(): DiscordAchievementManager = TODO("Not yet implemented")

    private external fun native_destroy()
    private external fun native_runCallbacks(): Int
    private external fun native_setLogHook(minLevel: Int, function: (level: Int, message: String) -> Unit)
    private external fun native_getApplicationManager(): Long
    private external fun native_getUserManager(): Long
    private external fun native_getImageManager(): Long
    private external fun native_getActivityManager(): Long
    private external fun native_getRelationshipManager(): Long
    private external fun native_getLobbyManager(): Long
    private external fun native_getNetworkManager(): Long
    private external fun native_getOverlayManager(): Long
    private external fun native_getStorageManager(): Long
    private external fun native_getStoreManager(): Long
    private external fun native_getVoiceManager(): Long
    private external fun native_getAchievementManager(): Long

    companion object {
        init {
            NativeLoader.loadLibraries(DiscordCoreImpl::class.java.classLoader, "discord_game_sdk", "discord_game_sdk_cpp", "discord_game_sdk_kotlin")
        }

        fun create(clientId: DiscordClientId, flags: DiscordCreateFlags): Result<DiscordCoreImpl, DiscordResult> = with(native_create(clientId, flags.toInt())) {
            return if (second == DiscordResult.Ok.toInt()) Success(DiscordCoreImpl(first))
            else Failure(DiscordResult.fromInt(second))
        }

        @JvmStatic
        private external fun native_create(clientId: DiscordClientId, flags: Int): Pair<Long, Int>
    }
}
