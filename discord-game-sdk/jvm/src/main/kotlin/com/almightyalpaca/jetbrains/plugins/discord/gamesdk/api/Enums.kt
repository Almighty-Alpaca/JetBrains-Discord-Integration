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

import java.util.*

enum class DiscordResult {
    /** Everything is good */
    Ok,
    /** Discord isn't working */
    ServiceUnavailable,
    /** The SDK version may be outdated */
    InvalidVersion,
    /** An internal error on transactional operations */
    LockFailed,
    /** Something on our side went wrong */
    InternalError,
    /** The data you sent didn't match what we expect */
    InvalidPayload,
    /** That's not a thing you can do */
    InvalidCommand,
    /** You aren't authorized to do that */
    InvalidPermissions,
    /** Couldn't fetch what you wanted */
    NotFetched,
    /** What you're looking for doesn't exist */
    NotFound,
    /** User already has a network connection open on that channel */
    Conflict,
    /** Activity secrets must be unique and not match party id */
    InvalidSecret,
    /** Join request for that user does not exist */
    InvalidJoinSecret,
    /** You accidentally set an `ApplicationId` in your `UpdateActivity()` payload */
    NoEligibleActivity,
    /** Your game invite is no longer valid */
    InvalidInvite,
    /** The internal auth call failed for the user, and you can't do this */
    NotAuthenticated,
    /** The user's bearer token is invalid */
    InvalidAccessToken,
    /** Access token belongs to another application */
    ApplicationMismatch,
    /** Something internally went wrong fetching image data */
    InvalidDataUrl,
    /** Not valid Base64 data */
    InvalidBase64,
    /** You're trying to access the list before creating a stable list with Filter() */
    NotFiltered,
    /** The lobby is full */
    LobbyFull,
    /** The secret you're using to connect is wrong */
    InvalidLobbySecret,
    /** File name is too long */
    InvalidFilename,
    /** File is too large */
    InvalidFileSize,
    /** The user does not have the right entitlement for this game */
    InvalidEntitlement,
    /** Discord is not installed */
    NotInstalled,
    /** Discord is not running */
    NotRunning,
    /** Insufficient buffer space when trying to write */
    InsufficientBuffer,
    /** User cancelled the purchase flow */
    PurchaseCanceled,
    /** Discord guild does not exist */
    InvalidGuild,
    /** The event you're trying to subscribe to does not exist */
    InvalidEvent,
    /** Discord channel does not exist */
    InvalidChannel,
    /** The origin header on the socket does not match what you've registered (you should not see this) */
    InvalidOrigin,
    /** You are calling that method too quickly */
    RateLimited,
    /** The OAuth2 process failed at some point */
    OAuth2Error,
    /** The user took too long selecting a channel for an invite */
    SelectChannelTimeout,
    /** Took too long trying to fetch the guild */
    GetGuildTimeout,
    /** Push to talk is required for this channel */
    SelectVoiceForceRequired,
    /** That push to talk shortcut is already registered */
    CaptureShortcutAlreadyListening,
    /** Your application cannot update this achievement */
    UnauthorizedForAchievement,
    /** The gift code is not valid */
    InvalidGiftCode,
    /** Something went wrong during the purchase flow */
    PurchaseError,
    /** Purchase flow aborted because the SDK is being torn down */
    TransactionAborted,
    /**  */
    DrawingInitFailed,
    ;

    internal fun toInt(): Int {
        return this.ordinal
    }

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordCreateFlags {
    /**
     * **WARNING:** Default is potentially dangerous as the Game SDK will kill the
     * whole application when Discord isn't running or is closed
     */
    Default,
    NoRequireDiscord,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordLogLevel {
    Error,
    Warn,
    Info,
    Debug,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

typealias DiscordUserFlags = EnumSet<DiscordUserFlag>

enum class DiscordUserFlag(private val bit: Int) {
    Partner(1),
    HypeSquadEvents(2),
    HypeSquadHouse1(6),
    HypeSquadHouse2(7),
    HypeSquadHouse3(8),
    ;

    companion object {
        internal fun DiscordUserFlags.toInt(): Int = fold(0, { acc, flag -> acc or (1 shr flag.bit) })

        internal fun fromInt(i: Int): EnumSet<DiscordUserFlag> = values()
            .filterTo(
                EnumSet.noneOf<DiscordUserFlag>(DiscordUserFlag::class.java),
                { flag -> i and (1 shr flag.bit) != 0 }
            )
    }
}

enum class DiscordPremiumType {
    None,
    Tier1,
    Tier2,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordImageType {
    User,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordActivityPartyPrivacy {
    Private,
    Public,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordActivityType {
    Playing,
    Streaming,
    Listening,
    Watching,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordActivityActionType {
    Join,
    Spectate,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

enum class DiscordActivityJoinRequestReply {
    No,
    Yes,
    Ignore,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordStatus {
    Offline,
    Online,
    Idle,
    DoNotDisturb,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordRelationshipType {
    None,
    Friend,
    Blocked,
    PendingIncoming,
    PendingOutgoing,
    Implicit,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordLobbyType {
    Private,
    Public,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

enum class DiscordLobbySearchComparison {
    LessThanOrEqual,
    LessThan,
    Equal,
    GreaterThan,
    GreaterThanOrEqual,
    NotEqual,
    ;

    internal fun toInt() = this.ordinal - 2

    companion object {
        internal fun fromInt(i: Int) = values()[i + 2]
    }
}

enum class DiscordLobbySearchCast {
    String,
    Number,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

enum class DiscordLobbySearchDistance {
    Local,
    Default,
    Extended,
    Global,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordKeyVariant {
    Normal,
    Right,
    Left,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordMouseButton {
    Left,
    Middle,
    Right,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}

enum class DiscordEntitlementType {
    Purchase,
    PremiumSubscription,
    DeveloperGift,
    TestModePurchase,
    FreePurchase,
    UserGift,
    PremiumPurchase,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

enum class DiscordSkuType {
    Application,
    DLC,
    Consumable,
    Bundle,
    ;

    internal fun toInt() = this.ordinal + 1

    companion object {
        internal fun fromInt(i: Int) = values()[i - 1]
    }
}

enum class DiscordInputModeType {
    VoiceActivity,
    PushToTalk,
    ;

    internal fun toInt() = this.ordinal

    companion object {
        internal fun fromInt(i: Int) = values()[i]
    }
}
