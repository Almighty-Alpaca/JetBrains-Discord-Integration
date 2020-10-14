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

typealias int8_t = Byte
@OptIn(ExperimentalUnsignedTypes::class)
typealias uint8_t = UByte
typealias int32_t = Int
@OptIn(ExperimentalUnsignedTypes::class)
typealias uint32_t = UInt
typealias int64_t = Long
@OptIn(ExperimentalUnsignedTypes::class)
typealias uint64_t = ULong

typealias DiscordClientId = uint64_t
typealias DiscordVersion = int32_t
typealias DiscordSnowflake = int64_t
typealias DiscordTimestamp = int64_t
typealias DiscordUserId = DiscordSnowflake
typealias DiscordLocale = @StringLength(max = 128) String
typealias DiscordBranch = @StringLength(max = 4096) String
typealias DiscordLobbyId = DiscordSnowflake
typealias DiscordLobbySecret = @StringLength(max = 128) String
typealias DiscordMetadataKey = @StringLength(max = 256) String
typealias DiscordMetadataValue = @StringLength(max = 4096) String
typealias DiscordNetworkPeerId = uint64_t
typealias DiscordNetworkChannelId = uint8_t
typealias DiscordPath = @StringLength(max = 4096) String
typealias DiscordDateTime = @StringLength(max = 64) String

class DiscordUser(
    val id: DiscordUserId,
    val username: @StringLength(max = 256) String,
    val discriminator: @StringLength(max = 8) String,
    val avatar: @StringLength(max = 128) String,
    val bot: Boolean
)

class DiscordOAuth2Token(
    val accessToken: @StringLength(max = 128) String,
    val scopes: @StringLength(max = 1024) String,
    val expires: DiscordTimestamp
)

class DiscordImageHandle(
    val type: DiscordImageType,
    val id: int64_t,
    val size: uint32_t
)

class DiscordImageDimensions(
    val width: uint32_t,
    val height: uint32_t
)

class DiscordActivityTimestamps(
    val start: DiscordTimestamp,
    val end: DiscordTimestamp
)

class DiscordActivityAssets(
    val large_image: @StringLength(max = 128) String,
    val large_text: @StringLength(max = 128) String,
    val small_image: @StringLength(max = 128) String,
    val small_text: @StringLength(max = 128) String
)

class DiscordPartySize(
    val currentSize: int32_t,
    val maxSize: int32_t
)

class DiscordActivityParty(
    val id: @StringLength(max = 128) String,
    val size: DiscordPartySize
)

class DiscordActivitySecrets(
    val match: @StringLength(max = 128) String,
    val join: @StringLength(max = 128) String,
    val spectate: @StringLength(max = 128) String
)

class DiscordActivity(
    val type: DiscordActivityType,
    val applicationId: int64_t,
    val name: @StringLength(max = 128) String,
    val state: @StringLength(max = 128) String,
    val details: @StringLength(max = 128) String,
    val timestamps: DiscordActivityTimestamps,
    val assets: DiscordActivityAssets,
    val party: DiscordActivityParty,
    val secrets: DiscordActivitySecrets,
    val instance: Boolean
)

class DiscordPresence(
    val status: DiscordStatus,
    val activity: DiscordActivity
)

class DiscordRelationship(
    val type: DiscordRelationshipType,
    val user: DiscordUser,
    val presence: DiscordPresence
)

class DiscordLobby(
    val id: DiscordLobbyId,
    val type: DiscordLobbyType,
    val ownerId: DiscordUserId,
    val secret: DiscordLobbySecret,
    val capacity: uint32_t,
    val locked: Boolean
)

class DiscordFileStat(
    val filename: @StringLength(max = 260) String,
    val size: uint64_t,
    val lastModified: uint64_t
)

class DiscordEntitlement(
    val id: DiscordSnowflake,
    val type: DiscordEntitlementType,
    val skuId: DiscordSnowflake
)

class DiscordSkuPrice(
    val amount: uint32_t,
    val currency: @StringLength(max = 16) String
)

class DiscordSku(
    val id: DiscordSnowflake,
    val type: DiscordSkuType,
    val name: @StringLength(max = 256) String,
    val price: DiscordSkuPrice
)

class DiscordInputMode(
    val type: DiscordInputModeType,
    val shortcut: @StringLength(max = 256) String
)

class DiscordUserAchievement(
    val userId: DiscordSnowflake,
    val achievementId: DiscordSnowflake,
    val percentComplete: uint8_t,
    val unlockedAt: DiscordDateTime
)

typealias DiscordApplicationEvents = @VoidPointer Long
typealias DiscordImageEvents = @VoidPointer Long
typealias DiscordStorageEvents = @VoidPointer Long
typealias DiscordCoreEvents = @VoidPointer Long
