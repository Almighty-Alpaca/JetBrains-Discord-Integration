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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*
import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.types.*
import gamesdk.impl.Native
import gamesdk.impl.types.*
import gamesdk.impl.types.toDiscordCode

internal class DiscordLobbyTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyTransaction {
    override fun setType(type: DiscordLobbyType) = native_setType(type.toNativeDiscordLobbyType()).toDiscordCode()
    override fun setOwner(ownerId: DiscordUserId) = (native_setOwner(ownerId)).toDiscordCode()
    override fun setCapacity(capacity: uint32_t) = (native_setCapacity(capacity)).toDiscordCode()
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = (native_setMetadata(metadataKey, metadataValue)).toDiscordCode()
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = (native_deleteMetadata(metadataKey)).toDiscordCode()
    override fun setLocked(locked: Boolean) = (native_setLocked(locked)).toDiscordCode()

    private external fun native_setType(type: Int): Int
    private external fun native_setOwner(ownerId: DiscordUserId): Int
    private external fun native_setCapacity(capacity: uint32_t): Int
    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
    private external fun native_setLocked(locked: Boolean): Int
}

internal class DiscordLobbyMemberTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyMemberTransaction {
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = (native_setMetadata(metadataKey, metadataValue)).toDiscordCode()
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = (native_deleteMetadata(metadataKey)).toDiscordCode()

    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
}

internal class DiscordLobbySearchQueryImpl(private val internalThisPointer: Long) : DiscordLobbySearchQuery {
    override fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) =
        (native_filter(key, comparison.toNativeDiscordLobbySearchComparison(), cast.toNativeDiscordLobbySearchCast(), value)).toDiscordCode()

    override fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) = (native_sort(key, cast.toNativeDiscordLobbySearchCast(), value)).toDiscordCode()
    override fun limit(limit: uint32_t) = (native_limit(limit)).toDiscordCode()
    override fun distance(distance: DiscordLobbySearchDistance) = (native_distance(distance.toNativeDiscordLobbySearchDistance())).toDiscordCode()

    private external fun native_filter(key: DiscordMetadataKey, comparison: Int, cast: Int, value: DiscordMetadataValue): Int
    private external fun native_sort(key: DiscordMetadataKey, cast: Int, value: DiscordMetadataValue): Int
    private external fun native_limit(limit: uint32_t): Int
    private external fun native_distance(distance: Int): Int
}

internal class DiscordApplicationManagerImpl(private val internalThisPointer: Long) : DiscordApplicationManager {
    override fun validateOrExit(callback: (result: DiscordCode) -> Unit) = native_validateOrExit { callback(it.toDiscordCode()) }

    override fun getCurrentLocale() = native_getCurrentLocale()
    override fun getCurrentBranch() = native_getDiscordBranch()
    override fun getOAuth2Token() = native_getOAuth2Token()
    override fun getTicket(callback: (result: DiscordCode, ticket: String) -> Unit) = native_getTicked { result, ticket -> callback((result).toDiscordCode(), ticket) }

    private external fun native_validateOrExit(callback: (result: NativeDiscordCode) -> Unit)
    private external fun native_getCurrentLocale(): DiscordLocale
    private external fun native_getDiscordBranch(): DiscordBranch
    private external fun native_getOAuth2Token(): DiscordOAuth2Token
    private external fun native_getTicked(callback: (result: Int, ticket: String) -> Unit)
}

internal class DiscordUserManagerImpl(private val internalThisPointer: Long) : DiscordUserManager {
    override fun getCurrentUser() = native_getCurrentUser().mapFirst(NativeDiscordCode::toDiscordCode)
    override fun getUser(userId: DiscordUserId, callback: (result: DiscordCode, user: DiscordUser?) -> Unit) =
        native_getUser(userId) { result, user -> callback((result).toDiscordCode(), user) }

    override fun getCurrentUserPremiumType() = native_getCurrentUserPremiumType().mapFirst(NativeDiscordCode::toDiscordCode)
    override fun currentUserHasFlag(flag: DiscordUserFlag) = native_currentUserHasFlag(flag).mapFirst(NativeDiscordCode::toDiscordCode)

    private external fun native_getCurrentUser(): Pair<Int, DiscordUser?>
    private external fun native_getUser(userId: DiscordUserId, callback: (result: Int, user: DiscordUser?) -> Unit)
    private external fun native_getCurrentUserPremiumType(): Pair<Int, DiscordPremiumType?>
    private external fun native_currentUserHasFlag(flag: DiscordUserFlag): Pair<Int, Boolean>
}

internal class DiscordImageManagerImpl(private val internalThisPointer: Long) : DiscordImageManager {
    override fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: (result: DiscordCode, result_handle: DiscordImageHandle) -> Unit) =
        native_fetch(handle.toNativeDiscordImageHandle(), refresh) { result, resultHandle -> callback((result).toDiscordCode(), resultHandle.toDiscordImageHandle()) }

    override fun getDimensions(handle: DiscordImageHandle) = native_getDimensions(handle.toNativeDiscordImageHandle())
    override fun getData(handle: DiscordImageHandle, dataLength: uint32_t) = native_getData(handle.toNativeDiscordImageHandle(), dataLength)

    private external fun native_fetch(
        handle: NativeDiscordImageHandle, refresh: Boolean, callback: (
            result: Int, result_handle: NativeDiscordImageHandle
        ) -> Unit
    )

    private external fun native_getDimensions(handle: NativeDiscordImageHandle): DiscordImageDimensions
    private external fun native_getData(handle: NativeDiscordImageHandle, dataLength: uint32_t): Array<uint8_t>
}

internal class DiscordActivityManagerImpl(private val internalThisPointer: Long) : DiscordActivityManager {
    override fun registerCommand(command: String) = (native_registerCommand(command)).toDiscordCode()

    override fun registerSteam(steamId: uint32_t) = (native_registerSteam(steamId)).toDiscordCode()

    override fun updateActivity(activity: DiscordActivity, callback: (result: DiscordCode) -> Unit) = native_updateActivity(activity.toNativeDiscordActivity()) { callback(it.toDiscordCode()) }

    override fun clearActivity(callback: (result: DiscordCode) -> Unit) = native_clearActivity { callback(it.toDiscordCode()) }

    override fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: (result: DiscordCode) -> Unit) =
        native_sendRequestReply(userId, reply.toNativeDiscordActivityJoinRequestReply()) { callback(it.toDiscordCode()) }

    override fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: (result: DiscordCode) -> Unit) =
        native_sendInvite(userId, type.toNativeDiscordActivityActionType(), content) { callback(it.toDiscordCode()) }

    override fun acceptInvite(userId: DiscordUserId, callback: (result: DiscordCode) -> Unit) = native_acceptInvite(userId) { callback(it.toDiscordCode()) }

    private external fun native_registerCommand(command: String): Int
    private external fun native_registerSteam(steamId: uint32_t): Int
    private external fun native_updateActivity(activity: NativeDiscordActivity, callback: (result: NativeDiscordCode) -> Unit)
    private external fun native_clearActivity(callback: (result: NativeDiscordCode) -> Unit)
    private external fun native_sendRequestReply(userId: DiscordUserId, reply: Int, callback: (result: NativeDiscordCode) -> Unit)
    private external fun native_sendInvite(userId: DiscordUserId, type: Int, content: String, callback: (result: NativeDiscordCode) -> Unit)
    private external fun native_acceptInvite(userId: DiscordUserId, callback: (result: NativeDiscordCode) -> Unit)
}

internal class DiscordRelationshipManagerImpl(private val internalThisPointer: Long) : DiscordRelationshipManager {
    override fun filter(filter: DiscordRelationshipFilter) = native_filter(filter)
    override fun count() = native_count()
    override fun get(userId: DiscordUserId): Pair<DiscordCode, DiscordRelationship?> = native_get(userId).mapFirst(NativeDiscordCode::toDiscordCode)
    override fun getAt(index: uint32_t): Pair<DiscordCode, DiscordRelationship?> = native_getAt(index).mapFirst(NativeDiscordCode::toDiscordCode)

    private external fun native_filter(filter: DiscordRelationshipFilter)
    private external fun native_count(): Pair<DiscordCode, int32_t>
    private external fun native_get(userId: DiscordUserId): Pair<Int, DiscordRelationship?>
    private external fun native_getAt(index: uint32_t): Pair<Int, DiscordRelationship?>
}

internal class DiscordCoreImpl(private val internalThisPointer: Long) : DiscordCore {
    override val alive: Boolean
        get() = internalThisPointer != 0L

    override fun close() = native_destroy()
    override fun runCallbacks() = (native_runCallbacks()).toDiscordCode()
    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit) =
        native_setLogHook(minLevel.toNativeDiscordLogLevel()) { level, message -> hook(level.toDiscordLogLevel(), message) }

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
            Native.loadLibraries(DiscordCoreImpl::class.java.classLoader, "discord_game_sdk", "discord_game_sdk_kotlin")
        }

        fun create(clientId: DiscordClientId, flags: DiscordCreateFlags): Result<DiscordCoreImpl, DiscordCode> = with(native_create(clientId, flags.toNativeDiscordCreateFlags())) {
            return if (second == DiscordCode.Ok.toNativeDiscordCode()) Success(DiscordCoreImpl(first))
            else Failure(second.toDiscordCode())
        }

        @JvmStatic
        private external fun native_create(clientId: DiscordClientId, flags: Int): Pair<Long, Int>
    }
}
