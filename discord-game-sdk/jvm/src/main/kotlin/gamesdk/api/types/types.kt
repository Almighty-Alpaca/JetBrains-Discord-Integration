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

package gamesdk.api.types

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.StringLength

public typealias int8_t = Byte
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint8_t = UByte
public typealias int32_t = Int
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint32_t = UInt
public typealias int64_t = Long
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint64_t = ULong

public typealias DiscordClientId = uint64_t
public typealias DiscordVersion = int32_t
public typealias DiscordSnowflake = int64_t
public typealias DiscordTimestamp = int64_t
public typealias DiscordUserId = DiscordSnowflake
public typealias DiscordLocale = @StringLength(max = 128) String
public typealias DiscordBranch = @StringLength(max = 4096) String
public typealias DiscordLobbyId = DiscordSnowflake
public typealias DiscordLobbySecret = @StringLength(max = 128) String
public typealias DiscordMetadataKey = @StringLength(max = 256) String
public typealias DiscordMetadataValue = @StringLength(max = 4096) String
public typealias DiscordNetworkPeerId = uint64_t
public typealias DiscordNetworkChannelId = uint8_t
public typealias DiscordPath = @StringLength(max = 4096) String
public typealias DiscordDateTime = @StringLength(max = 64) String

public typealias SteamId = uint32_t

public class DiscordUser(
    public val id: DiscordUserId,
    public val username: @StringLength(max = 256) String,
    public val discriminator: @StringLength(max = 8) String,
    public val avatar: @StringLength(max = 128) String,
    public val bot: Boolean
)

public class DiscordOAuth2Token(
    public val accessToken: @StringLength(max = 128) String,
    public val scopes: @StringLength(max = 1024) String,
    public val expires: DiscordTimestamp
)

public class DiscordImageHandle(
    public val type: DiscordImageType,
    public val id: int64_t,
    public val size: uint32_t
)

public class DiscordImageDimensions(
    public val width: uint32_t,
    public val height: uint32_t
)

public class DiscordActivityTimestamps(
    public val start: DiscordTimestamp = 0,
    public val end: DiscordTimestamp = 0
)

public class DiscordActivityAssets(
    public val large_image: @StringLength(max = 128) String = "",
    public val large_text: @StringLength(max = 128) String = "",
    public val small_image: @StringLength(max = 128) String = "",
    public val small_text: @StringLength(max = 128) String = ""
)

public class DiscordPartySize(
    public val currentSize: int32_t = 0,
    public val maxSize: int32_t = 0
)

public class DiscordActivityParty(
    public val id: @StringLength(max = 128) String = "",
    public val size: DiscordPartySize = DiscordPartySize(),
    public val privacy: DiscordActivityPartyPrivacy = DiscordActivityPartyPrivacy.Private
)

public class DiscordActivitySecrets(
    public val match: @StringLength(max = 128) String = "",
    public val join: @StringLength(max = 128) String = "",
    public val spectate: @StringLength(max = 128) String = ""
)

public class DiscordActivity(
    public val applicationId: int64_t,
    public val type: DiscordActivityType = DiscordActivityType.Playing,
    public val name: @StringLength(max = 128) String = "",
    public val state: @StringLength(max = 128) String = "",
    public val details: @StringLength(max = 128) String = "",
    public val timestamps: DiscordActivityTimestamps = DiscordActivityTimestamps(),
    public val assets: DiscordActivityAssets = DiscordActivityAssets(),
    public val party: DiscordActivityParty = DiscordActivityParty(),
    public val secrets: DiscordActivitySecrets = DiscordActivitySecrets(),
    public val instance: Boolean = false
)

public class DiscordPresence(
    public val status: DiscordStatus,
    public val activity: DiscordActivity
)

public class DiscordRelationship(
    public val type: DiscordRelationshipType,
    public val user: DiscordUser,
    public val presence: DiscordPresence
)

public class DiscordLobby(
    public val id: DiscordLobbyId,
    public val type: DiscordLobbyType,
    public val ownerId: DiscordUserId,
    public val secret: DiscordLobbySecret,
    public val capacity: uint32_t,
    public val locked: Boolean
)

public class DiscordImeUnderline(
    public val from: int32_t,
    public val to: int32_t,
    public val color: uint32_t,
    public val backgroundColor: uint32_t,
    public val thick: Boolean
)

public class DiscordRect(
    public val left: int32_t,
    public val top: int32_t,
    public val right: int32_t,
    public val bottom: int32_t
)

public class DiscordFileStat(
    public val filename: @StringLength(max = 260) String,
    public val size: uint64_t,
    public val lastModified: uint64_t
)

public class DiscordEntitlement(
    public val id: DiscordSnowflake,
    public val type: DiscordEntitlementType,
    public val skuId: DiscordSnowflake
)

public class DiscordSkuPrice(
    public val amount: uint32_t,
    public val currency: @StringLength(max = 16) String
)

public class DiscordSku(
    public val id: DiscordSnowflake,
    public val type: DiscordSkuType,
    public val name: @StringLength(max = 256) String,
    public val price: DiscordSkuPrice
)

public class DiscordInputMode(
    public val type: DiscordInputModeType,
    public val shortcut: @StringLength(max = 256) String
)

public class DiscordUserAchievement(
    public val userId: DiscordSnowflake,
    public val achievementId: DiscordSnowflake,
    public val percentComplete: uint8_t,
    public val unlockedAt: DiscordDateTime
)
