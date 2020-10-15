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

/// TODO: Implement a toInt() for all enums, reflecting the values in the native code

enum class DiscordResult {
    Ok,
    ServiceUnavailable,
    InvalidVersion,
    LockFailed,
    InternalError,
    InvalidPayload,
    InvalidCommand,
    InvalidPermissions,
    NotFetched,
    NotFound,
    Conflict,
    InvalidSecret,
    InvalidJoinSecret,
    NoEligibleActivity,
    InvalidInvite,
    NotAuthenticated,
    InvalidAccessToken,
    ApplicationMismatch,
    InvalidDataUrl,
    InvalidBase64,
    NotFiltered,
    LobbyFull,
    InvalidLobbySecret,
    InvalidFilename,
    InvalidFileSize,
    InvalidEntitlement,
    NotInstalled,
    NotRunning,
    InsufficientBuffer,
    PurchaseCanceled,
    InvalidGuild,
    InvalidEvent,
    InvalidChannel,
    InvalidOrigin,
    RateLimited,
    OAuth2Error,
    SelectChannelTimeout,
    GetGuildTimeout,
    SelectVoiceForceRequired,
    CaptureShortcutAlreadyListening,
    UnauthorizedForAchievement,
    InvalidGiftCode,
    PurchaseError,
    TransactionAborted,
    ;

    fun toInt(): Int {
        return this.ordinal
    }
    
    companion object {
        fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordCreateFlags {
    Default,
    NoRequireDiscord,
}

enum class DiscordLogLevel {
    Error,
    Warn,
    Info,
    Debug
}


// well this is just masks so we need ints.
typealias DiscordUserFlag = Int

const val DiscordUserFlag_Partner = 2
const val DiscordUserFlag_HypeSquadEvents = 4
const val DiscordUserFlag_HypeSquadHouse1 = 64
const val DiscordUserFlag_HypeSquadHouse2 = 128
const val DiscordUserFlag_HypeSquadHouse3 = 256

enum class DiscordPremiumType {
    None,
    Tier1,
    Tier2,
}

enum class DiscordImageType {
    User
}

enum class DiscordActivityType {
    Playing,
    Streaming,
    Listening,
    Watching
}

enum class DiscordActivityActionType {
    Join,
    Spectate,
}

enum class DiscordActivityJoinRequestReply {
    No,
    Yes,
    Ignore,
}

enum class DiscordStatus {
    Offline,
    Online,
    Idle,
    DoNotDisturb,
}

enum class DiscordRelationshipType {
    None,
    Friend,
    Blocked,
    PendingIncoming,
    PendingOutgoing,
    Implicit,
}

enum class DiscordLobbyType {
    Private,
    Public,
}

enum class DiscordLobbySearchComparison {
    LessThanOrEqual,
    LessThan,
    Equal,
    GreaterThan,
    GreaterThanOrEqual,
    NotEqual,
}

enum class DiscordLobbySearchCast {
    String,
    Number,
}

enum class DiscordLobbySearchDistance {
    Local,
    Default,
    Extended,
    Global,
}

enum class DiscordEntitlementType {
    Purchase,
    PremiumSubscription,
    DeveloperGift,
    TestModePurchase,
    FreePurchase,
    UserGift,
    PremiumPurchase,
}

enum class DiscordSkuType {
    Application,
    DLC,
    Consumable,
    Bundle,
}

enum class DiscordInputModeType {
    VoiceActivity,
    PushToTalk,
}
