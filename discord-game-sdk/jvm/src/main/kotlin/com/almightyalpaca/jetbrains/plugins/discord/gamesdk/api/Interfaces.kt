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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.DoublePointer
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.Pointer
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.VoidPointer
import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.types.*
import gamesdk.impl.types.*

public interface DiscordLobbyTransaction {
    public fun setType(type: DiscordLobbyType): DiscordCode
    public fun setOwner(ownerId: DiscordUserId): DiscordCode
    public fun setCapacity(capacity: uint32_t): DiscordCode
    public fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): DiscordCode
    public fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordCode
    public fun setLocked(locked: Boolean): DiscordCode
}

public interface DiscordLobbyMemberTransaction {
    public fun setMetadata(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): DiscordCode
    public fun deleteMetadata(metadataKey: DiscordMetadataKey): DiscordCode
}

public interface DiscordLobbySearchQuery {
    public fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): DiscordCode
    public fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): DiscordCode
    public fun limit(limit: uint32_t): DiscordCode
    public fun distance(distance: DiscordLobbySearchDistance): DiscordCode
}

public interface DiscordApplicationManager {
    public fun validateOrExit(callback: (result: DiscordCode) -> Unit)
    public fun getCurrentLocale(): DiscordLocale
    public fun getCurrentBranch(): DiscordBranch
    public fun getOAuth2Token(callback: (result: DiscordCode, token: DiscordOAuth2Token?) -> Unit)
    public fun getTicket(callback: (result: DiscordCode, ticket: String) -> Unit)
}

public interface DiscordUserEvents {
    public fun onCurrentUserUpdate(data: @VoidPointer Long)
}

public interface DiscordUserManager {
    public fun getCurrentUser(): Pair<DiscordCode, DiscordUser?>
    public fun getUser(userId: DiscordUserId, callback: (result: DiscordCode, user: DiscordUser?) -> Unit)
    public fun getCurrentUserPremiumType(): Pair<DiscordCode, DiscordPremiumType?>
    public fun currentUserHasFlag(flag: DiscordUserFlag): Pair<DiscordCode, Boolean>
}

public interface DiscordImageManager {
    public fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: (result: DiscordCode, result_handle: DiscordImageHandle) -> Unit)
    public fun getDimensions(handle: DiscordImageHandle): DiscordImageDimensions
    public fun getData(handle: DiscordImageHandle, dataLength: uint32_t): Array<uint8_t>
}

public interface DiscordActivityEvents {
    public fun onActivityJoin(eventData: @VoidPointer Long, secret: String)
    public fun onActivitySpectate(eventData: @VoidPointer Long, secret: String)
    public fun onActivityJoinRequest(eventData: @VoidPointer Long, user: @Pointer<DiscordUser> Long)
    public fun onActivityInvite(event_data: @VoidPointer Long, type: DiscordActivityActionType, user: @Pointer<DiscordUser> Long, activity: @Pointer<DiscordActivity> Long)
}

public interface DiscordActivityManager {
    public fun registerCommand(command: String): DiscordCode
    public fun registerSteam(steamId: uint32_t): DiscordCode
    public fun updateActivity(activity: DiscordActivity, callback: (result: DiscordCode) -> Unit)
    public fun clearActivity(callback: (result: DiscordCode) -> Unit)
    public fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: (result: DiscordCode) -> Unit)
    public fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: (result: DiscordCode) -> Unit)
    public fun acceptInvite(userId: DiscordUserId, callback: (result: DiscordCode) -> Unit)
}

public interface DiscordRelationshipEvents {
    public fun onRefresh(eventData: @VoidPointer Long)
    public fun onRelationshipUpdate(eventData: @VoidPointer Long, relationship: @Pointer<DiscordRelationship> Long)
}

public interface DiscordRelationshipManager {
    public fun filter(filter: DiscordRelationshipFilter)
    public fun count(): Pair<DiscordCode, int32_t>
    public fun get(userId: DiscordUserId): Pair<DiscordCode, DiscordRelationship?>
    public fun getAt(index: uint32_t): Pair<DiscordCode, DiscordRelationship?>
}

public interface DiscordLobbyEvents {
    public fun onLobbyUpdate(eventData: @VoidPointer Long, lobbyId: int64_t)
    public fun onLobbyDelete(eventData: @VoidPointer Long, lobbyId: int64_t, reason: uint32_t)
    public fun onMemberConnect(event_data: @VoidPointer Long, lobbyId: int64_t, userId: int64_t)
    public fun onMemberUpdate(eventData: @VoidPointer Long, lobbyId: int64_t, userId: int64_t)
    public fun onMemberDisconnect(eventData: @VoidPointer Long, lobbyId: int64_t, userId: int64_t)
    public fun onLobbyMessage(eventData: @VoidPointer Long, lobbyId: int64_t, userId: int64_t, data: @Pointer<uint8_t> Long, dataLength: uint32_t)
    public fun onSpeaking(eventData: @VoidPointer Long, lobbyId: int64_t, userId: int64_t, speaking: Boolean)
    public fun onNetworkMessage(eventData: @VoidPointer Long, lobbyId: int64_t, userId: int64_t, channelId: uint8_t, data: @Pointer<uint8_t> Long, dataLength: uint32_t)
}

public interface DiscordLobbyManager {
    public fun getLobbyCreateTransaction(transaction: @DoublePointer<DiscordLobbyTransaction> Long): DiscordCode
    public fun getLobbyUpdateTransaction(lobbyId: DiscordLobbyId, transaction: @DoublePointer<DiscordLobbyTransaction> Long): DiscordCode
    public fun getMemberUpdateTransaction(lobbyId: DiscordLobbyId, userId: DiscordUserId, transaction: @DoublePointer<DiscordLobbyTransaction> Long): DiscordCode
    public fun createLobby(transaction: @Pointer<DiscordLobbyTransaction> Long, callback: (result: DiscordCode, lobby: @Pointer<DiscordLobby> Long) -> Unit)
    public fun updateLobby(lobbyId: DiscordLobbyId, transaction: @Pointer<DiscordLobbyTransaction> Long, callback: (result: DiscordCode) -> Unit)
    public fun deleteLobby(lobbyId: DiscordLobbyId, callback: (result: DiscordCode) -> Unit)
    public fun connectLobby(lobbyId: DiscordLobbyId, secret: DiscordLobbySecret, callback: (result: DiscordCode, lobby: @Pointer<DiscordLobby> Long) -> Unit)
    public fun connectLobbyWithActivitySecret(activity_secret: DiscordLobbySecret, callback: (result: DiscordCode, lobby: @Pointer<DiscordLobby> Long) -> Unit)
    public fun disconnectLobby(lobbyId: DiscordLobbyId, callback: (result: DiscordCode) -> Unit)
    public fun getLobby(lobbyId: DiscordLobbyId, lobby: @Pointer<DiscordLobby> Long): DiscordCode
    public fun getLobbyActivitySecret(lobbyId: DiscordLobbyId, secret: @Pointer<DiscordLobbySecret> Long): DiscordCode
    public fun getLobbyMetadataValue(lobbyId: DiscordLobbyId, key: DiscordMetadataKey, value: @Pointer<DiscordMetadataValue> Long): DiscordCode
    public fun getLobbyMetadataKey(lobbyId: DiscordLobbyId, index: int32_t, key: @Pointer<DiscordMetadataKey> Long): DiscordCode
    public fun lobbyMetadataCount(lobbyId: DiscordLobbyId, count: @Pointer<int32_t> Long): DiscordCode
    public fun memberCount(lobbyId: DiscordLobbyId, count: @Pointer<int32_t> Long): DiscordCode
    public fun getMemberUserId(lobbyId: DiscordLobbyId, index: int32_t, user_id: @Pointer<DiscordUserId> Long): DiscordCode
    public fun getMemberUser(lobbyId: DiscordLobbyId, userId: DiscordUserId, user: @Pointer<DiscordUser> Long): DiscordCode
    public fun getMemberMetadataValue(lobbyId: DiscordLobbyId, userId: DiscordUserId, key: DiscordMetadataKey, value: @Pointer<DiscordMetadataValue> Long): DiscordCode
    public fun getMemberMetadataKey(lobbyId: DiscordLobbyId, userId: DiscordUserId, index: int32_t, key: @Pointer<DiscordMetadataKey> Long): DiscordCode
    public fun memberMetadataCount(lobbyId: DiscordLobbyId, userId: DiscordUserId, count: @Pointer<int32_t> Long): DiscordCode
    public fun updateMember(lobbyId: DiscordLobbyId, userId: DiscordUserId, transaction: @Pointer<DiscordLobbyMemberTransaction> Long, callback: (result: DiscordCode) -> Unit)
    public fun sendLobbyMessage(lobbyId: DiscordLobbyId, data: @Pointer<uint8_t> Long, dataLength: uint32_t, callback: (result: DiscordCode) -> Unit)
    public fun getSearchQuery(query: @DoublePointer<DiscordLobbySearchQuery> Long): DiscordCode
    public fun search(query: @Pointer<DiscordLobbySearchQuery> Long, callback: (result: DiscordCode) -> Unit)
    public fun lobbyCount(count: @Pointer<int32_t> Long)
    public fun getLobbyId(index: int32_t, lobbyId: @Pointer<DiscordLobbyId> Long): DiscordCode
    public fun connectVoice(lobbyId: DiscordLobbyId, callback: (result: DiscordCode) -> Unit)
    public fun disconnectVoice(lobbyId: DiscordLobbyId, callback: (result: DiscordCode) -> Unit)
    public fun connectNetwork(lobbyId: DiscordLobbyId): DiscordCode
    public fun disconnectNetwork(lobbyId: DiscordLobbyId): DiscordCode
    public fun flushNetwork(): DiscordCode
    public fun openNetworkChannel(lobbyId: DiscordLobbyId, channelId: uint8_t, reliable: Boolean): DiscordCode
    public fun sendNetworkMessage(lobbyId: DiscordLobbyId, userId: DiscordUserId, channelId: uint8_t, data: @Pointer<uint8_t> Long, dataLength: uint32_t): DiscordCode
}

public interface DiscordNetworkEvents {
    public fun onMessage(eventData: @VoidPointer Long, peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId, data: @Pointer<uint8_t> Long, dataLength: uint32_t)
    public fun onRouteUpdate(eventData: @VoidPointer Long, routeData: String)
}

public interface DiscordNetworkManager {
    /** Get the local peer ID for this process.  */
    public fun getPeerId(peerId: @Pointer<DiscordNetworkPeerId> Long)

    /** Send pending network messages.  */
    public fun flush(): DiscordCode

    /** Open a connection to a remote peer.  */
    public fun openPeer(peerId: DiscordNetworkPeerId, routeData: String): DiscordCode

    /** Update the route data for a connected peer.  */
    public fun updatePeer(peerId: DiscordNetworkPeerId, routeData: String): DiscordCode

    /** Close the connection to a remote peer.  */
    public fun closePeer(peerId: DiscordNetworkPeerId): DiscordCode

    /** Open a message channel to a connected peer.  */
    public fun openChannel(peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId, reliable: Boolean): DiscordCode

    /** Close a message channel to a connected peer.  */
    public fun closeChannel(peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId): DiscordCode

    /** Send a message to a connected peer over an opened message channel.  */
    public fun sendMessage(peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId, data: @Pointer<uint8_t> Long, dataLength: uint32_t): DiscordCode
}

public interface DiscordOverlayEvents {
    public fun onToggle(eventData: @VoidPointer Long, locked: Boolean)
}

public interface DiscordOverlayManager {
    public fun isEnabled(enabled: @Pointer<Boolean> Long)
    public fun isLocked(locked: @Pointer<Boolean> Long)
    public fun setLocked(locked: Boolean, callback: (result: DiscordCode) -> Unit)
    public fun openActivityInvite(type: DiscordActivityActionType, callback: (result: DiscordCode) -> Unit)
    public fun openGuildInvite(code: String, callback: (result: DiscordCode) -> Unit)
    public fun openVoiceSettings(callback: (result: DiscordCode) -> Unit)
    /* TODO: Add definitions for https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration/commit/aebc8b9e1bcbd8bd92bf51c4dc84a758b7e08ebe#diff-55032fa98b9ee622a6971564672dc88c9f52d3b09234dea74da2b17879d909fdR553-R565 */
}

public interface DiscordStorageManager {
    public fun read(name: String, data: @Pointer<uint8_t> Long, dataLength: uint32_t, read: @Pointer<uint32_t> Long): DiscordCode
    public fun readAsync(name: String, callback: (result: DiscordCode, data: @Pointer<uint8_t> Long, dataLength: uint32_t) -> Unit)
    public fun readAsyncPartial(name: String, offset: uint64_t, length: uint64_t, callback: (result: DiscordCode, data: @Pointer<uint8_t> Long, dataLength: uint32_t) -> Unit)
    public fun write(name: String, data: @Pointer<uint8_t> Long, dataLength: uint32_t): DiscordCode
    public fun writeAsync(name: String, data: @Pointer<uint8_t> Long, dataLength: uint32_t, callback: (result: DiscordCode) -> Unit)
    public fun delete(name: String): DiscordCode
    public fun exists(name: String, exists: @Pointer<Boolean> Long): DiscordCode
    public fun count(count: @Pointer<int32_t> Long)
    public fun stat(name: String, stat: @Pointer<DiscordFileStat> Long): DiscordCode
    public fun statAt(index: int32_t, stat: @Pointer<DiscordFileStat> Long): DiscordCode
    public fun getPath(path: @Pointer<DiscordPath> Long): DiscordCode
}

public interface DiscordStoreEvents {
    public fun onEntitlementCreate(eventData: @VoidPointer Long, entitlement: @Pointer<DiscordEntitlement> Long)
    public fun onEntitlementDelete(eventData: @VoidPointer Long, entitlement: @Pointer<DiscordEntitlement> Long)
}

public interface DiscordStoreManager {
    public fun fetchSkus(callback: (result: DiscordCode) -> Unit)
    public fun countSkus(count: @Pointer<int32_t> Long)
    public fun getSku(skuId: DiscordSnowflake, sku: @Pointer<DiscordSku> Long): DiscordCode
    public fun getSkuAt(index: int32_t, sku: @Pointer<DiscordSku> Long): DiscordCode
    public fun fetchEntitlements(callback: (result: DiscordCode) -> Unit)
    public fun countEntitlements(count: @Pointer<int32_t> Long)
    public fun getEntitlement(entitlementId: DiscordSnowflake, entitlement: @Pointer<DiscordEntitlement> Long): DiscordCode
    public fun getEntitlementAt(index: int32_t, entitlement: @Pointer<DiscordEntitlement> Long): DiscordCode
    public fun hasSkuEntitlement(skuId: DiscordSnowflake, hasEntitlement: @Pointer<Boolean> Long): DiscordCode
    public fun startPurchase(sku_id: DiscordSnowflake, callback: (result: DiscordCode) -> Unit)
}

public interface DiscordVoiceEvents {
    public fun onSettingsUpdate(eventData: @VoidPointer Long)
}

public interface DiscordVoiceManager {
    public fun getInputMode(inputMode: @Pointer<DiscordInputMode> Long): DiscordCode
    public fun setInputMode(inputMode: DiscordInputMode, callback: (result: DiscordCode) -> Unit)
    public fun isSelfMute(mute: @Pointer<Boolean> Long): DiscordCode
    public fun setSelfMute(mute: Boolean): DiscordCode
    public fun isSelfDeaf(deaf: @Pointer<Boolean> Long): DiscordCode
    public fun setSelfDeaf(deaf: Boolean): DiscordCode
    public fun isLocalMute(userId: DiscordSnowflake, mute: @Pointer<Boolean> Long): DiscordCode
    public fun setLocalMute(userId: DiscordSnowflake, mute: Boolean): DiscordCode
    public fun getLocalVolume(userId: DiscordSnowflake, volume: @Pointer<uint8_t> Long): DiscordCode
    public fun setLocalVolume(userId: DiscordSnowflake, volume: uint8_t): DiscordCode
}

public interface DiscordAchievementEvents {
    public fun onUserAchievementUpdate(event_data: @VoidPointer Long, userAchievement: @Pointer<DiscordUserAchievement> Long)
}

public interface DiscordAchievementManager {
    public fun setUserAchievement(achievementId: DiscordSnowflake, percentComplete: uint8_t, callback: (result: DiscordCode) -> Unit)
    public fun fetchUserAchievements(callback: (result: DiscordCode) -> Unit)
    public fun countUserAchievements(count: @Pointer<int32_t> Long)
    public fun getUserAchievement(userAchievementId: DiscordSnowflake, userAchievement: @Pointer<DiscordUserAchievement> Long): DiscordCode
    public fun getUserAchievementAt(index: int32_t, userAchievement: @Pointer<DiscordUserAchievement> Long): DiscordCode
}

public interface DiscordCore {
    public val alive: Boolean
    public fun close()
    public fun runCallbacks(): DiscordCode
    public fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit)
    public fun getApplicationManager(): DiscordApplicationManager
    public fun getUserManager(): DiscordUserManager
    public fun getImageManager(): DiscordImageManager
    public fun getActivityManager(): DiscordActivityManager
    public fun getRelationshipManager(): DiscordRelationshipManager
    public fun getLobbyManager(): DiscordLobbyManager
    public fun getNetworkManager(): DiscordNetworkManager
    public fun getOverlayManager(): DiscordOverlayManager
    public fun getStorageManager(): DiscordStorageManager
    public fun getStoreManager(): DiscordStoreManager
    public fun getVoiceManager(): DiscordVoiceManager
    public fun getAchievementManager(): DiscordAchievementManager
}
