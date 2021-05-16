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
import gamesdk.api.DiscordObjectResult
import gamesdk.api.DiscordObjectResultCallback
import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.DiscordResultCallback
import gamesdk.api.types.*
import gamesdk.impl.*
import gamesdk.impl.types.*
import java.nio.ByteBuffer

internal class DiscordLobbyTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyTransaction {
    override fun setType(type: DiscordLobbyType) = native_setType(type.toNativeDiscordLobbyType()).toDiscordCode()
    override fun setOwner(ownerId: DiscordUserId) = (native_setOwner(ownerId)).toDiscordCode()
    override fun setCapacity(capacity: uint32_t) = (native_setCapacity(capacity)).toDiscordCode()
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = (native_setMetadata(metadataKey.toNativeString(), metadataValue.toNativeString())).toDiscordCode()
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = (native_deleteMetadata(metadataKey.toNativeString())).toDiscordCode()
    override fun setLocked(locked: Boolean) = (native_setLocked(locked)).toDiscordCode()

    private external fun native_setType(type: NativeDiscordLobbyType): NativeDiscordResult
    private external fun native_setOwner(ownerId: NativeDiscordUserId): NativeDiscordResult
    private external fun native_setCapacity(capacity: uint32_t): NativeDiscordResult
    private external fun native_setMetadata(metadataKey: NativeDiscordMetadataKey, metadataValue: NativeDiscordMetadataValue): NativeDiscordResult
    private external fun native_deleteMetadata(metadataKey: NativeDiscordMetadataKey): NativeDiscordResult
    private external fun native_setLocked(locked: Boolean): NativeDiscordResult
}

internal class DiscordLobbyMemberTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyMemberTransaction {
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = (native_setMetadata(metadataKey.toNativeString(), metadataValue.toNativeString())).toDiscordCode()
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = (native_deleteMetadata(metadataKey.toNativeString())).toDiscordCode()

    private external fun native_setMetadata(metadataKey: NativeDiscordMetadataKey, metadataValue: NativeDiscordMetadataValue): NativeDiscordResult
    private external fun native_deleteMetadata(metadataKey: NativeDiscordMetadataKey): NativeDiscordResult
}

internal class DiscordLobbySearchQueryImpl(private val internalThisPointer: Long) : DiscordLobbySearchQuery {
    override fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) =
        (native_filter(key.toNativeString(), comparison.toNativeDiscordLobbySearchComparison(), cast.toNativeDiscordLobbySearchCast(), value.toNativeString())).toDiscordCode()

    override fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) = (native_sort(key.toNativeString(), cast.toNativeDiscordLobbySearchCast(), value.toNativeString())).toDiscordCode()
    override fun limit(limit: uint32_t) = (native_limit(limit)).toDiscordCode()
    override fun distance(distance: DiscordLobbySearchDistance) = (native_distance(distance.toNativeDiscordLobbySearchDistance())).toDiscordCode()

    private external fun native_filter(key: NativeDiscordMetadataKey, comparison: Int, cast: Int, value: NativeDiscordMetadataValue): NativeDiscordResult
    private external fun native_sort(key: NativeDiscordMetadataKey, cast: Int, value: NativeDiscordMetadataValue): NativeDiscordResult
    private external fun native_limit(limit: uint32_t): NativeDiscordResult
    private external fun native_distance(distance: NativeDiscordLobbySearchDistance): NativeDiscordResult
}

internal class DiscordApplicationManagerImpl(private val internalThisPointer: Long) : DiscordApplicationManager {
    override fun validateOrExit(callback: DiscordResultCallback) = native_validateOrExit { callback(it.toDiscordResult()) }

    override fun getCurrentLocale() = native_getCurrentLocale()
    override fun getCurrentBranch() = native_getDiscordBranch()
    override fun getOAuth2Token(callback: DiscordObjectResultCallback<DiscordOAuth2Token>) = native_getOAuth2Token { callback(it.toDiscordObjectResult(NativeDiscordOAuth2Token::toDiscordOAuth2Token)) }

    override fun getTicket(callback: DiscordObjectResultCallback<String>) = native_getTicket { callback(it.toDiscordObjectResult()) }

    private external fun native_validateOrExit(callback: NativeDiscordResultCallback)
    private external fun native_getCurrentLocale(): DiscordLocale
    private external fun native_getDiscordBranch(): DiscordBranch
    private external fun native_getOAuth2Token(callback: NativeDiscordObjectResultCallback<NativeDiscordOAuth2Token>)
    private external fun native_getTicket(callback: NativeDiscordObjectResultCallback<String>)
}

internal class DiscordUserManagerImpl(private val internalThisPointer: Long) : DiscordUserManager {
    override fun getCurrentUser(): DiscordObjectResult<DiscordUser> = native_getCurrentUser().toDiscordObjectResult(NativeDiscordUser::toDiscordUser)
    override fun getUser(userId: DiscordUserId, callback: DiscordObjectResultCallback<DiscordUser>) = native_getUser(userId) { callback(it.toDiscordObjectResult(NativeDiscordUser::toDiscordUser)) }

    override fun getCurrentUserPremiumType() = native_getCurrentUserPremiumType().toDiscordObjectResult(NativeDiscordPremiumType::toDiscordPremiumType)
    override fun currentUserHasFlag(flag: DiscordUserFlag) = native_currentUserHasFlag(1 shl flag.offset).toDiscordObjectResult()

    private external fun native_getCurrentUser(): NativeDiscordObjectResult<NativeDiscordUser>
    private external fun native_getUser(userId: DiscordUserId, callback: NativeDiscordObjectResultCallback<NativeDiscordUser>)
    private external fun native_getCurrentUserPremiumType(): NativeDiscordObjectResult<NativeDiscordPremiumType>
    private external fun native_currentUserHasFlag(flag: Int): NativeDiscordObjectResult<Boolean>
}

internal class DiscordImageManagerImpl(private val internalThisPointer: Long) : DiscordImageManager {
    override fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: (result: DiscordObjectResult<DiscordImageHandle>) -> Unit) =
        native_fetch(handle.toNativeDiscordImageHandle(), refresh) { result -> callback(result.toDiscordObjectResult { it.toDiscordImageHandle() }) }

    override fun getDimensions(handle: DiscordImageHandle) = native_getDimensions(handle.toNativeDiscordImageHandle())
    override fun getData(handle: DiscordImageHandle, dataLength: uint32_t) = native_getData(handle.toNativeDiscordImageHandle(), dataLength)

    private external fun native_fetch(handle: NativeDiscordImageHandle, refresh: Boolean, callback: NativeDiscordObjectResultCallback<NativeDiscordImageHandle>)

    private external fun native_getDimensions(handle: NativeDiscordImageHandle): DiscordImageDimensions
    private external fun native_getData(handle: NativeDiscordImageHandle, dataLength: uint32_t): ByteBuffer
}

internal class DiscordActivityManagerImpl(private val internalThisPointer: Long) : DiscordActivityManager {
    override fun registerCommand(command: String) = native_registerCommand(command).toDiscordCode()

    override fun registerSteam(steamId: uint32_t) = native_registerSteam(steamId).toDiscordCode()

    override fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback) = native_updateActivity(activity.toNativeDiscordActivity()) { callback(it.toDiscordResult()) }

    override fun clearActivity(callback: DiscordResultCallback) = native_clearActivity { callback(it.toDiscordResult()) }

    override fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback) =
        native_sendRequestReply(userId, reply.toNativeDiscordActivityJoinRequestReply()) { callback(it.toDiscordResult()) }

    override fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: DiscordResultCallback) =
        native_sendInvite(userId, type.toNativeDiscordActivityActionType(), content) { callback(it.toDiscordResult()) }

    override fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback) = native_acceptInvite(userId) { callback(it.toDiscordResult()) }

    private external fun native_registerCommand(command: String): NativeDiscordResult
    private external fun native_registerSteam(steamId: uint32_t): NativeDiscordResult
    private external fun native_updateActivity(activity: NativeDiscordActivity, callback: NativeDiscordResultCallback)
    private external fun native_clearActivity(callback: NativeDiscordResultCallback)
    private external fun native_sendRequestReply(userId: NativeDiscordUserId, reply: NativeDiscordActivityJoinRequestReply, callback: NativeDiscordResultCallback)
    private external fun native_sendInvite(userId: NativeDiscordUserId, type: NativeDiscordActivityType, content: String, callback: NativeDiscordResultCallback)
    private external fun native_acceptInvite(userId: NativeDiscordUserId, callback: NativeDiscordResultCallback)
}

internal class DiscordRelationshipManagerImpl(private val internalThisPointer: Long) : DiscordRelationshipManager {
    override fun filter(filter: DiscordRelationshipFilter) = native_filter(filter)
    override fun count() = native_count().toDiscordObjectResult()
    override fun get(userId: DiscordUserId) = native_get(userId).toDiscordObjectResult()
    override fun getAt(index: uint32_t) = native_getAt(index).toDiscordObjectResult()

    private external fun native_filter(filter: DiscordRelationshipFilter)
    private external fun native_count(): NativeDiscordObjectResult<int32_t>
    private external fun native_get(userId: NativeDiscordUserId): NativeDiscordObjectResult<DiscordRelationship>
    private external fun native_getAt(index: uint32_t): NativeDiscordObjectResult<DiscordRelationship>
}

internal class DiscordCoreImpl(private val internalThisPointer: Long) : DiscordCore {
    override val alive: Boolean get() = internalThisPointer != 0L

    override fun close() = native_destroy()
    override fun runCallbacks() = (native_runCallbacks()).toDiscordCode()
    override fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit) =
        native_setLogHook(minLevel.toNativeDiscordLogLevel()) { level, message -> hook(level.toDiscordLogLevel(), message) }

    override val applicationManager = DiscordApplicationManagerImpl(native_getApplicationManager())
    override val userManager = DiscordUserManagerImpl(native_getUserManager())
    override val imageManager = DiscordImageManagerImpl(native_getImageManager())
    override val activityManager = DiscordActivityManagerImpl(native_getActivityManager())
    override val relationshipManager = DiscordRelationshipManagerImpl(native_getRelationshipManager())

    override val lobbyManager: DiscordLobbyManager get() = TODO("Not yet implemented")
    override val networkManager: DiscordNetworkManager get() = TODO("Not yet implemented")
    override val overlayManager: DiscordOverlayManager get() = TODO("Not yet implemented")
    override val storageManager: DiscordStorageManager get() = TODO("Not yet implemented")
    override val storeManager: DiscordStoreManager get() = TODO("Not yet implemented")
    override val voiceManager: DiscordVoiceManager get() = TODO("Not yet implemented")
    override val achievementManager: DiscordAchievementManager get() = TODO("Not yet implemented")

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

        fun create(clientId: DiscordClientId, flags: DiscordCreateFlags): DiscordObjectResult<DiscordCore> =
            native_create(clientId, flags.toNativeDiscordCreateFlags()).toDiscordObjectResult(::DiscordCoreImpl)

        @JvmStatic
        private external fun native_create(clientId: DiscordClientId, flags: Int): NativeDiscordObjectResult<Long>
    }
}
