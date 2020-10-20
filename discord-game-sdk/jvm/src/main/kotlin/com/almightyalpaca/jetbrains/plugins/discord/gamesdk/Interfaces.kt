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

interface DiscordLobbyTransaction {
    fun setType(type: DiscordLobbyType): DiscordResult

    fun setOwner(ownerId: DiscordUserId): DiscordResult

    fun setCapacity(capacity: uint32_t): DiscordResult

    fun setMetadata(
        metadataKey: DiscordMetadataKey,
        metadataValue: DiscordMetadataValue
    ): DiscordResult

    fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordResult

    fun setLocked(locked: Boolean): DiscordResult
}

interface DiscordLobbyMemberTransaction {
    fun setMetadata(
        metadataKey: DiscordMetadataKey,
        metadataValue: DiscordMetadataValue
    ): DiscordResult

    fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordResult
}

interface DiscordLobbySearchQuery {
    fun filter(
        key: DiscordMetadataKey,
        comparison: DiscordLobbySearchComparison,
        cast: DiscordLobbySearchCast,
        value: DiscordMetadataValue
    ): DiscordResult

    fun sort(
        key: DiscordMetadataKey,
        cast: DiscordLobbySearchCast,
        value: DiscordMetadataValue
    ): DiscordResult

    fun limit(limit: uint32_t): DiscordResult

    fun distance(distance: DiscordLobbySearchDistance): DiscordResult
}

// TODO: try to replace pointers with object refs

interface DiscordApplicationManager {
    fun <T> validateOrExit(
        callbackData: T,
        callback: (callbackData: T, result: DiscordResult) -> Unit
    )

    fun getCurrentLocale(): DiscordLocale

    fun getCurrentBranch(): DiscordBranch

    fun getOAuth2Token(): DiscordOAuth2Token

    fun <T> getTicket(
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult,
            ticket: String
        ) -> Unit
    )
}

interface DiscordUserEvents {
    fun onCurrentUserUpdate(data: @VoidPointer Long)
}

interface DiscordUserManager {
    fun getCurrentUser(): Pair<DiscordResult, DiscordUser?>

    fun <T> getUser(
        userId: DiscordUserId,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult,
            user: DiscordUser?
        ) -> Unit
    )

    fun getCurrentUserPremiumType(): Pair<DiscordResult, DiscordPremiumType?>

    fun currentUserHasFlag(flag: DiscordUserFlag): Pair<DiscordResult, Boolean>
}

interface DiscordImageManager {
    fun <T> fetch(
        handle: DiscordImageHandle,
        refresh: Boolean,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult,
            result_handle: DiscordImageHandle
        ) -> Unit
    )

    fun getDimensions(handle: DiscordImageHandle): DiscordImageDimensions

    fun getData(
        handle: DiscordImageHandle,
        dataLength: uint32_t
    ): Array<uint8_t>
}

interface DiscordActivityEvents {
    fun onActivityJoin(
        eventData: @VoidPointer Long,
        secret: String
    )

    fun onActivitySpectate(
        eventData: @VoidPointer Long,
        secret: String
    )

    fun onActivityJoinRequest(
        eventData: @VoidPointer Long,
        user: @Pointer<DiscordUser> Long
    )

    fun onActivityInvite(
        event_data: @VoidPointer Long,
        type: DiscordActivityActionType,
        user: @Pointer<DiscordUser> Long,
        activity: @Pointer<DiscordActivity> Long
    )
}

interface DiscordActivityManager {
    fun registerCommand(command: String): DiscordResult

    fun registerSteam(steamId: uint32_t): DiscordResult

    fun <T> updateActivity(
        activity: DiscordActivity,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> clearActivity(
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> sendRequestReply(
        userId: DiscordUserId,
        reply: DiscordActivityJoinRequestReply,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> sendInvite(
        userId: DiscordUserId,
        type: DiscordActivityActionType,
        content: String,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> acceptInvite(
        userId: DiscordUserId,
        callbackData: T,
        callback: (
            callbackData: T,
            result: DiscordResult
        ) -> Unit
    )
}

interface DiscordRelationshipEvents {
    fun onRefresh(eventData: @VoidPointer Long)

    fun onRelationshipUpdate(
        eventData: @VoidPointer Long,
        relationship: @Pointer<DiscordRelationship> Long
    )
}

interface DiscordRelationshipManager {
    fun <T> filter(
        filterData: T,
        filter: (
            filterData: T,
            relationship: DiscordRelationship
        ) -> Boolean
    )

    fun count(): Pair<DiscordResult, int32_t>

    fun get(userId: DiscordUserId): Pair<DiscordResult, DiscordRelationship?>

    fun getAt(index: uint32_t): Pair<DiscordResult, DiscordRelationship?>
}

interface DiscordLobbyEvents {
    fun onLobbyUpdate(
        eventData: @VoidPointer Long,
        lobbyId: int64_t
    )

    fun onLobbyDelete(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        reason: uint32_t
    )

    fun onMemberConnect(
        event_data: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )

    fun onMemberUpdate(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )

    fun onMemberDisconnect(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t
    )

    fun onLobbyMessage(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    )

    fun onSpeaking(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        speaking: Boolean
    )

    fun onNetworkMessage(
        eventData: @VoidPointer Long,
        lobbyId: int64_t,
        userId: int64_t,
        channelId: uint8_t,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    )
}

interface DiscordLobbyManager {
    fun getLobbyCreateTransaction(transaction: @DoublePointer<DiscordLobbyTransaction> Long): DiscordResult
    fun getLobbyUpdateTransaction(
        lobbyId: DiscordLobbyId,
        transaction: @DoublePointer<DiscordLobbyTransaction> Long
    ): DiscordResult

    fun getMemberUpdateTransaction(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        transaction: @DoublePointer<DiscordLobbyTransaction> Long
    ): DiscordResult

    fun <T> createLobby(
        transaction: @Pointer<DiscordLobbyTransaction> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    )

    fun <T> updateLobby(
        lobbyId: DiscordLobbyId,
        transaction: @Pointer<DiscordLobbyTransaction> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> deleteLobby(
        lobbyId: DiscordLobbyId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> connectLobby(
        lobbyId: DiscordLobbyId,
        secret: DiscordLobbySecret,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    )

    fun <T> connectLobbyWithActivitySecret(
        activity_secret: DiscordLobbySecret,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            lobby: @Pointer<DiscordLobby> Long
        ) -> Unit
    )

    fun <T> disconnectLobby(
        lobbyId: DiscordLobbyId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun getLobby(
        lobbyId: DiscordLobbyId,
        lobby: @Pointer<DiscordLobby> Long
    ): DiscordResult

    fun getLobbyActivitySecret(
        lobbyId: DiscordLobbyId,
        secret: @Pointer<DiscordLobbySecret> Long
    ): DiscordResult

    fun getLobbyMetadataValue(
        lobbyId: DiscordLobbyId,
        key: DiscordMetadataKey,
        value: @Pointer<DiscordMetadataValue> Long
    ): DiscordResult

    fun getLobbyMetadataKey(
        lobbyId: DiscordLobbyId,
        index: int32_t,
        key: @Pointer<DiscordMetadataKey> Long
    ): DiscordResult

    fun lobbyMetadataCount(
        lobbyId: DiscordLobbyId,
        count: @Pointer<int32_t> Long
    ): DiscordResult

    fun memberCount(
        lobbyId: DiscordLobbyId,
        count: @Pointer<int32_t> Long
    ): DiscordResult

    fun getMemberUserId(
        lobbyId: DiscordLobbyId,
        index: int32_t,
        user_id: @Pointer<DiscordUserId> Long
    ): DiscordResult

    fun getMemberUser(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        user: @Pointer<DiscordUser> Long
    ): DiscordResult

    fun getMemberMetadataValue(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        key: DiscordMetadataKey,
        value: @Pointer<DiscordMetadataValue> Long
    ): DiscordResult

    fun getMemberMetadataKey(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        index: int32_t,
        key: @Pointer<DiscordMetadataKey> Long
    ): DiscordResult

    fun memberMetadataCount(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        count: @Pointer<int32_t> Long
    ): DiscordResult

    fun <T> updateMember(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        transaction: @Pointer<DiscordLobbyMemberTransaction> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> sendLobbyMessage(
        lobbyId: DiscordLobbyId,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun getSearchQuery(query: @DoublePointer<DiscordLobbySearchQuery> Long): DiscordResult

    fun <T> search(
        query: @Pointer<DiscordLobbySearchQuery> Long,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun lobbyCount(count: @Pointer<int32_t> Long)

    fun getLobbyId(
        index: int32_t,
        lobbyId: @Pointer<DiscordLobbyId> Long
    ): DiscordResult

    fun <T> connectVoice(
        lobbyId: DiscordLobbyId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> disconnectVoice(
        lobbyId: DiscordLobbyId,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun connectNetwork(lobbyId: DiscordLobbyId): DiscordResult

    fun disconnectNetwork(lobbyId: DiscordLobbyId): DiscordResult

    fun flushNetwork(): DiscordResult

    fun openNetworkChannel(
        lobbyId: DiscordLobbyId,
        channelId: uint8_t,
        reliable: Boolean
    ): DiscordResult

    fun sendNetworkMessage(
        lobbyId: DiscordLobbyId,
        userId: DiscordUserId,
        channelId: uint8_t,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    ): DiscordResult
}

interface DiscordNetworkEvents {
    fun onMessage(
        eventData: @VoidPointer Long,
        peerId: DiscordNetworkPeerId,
        channelId: DiscordNetworkChannelId,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    )

    fun onRouteUpdate(
        eventData: @VoidPointer Long,
        routeData: String
    )
}

interface DiscordNetworkManager {
    /**
     * Get the local peer ID for this process.
     */
    fun getPeerId(peerId: @Pointer<DiscordNetworkPeerId> Long)

    /**
     * Send pending network messages.
     */
    fun flush(): DiscordResult

    /**
     * Open a connection to a remote peer.
     */
    fun openPeer(
        peerId: DiscordNetworkPeerId,
        routeData: String
    ): DiscordResult

    /**
     * Update the route data for a connected peer.
     */
    fun updatePeer(
        peerId: DiscordNetworkPeerId,
        routeData: String
    ): DiscordResult

    /**
     * Close the connection to a remote peer.
     */
    fun closePeer(peerId: DiscordNetworkPeerId): DiscordResult

    /**
     * Open a message channel to a connected peer.
     */
    fun openChannel(
        peerId: DiscordNetworkPeerId,
        channelId: DiscordNetworkChannelId,
        reliable: Boolean
    ): DiscordResult

    /**
     * Close a message channel to a connected peer.
     */
    fun closeChannel(
        peerId: DiscordNetworkPeerId,
        channelId: DiscordNetworkChannelId
    ): DiscordResult

    /**
     * Send a message to a connected peer over an opened message channel.
     */
    fun sendMessage(
        peerId: DiscordNetworkPeerId,
        channelId: DiscordNetworkChannelId,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    ): DiscordResult
}

interface DiscordOverlayEvents {
    fun onToggle(
        eventData: @VoidPointer Long,
        locked: Boolean
    )
}

interface DiscordOverlayManager {
    fun isEnabled(enabled: @Pointer<Boolean> Long)

    fun isLocked(locked: @Pointer<Boolean> Long)

    fun <T> setLocked(
        locked: Boolean,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> openActivityInvite(
        type: DiscordActivityActionType,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> openGuildInvite(
        code: String,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> openVoiceSettings(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
}

interface DiscordStorageManager {
    fun read(
        name: String,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t,
        read: @Pointer<uint32_t> Long
    ): DiscordResult

    fun <T> readAsync(
        name: String,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            data: @Pointer<uint8_t> Long,
            dataLength: uint32_t
        ) -> Unit
    )

    fun <T> readAsyncPartial(
        name: String,
        offset: uint64_t,
        length: uint64_t,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult,
            data: @Pointer<uint8_t> Long,
            dataLength: uint32_t
        ) -> Unit
    )

    fun write(
        name: String,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t
    ): DiscordResult

    fun <T> writeAsync(
        name: String,
        data: @Pointer<uint8_t> Long,
        dataLength: uint32_t,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun delete(name: String): DiscordResult

    fun exists(
        name: String,
        exists: @Pointer<Boolean> Long
    ): DiscordResult

    fun count(count: @Pointer<int32_t> Long)

    fun stat(
        name: String,
        stat: @Pointer<DiscordFileStat> Long
    ): DiscordResult

    fun statAt(
        index: int32_t,
        stat: @Pointer<DiscordFileStat> Long
    ): DiscordResult

    fun getPath(path: @Pointer<DiscordPath> Long): DiscordResult
}

interface DiscordStoreEvents {
    fun onEntitlementCreate(
        eventData: @VoidPointer Long,
        entitlement: @Pointer<DiscordEntitlement> Long
    )

    fun onEntitlementDelete(
        eventData: @VoidPointer Long,
        entitlement: @Pointer<DiscordEntitlement> Long
    )
}

interface DiscordStoreManager {
    fun <T> fetchSkus(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun countSkus(count: @Pointer<int32_t> Long)

    fun getSku(
        skuId: DiscordSnowflake,
        sku: @Pointer<DiscordSku> Long
    ): DiscordResult

    fun getSkuAt(
        index: int32_t,
        sku: @Pointer<DiscordSku> Long
    ): DiscordResult

    fun <T> fetchEntitlements(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun countEntitlements(count: @Pointer<int32_t> Long)

    fun getEntitlement(
        entitlementId: DiscordSnowflake,
        entitlement: @Pointer<DiscordEntitlement> Long
    ): DiscordResult

    fun getEntitlementAt(
        index: int32_t,
        entitlement: @Pointer<DiscordEntitlement> Long
    ): DiscordResult

    fun hasSkuEntitlement(
        skuId: DiscordSnowflake,
        hasEntitlement: @Pointer<Boolean> Long
    ): DiscordResult

    fun <T> startPurchase(
        sku_id: DiscordSnowflake,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )
}

interface DiscordVoiceEvents {
    fun onSettingsUpdate(eventData: @VoidPointer Long)
}

interface DiscordVoiceManager {
    fun getInputMode(inputMode: @Pointer<DiscordInputMode> Long): DiscordResult
    fun <T> setInputMode(
        inputMode: DiscordInputMode,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun isSelfMute(mute: @Pointer<Boolean> Long): DiscordResult
    fun setSelfMute(mute: Boolean): DiscordResult
    fun isSelfDeaf(deaf: @Pointer<Boolean> Long): DiscordResult
    fun setSelfDeaf(deaf: Boolean): DiscordResult
    fun isLocalMute(
        userId: DiscordSnowflake,
        mute: @Pointer<Boolean> Long
    ): DiscordResult

    fun setLocalMute(
        userId: DiscordSnowflake,
        mute: Boolean
    ): DiscordResult

    fun getLocalVolume(
        userId: DiscordSnowflake,
        volume: @Pointer<uint8_t> Long
    ): DiscordResult

    fun setLocalVolume(
        userId: DiscordSnowflake,
        volume: uint8_t
    ): DiscordResult
}

interface DiscordAchievementEvents {
    fun onUserAchievementUpdate(event_data: @VoidPointer Long, userAchievement: @Pointer<DiscordUserAchievement> Long)
}

interface DiscordAchievementManager {
    fun <T> setUserAchievement(
        achievementId: DiscordSnowflake,
        percentComplete: uint8_t,
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun <T> fetchUserAchievements(
        callbackData: @Pointer<T> Long,
        callback: (
            callbackData: @Pointer<T> Long,
            result: DiscordResult
        ) -> Unit
    )

    fun countUserAchievements(count: @Pointer<int32_t> Long)

    fun getUserAchievement(
        userAchievementId: DiscordSnowflake,
        userAchievement: @Pointer<DiscordUserAchievement> Long
    ): DiscordResult

    fun getUserAchievementAt(
        index: int32_t,
        userAchievement: @Pointer<DiscordUserAchievement> Long
    ): DiscordResult
}

interface DiscordCore {
    fun destroy()
    fun runCallbacks(): DiscordResult
    fun <T> setLogHook(minLevel: DiscordLogLevel, hookData: T, hook: (hookData: T, level: DiscordLogLevel, message: String) -> Unit)
    fun getApplicationManager(): DiscordApplicationManager
    fun getUserManager(): DiscordUserManager
    fun getImageManager(): DiscordImageManager
    fun getActivityManager(): DiscordActivityManager
    fun getRelationshipManager(): DiscordRelationshipManager
    fun getLobbyManager(): DiscordLobbyManager
    fun getNetworkManager(): DiscordNetworkManager
    fun getOverlayManager(): DiscordOverlayManager
    fun getStorageManager(): DiscordStorageManager
    fun getStoreManager(): DiscordStoreManager
    fun getVoiceManager(): DiscordVoiceManager
    fun getAchievementManager(): DiscordAchievementManager
}

class DiscordCreateParams<EventDataType>(
    val clientId: DiscordClientId,
    val flags: uint64_t,
    val events: @Pointer<DiscordCoreEvents> Long,
    val eventData: @Pointer<EventDataType> Long,
    val application_events: @Pointer<DiscordApplicationEvents> Long,
    val application_version: DiscordVersion,
    val user_events: @Pointer<DiscordUserEvents> Long,
    val user_version: DiscordVersion,
    val image_events: @Pointer<DiscordImageEvents> Long,
    val image_version: DiscordVersion,
    val activity_events: @Pointer<DiscordActivityEvents> Long,
    val activity_version: DiscordVersion,
    val relationship_events: @Pointer<DiscordRelationshipEvents> Long,
    val relationship_version: DiscordVersion,
    val lobby_events: @Pointer<DiscordLobbyEvents> Long,
    val lobby_version: DiscordVersion,
    val network_events: @Pointer<DiscordNetworkEvents> Long,
    val network_version: DiscordVersion,
    val overlay_events: @Pointer<DiscordOverlayEvents> Long,
    val overlay_version: DiscordVersion,
    val storage_events: @Pointer<DiscordStorageEvents> Long,
    val storage_version: DiscordVersion,
    val store_events: @Pointer<DiscordStoreEvents> Long,
    val store_version: DiscordVersion,
    val voice_events: @Pointer<DiscordVoiceEvents> Long,
    val voice_version: DiscordVersion,
    val achievement_events: @Pointer<DiscordAchievementEvents> Long,
    val achievement_version: DiscordVersion
)

external fun <Type> discordCreate(version: DiscordVersion, params: @Pointer<DiscordCreateParams<Type>> Long, result: @DoublePointer<DiscordCore> Long): DiscordResult

