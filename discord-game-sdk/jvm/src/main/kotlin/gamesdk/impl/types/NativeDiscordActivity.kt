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

package gamesdk.impl.types

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.StringLength
import gamesdk.api.types.*

public typealias NativeDiscordApplicationId = NativeDiscordSnowflake

public typealias NativeDiscordPartySizeValue = int32_t

internal class NativeDiscordActivity(
    val type: NativeDiscordActivityType,
    val applicationId: NativeDiscordApplicationId,
    val name: @StringLength(max = 128) NativeString,
    val state: @StringLength(max = 128) NativeString,
    val details: @StringLength(max = 128) NativeString,
    val timestampStart: NativeDiscordTimestamp,
    val timestampEnd: NativeDiscordTimestamp,
    val assetsLargeImage: @StringLength(max = 128) NativeString,
    val assetsLargeText: @StringLength(max = 128) NativeString,
    val assetsSmallImage: @StringLength(max = 128) NativeString,
    val assetsSmallText: @StringLength(max = 128) NativeString,
    val partyId: @StringLength(max = 128) NativeString,
    val partyCurrentSize: NativeDiscordPartySizeValue,
    val partyMaxSize: NativeDiscordPartySizeValue,
    val partyPrivacy: NativeDiscordActivityPartyPrivacy,
    val secretsMatch: @StringLength(max = 128) NativeString,
    val secretsJoin: @StringLength(max = 128) NativeString,
    val secretsSpectate: @StringLength(max = 128) NativeString,
    val instance: Boolean
)

/*
* TODO: Maybe check the length in before passing it though JNI?
*       Currently they are just being cut to length
 */
internal fun DiscordActivity.toNativeDiscordActivity(): NativeDiscordActivity = NativeDiscordActivity(
    type = this.type.toNativeDiscordActivityType(),
    applicationId = this.applicationId,
    name = this.name.toNativeString(),
    state = this.state.toNativeString(),
    details = this.details.toNativeString(),
    timestampStart = this.timestamps.start,
    timestampEnd = this.timestamps.end,
    assetsLargeImage = this.assets.large_image.toNativeString(),
    assetsLargeText = this.assets.large_text.toNativeString(),
    assetsSmallImage = this.assets.small_image.toNativeString(),
    assetsSmallText = this.assets.small_text.toNativeString(),
    partyId = this.party.id.toNativeString(),
    partyCurrentSize = this.party.size.currentSize,
    partyMaxSize = this.party.size.maxSize,
    partyPrivacy = this.party.privacy.toNativeDiscordActivityPartyPrivacy(),
    secretsMatch = this.secrets.match.toNativeString(),
    secretsJoin = this.secrets.join.toNativeString(),
    secretsSpectate = this.secrets.spectate.toNativeString(),
    instance = this.instance
)

internal fun NativeDiscordActivity.toDiscordActivity(): DiscordActivity = DiscordActivity(
    type = this.type.toDiscordActivityType(),
    applicationId = this.applicationId,
    name = this.name.toKotlinString(),
    state = this.state.toKotlinString(),
    details = this.details.toKotlinString(),
    timestamps = DiscordActivityTimestamps(
        start = timestampStart,
        end = timestampEnd
    ),
    assets = DiscordActivityAssets(
        large_image = this.assetsLargeImage.toKotlinString(),
        large_text = this.assetsLargeText.toKotlinString(),
        small_image = this.assetsSmallImage.toKotlinString(),
        small_text = this.assetsSmallText.toKotlinString()
    ),
    party = DiscordActivityParty(
        id = this.partyId.toKotlinString(),
        size = DiscordPartySize(
            currentSize = this.partyCurrentSize,
            maxSize = this.partyMaxSize
        ),
        privacy = this.partyPrivacy.toDiscordActivityPartyPrivacy()
    ),
    secrets = DiscordActivitySecrets(
        match = this.secretsMatch.toKotlinString(),
        join = this.secretsJoin.toKotlinString(),
        spectate = this.secretsSpectate.toKotlinString()
    ),
    instance = this.instance
)

internal typealias NativeDiscordActivityActionType = Int

internal fun DiscordActivityActionType.toNativeDiscordActivityActionType(): NativeDiscordActivityActionType = this.ordinal + 1

internal fun NativeDiscordActivityActionType.toDiscordActivityActionType(): DiscordActivityActionType =
    when (this - 1) {
        in DiscordActivityActionType.values().indices -> DiscordActivityActionType.values()[this - 1]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordActivityJoinRequestReply = Int

internal fun DiscordActivityJoinRequestReply.toNativeDiscordActivityJoinRequestReply(): NativeDiscordActivityJoinRequestReply = this.ordinal

internal fun NativeDiscordActivityJoinRequestReply.toDiscordActivityJoinRequestReply(): DiscordActivityJoinRequestReply =
    when (this) {
        in DiscordActivityJoinRequestReply.values().indices -> DiscordActivityJoinRequestReply.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordActivityPartyPrivacy = Int

internal fun DiscordActivityPartyPrivacy.toNativeDiscordActivityPartyPrivacy(): NativeDiscordActivityPartyPrivacy = this.ordinal

internal fun NativeDiscordActivityPartyPrivacy.toDiscordActivityPartyPrivacy(): DiscordActivityPartyPrivacy =
    when (this) {
        in DiscordActivityPartyPrivacy.values().indices -> DiscordActivityPartyPrivacy.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordActivityType = Int

internal fun DiscordActivityType.toNativeDiscordActivityType(): NativeDiscordActivityType = this.ordinal

internal fun NativeDiscordActivityType.toDiscordActivityType(): DiscordActivityType =
    when (this) {
        in DiscordActivityType.values().indices -> DiscordActivityType.values()[this]
        else -> throw IllegalArgumentException()
    }
