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
    val name: @StringLength(max = 128) String,
    val state: @StringLength(max = 128) String,
    val details: @StringLength(max = 128) String,
    val timestampStart: NativeDiscordTimestamp,
    val timestampEnd: NativeDiscordTimestamp,
    val assetsLargeImage: @StringLength(max = 128) String,
    val assetsLargeText: @StringLength(max = 128) String,
    val assetsSmallImage: @StringLength(max = 128) String,
    val assetsSmallText: @StringLength(max = 128) String,
    val partyId: @StringLength(max = 128) String,
    val partyCurrentSize: NativeDiscordPartySizeValue,
    val partyMaxSize: NativeDiscordPartySizeValue,
    val partyPrivacy: NativeDiscordActivityPartyPrivacy,
    val secretsMatch: @StringLength(max = 128) String,
    val secretsJoin: @StringLength(max = 128) String,
    val secretsSpectate: @StringLength(max = 128) String,
    val instance: Boolean
)

internal fun DiscordActivity.toNativeDiscordActivity(): NativeDiscordActivity = NativeDiscordActivity(
    type = this.type.toNativeDiscordActivityType(),
    applicationId = this.applicationId,
    name = this.name,
    state = this.state,
    details = this.details,
    timestampStart = this.timestamps.start,
    timestampEnd = this.timestamps.end,
    assetsLargeImage = this.assets.large_image,
    assetsLargeText = this.assets.large_text,
    assetsSmallImage = this.assets.small_image,
    assetsSmallText = this.assets.small_text,
    partyId = this.party.id,
    partyCurrentSize = this.party.size.currentSize,
    partyMaxSize = this.party.size.maxSize,
    partyPrivacy = this.party.privacy.toNativeDiscordActivityPartyPrivacy(),
    secretsMatch = this.secrets.match,
    secretsJoin = this.secrets.join,
    secretsSpectate = this.secrets.spectate,
    instance = this.instance
)

internal fun NativeDiscordActivity.toDiscordActivity(): DiscordActivity = DiscordActivity(
    type = this.type.toDiscordActivityType(),
    applicationId = this.applicationId,
    name = this.name,
    state = this.state,
    details = this.details,
    timestamps = DiscordActivityTimestamps(
        start = timestampStart,
        end = timestampEnd
    ),
    assets = DiscordActivityAssets(
        large_image = this.assetsLargeImage,
        large_text = this.assetsLargeText,
        small_image = this.assetsSmallImage,
        small_text = this.assetsSmallText
    ),
    party = DiscordActivityParty(
        id = this.partyId,
        size = DiscordPartySize(
            currentSize = this.partyCurrentSize,
            maxSize = this.partyMaxSize
        ),
        privacy = this.partyPrivacy.toDiscordActivityPartyPrivacy()
    ),
    secrets = DiscordActivitySecrets(
        match = this.secretsMatch,
        join = this.secretsJoin,
        spectate = this.secretsSpectate
    ),
    instance = this.instance
)

internal typealias NativeDiscordActivityActionType = Int

internal fun DiscordActivityActionType.toNativeDiscordActivityActionType(): NativeDiscordActivityActionType = this.ordinal

internal fun NativeDiscordActivityActionType.toDiscordActivityActionType(): DiscordActivityActionType =
    when (this) {
        in DiscordActivityActionType.values().indices -> DiscordActivityActionType.values()[this]
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
