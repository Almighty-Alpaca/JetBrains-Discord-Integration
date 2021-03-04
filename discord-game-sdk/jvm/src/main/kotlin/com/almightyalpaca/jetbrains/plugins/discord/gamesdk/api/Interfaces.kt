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
import gamesdk.api.DiscordObjectResult
import gamesdk.api.DiscordObjectResultCallback
import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.DiscordResultCallback
import gamesdk.api.types.*
import gamesdk.impl.types.*
import java.nio.ByteBuffer

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

    /**
     * Alias for [setMetadata]
     */
    public operator fun set(metadataKey: DiscordMetadataKey, metadataValue: DiscordMetadataValue): DiscordCode = setMetadata(metadataKey, metadataValue)

    /**
     * Alias for [deleteMetadata]
     */
    public fun delete(metadataKey: DiscordMetadataKey): DiscordCode = deleteMetadata(metadataKey)
}

public interface DiscordLobbySearchQuery {
    public fun filter(key: DiscordMetadataKey, comparison: DiscordLobbySearchComparison, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): DiscordCode
    public fun sort(key: DiscordMetadataKey, cast: DiscordLobbySearchCast, value: DiscordMetadataValue): DiscordCode
    public fun limit(limit: uint32_t): DiscordCode
    public fun distance(distance: DiscordLobbySearchDistance): DiscordCode
}

public interface DiscordApplicationManager {
    public fun validateOrExit(callback: DiscordResultCallback)
    public fun getCurrentLocale(): DiscordLocale
    public fun getCurrentBranch(): DiscordBranch
    public fun getOAuth2Token(callback: DiscordObjectResultCallback<DiscordOAuth2Token>)
    public fun getTicket(callback: DiscordObjectResultCallback<String>)
}

public interface DiscordUserEvents {
    public fun onCurrentUserUpdate()
}

public interface DiscordUserManager {
    public fun getCurrentUser(): DiscordObjectResult<DiscordUser>
    public fun getUser(userId: DiscordUserId, callback: DiscordObjectResultCallback<DiscordUser>)
    public fun getCurrentUserPremiumType(): DiscordObjectResult<DiscordPremiumType>
    public fun currentUserHasFlag(flag: DiscordUserFlag): DiscordObjectResult<Boolean>
}

public interface DiscordImageManager {
    public fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: DiscordObjectResultCallback<DiscordImageHandle>)
    public fun getDimensions(handle: DiscordImageHandle): DiscordImageDimensions
    public fun getData(handle: DiscordImageHandle, dataLength: uint32_t): ByteBuffer
}

public interface DiscordActivityEvents {
    public fun onActivityJoin(secret: String)
    public fun onActivitySpectate(secret: String)
    public fun onActivityJoinRequest(user: DiscordUser)
    public fun onActivityInvite(type: DiscordActivityActionType, user: DiscordUser, activity: DiscordActivity)
}

public interface DiscordActivityManager {
    public fun registerCommand(command: String): DiscordCode
    public fun registerSteam(steamId: uint32_t): DiscordCode
    public fun updateActivity(activity: DiscordActivity, callback: DiscordResultCallback)
    public fun clearActivity(callback: DiscordResultCallback)
    public fun sendRequestReply(userId: DiscordUserId, reply: DiscordActivityJoinRequestReply, callback: DiscordResultCallback)
    public fun sendInvite(userId: DiscordUserId, type: DiscordActivityActionType, content: String, callback: DiscordResultCallback)
    public fun acceptInvite(userId: DiscordUserId, callback: DiscordResultCallback)
}

public interface DiscordRelationshipEvents {
    public fun onRefresh()
    public fun onRelationshipUpdate(relationship: DiscordRelationship)
}

public interface DiscordRelationshipManager {
    public fun filter(filter: DiscordRelationshipFilter)
    public fun count(): DiscordObjectResult<int32_t>
    public fun get(userId: DiscordUserId): DiscordObjectResult<DiscordRelationship>
    public fun getAt(index: uint32_t): DiscordObjectResult<DiscordRelationship>
}

public interface DiscordLobbyEvents {
    public fun onLobbyUpdate(lobbyId: int64_t)
    public fun onLobbyDelete(lobbyId: int64_t, reason: uint32_t)
    public fun onMemberConnect(lobbyId: int64_t, userId: int64_t)
    public fun onMemberUpdate(lobbyId: int64_t, userId: int64_t)
    public fun onMemberDisconnect(lobbyId: int64_t, userId: int64_t)
    public fun onLobbyMessage(lobbyId: int64_t, userId: int64_t, data: ByteBuffer)
    public fun onSpeaking(lobbyId: int64_t, userId: int64_t, speaking: Boolean)
    public fun onNetworkMessage(lobbyId: int64_t, userId: int64_t, channelId: uint8_t, data: ByteBuffer)
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
    public fun onMessage(peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId, data: ByteBuffer)
    public fun onRouteUpdate(routeData: String)
}

public interface DiscordNetworkManager {
    /** Get the local peer ID for this process.  */
    public fun getPeerId(): DiscordNetworkPeerId

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
    public fun sendMessage(peerId: DiscordNetworkPeerId, channelId: DiscordNetworkChannelId, data: ByteBuffer): DiscordCode
}

public interface DiscordOverlayEvents {
    public fun onToggle(locked: Boolean)
}

public interface DiscordOverlayManager {
    public fun isEnabled(): Boolean
    public fun isLocked(): Boolean
    public fun setLocked(locked: Boolean, callback: DiscordResultCallback)
    public fun openActivityInvite(type: DiscordActivityActionType, callback: DiscordResultCallback)
    public fun openGuildInvite(code: String, callback: DiscordResultCallback)
    public fun openVoiceSettings(callback: DiscordResultCallback)
    /* TODO: Add definitions for https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration/commit/aebc8b9e1bcbd8bd92bf51c4dc84a758b7e08ebe#diff-55032fa98b9ee622a6971564672dc88c9f52d3b09234dea74da2b17879d909fdR553-R565 */
}

public interface DiscordStorageManager {
    public fun read(name: String, data: ByteBuffer): DiscordObjectResult<uint32_t> // count read
    public fun readAsync(name: String, callback: DiscordObjectResultCallback<ByteBuffer>)
    public fun readAsyncPartial(name: String, offset: uint64_t, length: uint64_t, callback: DiscordObjectResultCallback<ByteBuffer>)
    public fun write(name: String, data: ByteBuffer): DiscordCode
    public fun writeAsync(name: String, data: ByteBuffer, callback: DiscordResultCallback)
    public fun delete(name: String): DiscordCode
    public fun exists(name: String): DiscordObjectResult<DiscordCode>
    public fun count(): int32_t
    public fun stat(name: String): DiscordObjectResult<DiscordFileStat>
    public fun statAt(index: int32_t): DiscordObjectResult<DiscordFileStat>
    public fun getPath(): DiscordObjectResult<DiscordPath>
}

public interface DiscordStoreEvents {
    public fun onEntitlementCreate(entitlement: DiscordEntitlement)
    public fun onEntitlementDelete(entitlement: DiscordEntitlement)
}

public interface DiscordStoreManager {
    public fun fetchSkus(callback: DiscordResultCallback)
    public fun countSkus(): int32_t
    public fun getSku(skuId: DiscordSnowflake): DiscordObjectResult<DiscordSku>
    public fun getSkuAt(index: int32_t): DiscordObjectResult<DiscordSku>
    public fun fetchEntitlements(callback: DiscordResultCallback)
    public fun countEntitlements(): int32_t
    public fun getEntitlement(entitlementId: DiscordSnowflake): DiscordObjectResult<DiscordEntitlement>
    public fun getEntitlementAt(index: int32_t): DiscordObjectResult<DiscordEntitlement>
    public fun hasSkuEntitlement(skuId: DiscordSnowflake): DiscordObjectResult<Boolean>
    public fun startPurchase(sku_id: DiscordSnowflake, callback: DiscordResultCallback)
}

public interface DiscordVoiceEvents {
    public fun onSettingsUpdate()
}

public interface DiscordVoiceManager {
    public fun getInputMode(): DiscordObjectResult<DiscordInputMode>
    public fun setInputMode(inputMode: DiscordInputMode, callback: DiscordResultCallback)
    public fun isSelfMute(): DiscordObjectResult<Boolean>
    public fun setSelfMute(mute: Boolean): DiscordCode
    public fun isSelfDeaf(): DiscordObjectResult<Boolean>
    public fun setSelfDeaf(deaf: Boolean): DiscordCode
    public fun isLocalMute(userId: DiscordSnowflake): DiscordObjectResult<Boolean>
    public fun setLocalMute(userId: DiscordSnowflake, mute: Boolean): DiscordCode
    public fun getLocalVolume(userId: DiscordSnowflake): DiscordObjectResult<uint8_t>
    public fun setLocalVolume(userId: DiscordSnowflake, volume: uint8_t): DiscordCode
}

public interface DiscordAchievementEvents {
    public fun onUserAchievementUpdate(userAchievement: DiscordUserAchievement)
}

public interface DiscordAchievementManager {
    public fun setUserAchievement(achievementId: DiscordSnowflake, percentComplete: uint8_t, callback: DiscordResultCallback)
    public fun fetchUserAchievements(callback: DiscordResultCallback)
    public fun countUserAchievements(): int32_t
    public fun getUserAchievement(userAchievementId: DiscordSnowflake): DiscordObjectResult<DiscordUserAchievement>
    public fun getUserAchievementAt(index: int32_t): DiscordObjectResult<DiscordUserAchievement>
}

public interface DiscordCore {
    public val alive: Boolean
    public fun close()
    public fun runCallbacks(): DiscordCode
    public fun setLogHook(minLevel: DiscordLogLevel, hook: (level: DiscordLogLevel, message: String) -> Unit)
    public val applicationManager: DiscordApplicationManager
    public val userManager: DiscordUserManager
    public val imageManager: DiscordImageManager
    public val activityManager: DiscordActivityManager
    public val relationshipManager: DiscordRelationshipManager
    public val lobbyManager: DiscordLobbyManager
    public val networkManager: DiscordNetworkManager
    public val overlayManager: DiscordOverlayManager
    public val storageManager: DiscordStorageManager
    public val storeManager: DiscordStoreManager
    public val voiceManager: DiscordVoiceManager
    public val achievementManager: DiscordAchievementManager
}
