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

public sealed class DiscordCode(public val ordinal: Int) {
    private val name = this::class.simpleName

    override fun toString(): String = "$name($ordinal)"


    /** Everything is good */
    public object Ok : DiscordCode(0)

    public sealed class Failure(native: Int) : DiscordCode(native) {
        /** Discord isn't working */
        public object ServiceUnavailable : Failure(1)

        /** The SDK version may be outdated */
        public object InvalidVersion : Failure(2)

        /** An internal error on transactional operations */
        public object LockFailed : Failure(3)

        /** Something on our side went wrong */
        public object InternalError : Failure(4)

        /** The data you sent didn't match what we expect */
        public object InvalidPayload : Failure(5)

        /** That's not a thing you can do */
        public object InvalidCommand : Failure(6)

        /** You aren't authorized to do that */
        public object InvalidPermissions : Failure(7)

        /** Couldn't fetch what you wanted */
        public object NotFetched : Failure(8)

        /** What you're looking for doesn't exist */
        public object NotFound : Failure(9)

        /** User already has a network connection open on that channel */
        public object Conflict : Failure(10)

        /** Activity secrets must be unique and not match party id */
        public object InvalidSecret : Failure(11)

        /** Join request for that user does not exist */
        public object InvalidJoinSecret : Failure(12)

        /** You accidentally set an `ApplicationId` in your `UpdateActivity()` payload */
        public object NoEligibleActivity : Failure(13)

        /** Your game invite is no longer valid */
        public object InvalidInvite : Failure(14)

        /** The internal auth call failed for the user and you can't do this */
        public object NotAuthenticated : Failure(15)

        /** The user's bearer token is invalid */
        public object InvalidAccessToken : Failure(16)

        /** Access token belongs to another application */
        public object ApplicationMismatch : Failure(17)

        /** Something internally went wrong fetching image data */
        public object InvalidDataUrl : Failure(18)

        /** Not valid Base64 data */
        public object InvalidBase64 : Failure(19)

        /** You're trying to access the list before creating a stable list with Filter() */
        public object NotFiltered : Failure(20)

        /** The lobby is full */
        public object LobbyFull : Failure(21)

        /** The secret you're using to connect is wrong */
        public object InvalidLobbySecret : Failure(22)

        /** File name is too long */
        public object InvalidFilename : Failure(23)

        /** File is too large */
        public object InvalidFileSize : Failure(24)

        /** The user does not have the right entitlement for this game */
        public object InvalidEntitlement : Failure(25)

        /** Discord is not installed */
        public object NotInstalled : Failure(26)

        /** Discord is not running */
        public object NotRunning : Failure(27)

        /** Insufficient buffer space when trying to write */
        public object InsufficientBuffer : Failure(28)

        /** User cancelled the purchase flow */
        public object PurchaseCanceled : Failure(29)

        /** Discord guild does not exist */
        public object InvalidGuild : Failure(30)

        /** The event you're trying to subscribe to does not exist */
        public object InvalidEvent : Failure(31)

        /** Discord channel does not exist */
        public object InvalidChannel : Failure(32)

        /** The origin header on the socket does not match what you've registered (you should not see this) */
        public object InvalidOrigin : Failure(33)

        /** You are calling that method too quickly */
        public object RateLimited : Failure(34)

        /** The OAuth2 process failed at some point */
        public object OAuth2Error : Failure(35)

        /** The user took too long selecting a channel for an invite */
        public object SelectChannelTimeout : Failure(36)

        /** Took too long trying to fetch the guild */
        public object GetGuildTimeout : Failure(37)

        /** Push to talk is required for this channel */
        public object SelectVoiceForceRequired : Failure(38)

        /** That push to talk shortcut is already registered */
        public object CaptureShortcutAlreadyListening : Failure(39)

        /** Your application cannot update this achievement */
        public object UnauthorizedForAchievement : Failure(40)

        /** The gift code is not valid */
        public object InvalidGiftCode : Failure(41)

        /** Something went wrong during the purchase flow */
        public object PurchaseError : Failure(42)

        /** Purchase flow aborted because the SDK is being torn down */
        public object TransactionAborted : Failure(43)

        /**  */
        public object DrawingInitFailed : Failure(44)

        public companion object {
            public val VALUES: List<Failure> by lazy {
                listOf(
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
                    DrawingInitFailed
                )
            }
        }
    }

    public companion object {
        public val VALUES: List<DiscordCode> by lazy { listOf(Ok) + Failure.VALUES }
    }
}
