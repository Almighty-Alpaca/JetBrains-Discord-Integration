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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.mapFirst

class DiscordLobbyTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyTransaction {
    override fun setType(type: DiscordLobbyType) = DiscordResult.fromInt(native_setType(type))
    override fun setOwner(ownerId: DiscordUserId) = DiscordResult.fromInt(native_setOwner(ownerId))
    override fun setCapacity(capacity: uint32_t) = DiscordResult.fromInt(native_setCapacity(capacity))
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = DiscordResult.fromInt(native_setMetadata(metadataKey, metadataValue))
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = DiscordResult.fromInt(native_deleteMetadata(metadataKey))
    override fun setLocked(locked: Boolean) = DiscordResult.fromInt(native_setLocked(locked))

    private external fun native_setType(type: DiscordLobbyType): Int
    private external fun native_setOwner(ownerId: DiscordUserId): Int
    private external fun native_setCapacity(capacity: uint32_t): Int
    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
    private external fun native_setLocked(locked: Boolean): Int
}

class DiscordLobbyMemberTransactionImpl(private val internalThisPointer: Long) : DiscordLobbyMemberTransaction {
    override fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue) = DiscordResult.fromInt(native_setMetadata(metadataKey, metadataValue))
    override fun deleteMetadata(metadataKey: DiscordMetadataKey) = DiscordResult.fromInt(native_deleteMetadata(metadataKey))

    private external fun native_setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): Int
    private external fun native_deleteMetadata(metadataKey: DiscordMetadataKey): Int
}

class DiscordLobbySearchQueryImpl(private val internalThisPointer: Long) : DiscordLobbySearchQuery {
    override fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) = DiscordResult.fromInt(native_filter(key,
        comparison, cast, value))

    override fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue) = DiscordResult.fromInt(native_sort(key, cast, value))
    override fun limit(limit: uint32_t) = DiscordResult.fromInt(native_limit(limit))
    override fun distance(distance: DiscordLobbySearchDistance) = DiscordResult.fromInt(native_distance(distance))

    private external fun native_filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): Int
    private external fun native_sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): Int
    private external fun native_limit(limit: uint32_t): Int
    private external fun native_distance(distance: DiscordLobbySearchDistance): Int
}

class DiscordApplicationManagerImpl(private val internalThisPointer: Long) : DiscordApplicationManager {
    override fun <T> validateOrExit(callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) = native_validateOrExit(callbackData) { pCallbackData, result ->
        callback(pCallbackData,
            DiscordResult.fromInt(result))
    }

    override fun getCurrentLocale() = native_getCurrentLocale()
    override fun getCurrentBranch() = native_getDiscordBranch()
    override fun getOAuth2Token() = native_getOAuth2Token()
    override fun <T> getTicket(callbackData: T, callback: (callbackData: T, result: DiscordResult, ticket: String) -> Unit) =
        native_getTicked(callbackData) { pCallbackData, result, ticket -> callback(pCallbackData, DiscordResult.fromInt(result), ticket) }

    private external fun <T> native_validateOrExit(callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
    private external fun native_getCurrentLocale(): DiscordLocale
    private external fun native_getDiscordBranch(): DiscordBranch
    private external fun native_getOAuth2Token(): DiscordOAuth2Token
    private external fun <T> native_getTicked(callbackData: T, callback: (callbackData: T, result: Int, ticket: String) -> Unit)
}

class DiscordUserManagerImpl(private val internalThisPointer: Long) : DiscordUserManager {
    override fun getCurrentUser() = native_getCurrentUser().mapFirst(DiscordResult.Companion::fromInt)
    override fun <T> getUser(userId: DiscordUserId, callbackData: T, callback: (callbackData: T, result: DiscordResult, user: DiscordUser?) -> Unit) =
        native_getUser(userId, callbackData) { pCallbackData, result, user -> callback(pCallbackData, DiscordResult.fromInt(result), user) }

    override fun getCurrentUserPremiumType() = native_getCurrentUserPremiumType().mapFirst(DiscordResult.Companion::fromInt)
    override fun currentUserHasFlag(flag: DiscordUserFlag) = native_currentUserHasFlag(flag).mapFirst(DiscordResult.Companion::fromInt)

    private external fun native_getCurrentUser(): Pair<Int, DiscordUser?>
    private external fun <T> native_getUser(userId: DiscordUserId, callbackData: T, callback: (callbackData: T, result: Int, user: DiscordUser?) -> Unit)
    private external fun native_getCurrentUserPremiumType(): Pair<Int, DiscordPremiumType?>
    private external fun native_currentUserHasFlag(flag: DiscordUserFlag): Pair<Int, Boolean>
}

class DiscordImageManagerImpl(private val internalThisPointer: Long) : DiscordImageManager {
    override fun <T> fetch(handle: DiscordImageHandle, refresh: Boolean, callbackData: T, callback: (callbackData: T, result: DiscordResult, result_handle: DiscordImageHandle) -> Unit) =
        native_fetch(handle, refresh, callbackData) { pCallbackData, result, resultHandle -> callback(pCallbackData, DiscordResult.fromInt(result), resultHandle) }

    override fun getDimensions(handle: DiscordImageHandle) = native_getDimensions(handle)
    override fun getData(handle: DiscordImageHandle, dataLength: uint32_t) = native_getData(handle, dataLength)

    private external fun <T> native_fetch(handle: DiscordImageHandle, refresh: Boolean, callbackData: T, callback: (callbackData: T, result: Int, result_handle: DiscordImageHandle) -> Unit)
    private external fun native_getDimensions(handle: DiscordImageHandle): DiscordImageDimensions
    private external fun native_getData(handle: DiscordImageHandle, dataLength: uint32_t): Array<uint8_t>
}

class DiscordActivityManagerImpl(private val internalThisPointer: Long) : DiscordActivityManager {
    override fun registerCommand(command: String) = DiscordResult.fromInt(native_registerCommand(command))

    override fun registerSteam(steamId: uint32_t) = DiscordResult.fromInt(native_registerSteam(steamId))

    override fun <T> updateActivity(activity: DiscordActivity, callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) =
        native_updateActivity(activity, callbackData) { pCallbackData, result -> callback(pCallbackData, DiscordResult.fromInt(result)) }

    override fun <T> clearActivity(callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) = native_clearActivity(callbackData) { pCallbackData: T, result: Int ->
        callback(pCallbackData, DiscordResult.fromInt(result))
    }

    override fun <T> sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) =
        native_sendRequestReply(userId, reply, callbackData) { pCallbackData, result -> callback(pCallbackData, DiscordResult.fromInt(result)) }

    override fun <T> sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) =
        native_sendInvite(userId, type, content, callbackData) { pCallbackData, result -> callback(pCallbackData, DiscordResult.fromInt(result)) }

    override fun <T> acceptInvite(userId: DiscordUserId, callbackData: T, callback: (callbackData: T, result: DiscordResult) -> Unit) =
        native_acceptInvite(userId, callbackData) { pCallbackData, result ->
            callback(pCallbackData, DiscordResult.fromInt(result))
        }

    private external fun native_registerCommand(command: String): Int
    private external fun native_registerSteam(steamId: uint32_t): Int
    private external fun <T> native_updateActivity(activity: DiscordActivity, callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
    private external fun <T> native_clearActivity(callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
    private external fun <T> native_sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
    private external fun <T> native_sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
    private external fun <T> native_acceptInvite(userId: DiscordUserId, callbackData: T, callback: (callbackData: T, result: Int) -> Unit)
}

class DiscordRelationshipManagerImpl(private val internalThisPointer: Long): DiscordRelationshipManager {
    override fun <T> filter(filterData: T, filter: (filterData: T, relationship: DiscordRelationship) -> Boolean) = native_filter(filterData, filter)
    override fun count() = native_count()
    override fun get(userId: DiscordUserId): Pair<DiscordResult, DiscordRelationship?> = native_get(userId).mapFirst(DiscordResult.Companion::fromInt)
    override fun getAt(index: uint32_t): Pair<DiscordResult, DiscordRelationship?> = native_getAt(index).mapFirst(DiscordResult.Companion::fromInt)

    private external fun <T> native_filter(filterData: T, filter: (filterData: T, relationship: DiscordRelationship) -> Boolean)
    private external fun native_count(): Pair<DiscordResult, int32_t>
    private external fun native_get(userId: DiscordUserId): Pair<Int, DiscordRelationship?>
    private external fun native_getAt(index: uint32_t): Pair<Int, DiscordRelationship?>
}
