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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*

/*
 * To simplify the native interface, this file contains "deconstructed" classes,
 * which are made out only of primitives and Strings
 */

internal class DeconstructedDiscordImageHandle(
    val type: Int,
    val id: int64_t,
    val size: uint32_t
) {
    fun construct() = DiscordImageHandle(type = DiscordImageType.fromInt(this.type), id = this.id, size = this.size)
}

internal fun DiscordImageHandle.deconstruct() = DeconstructedDiscordImageHandle(type = this.type.toInt(), id = this.id, size = this.size)

/**
 * "Deconstructed" DiscordActivity
 * @see DiscordActivity
 */
internal class DeconstructedDiscordActivity(
    val type: Int,
    val applicationId: int64_t,
    val name: @StringLength(max = 128) String,
    val state: @StringLength(max = 128) String,
    val details: @StringLength(max = 128) String,
    val timestampStart: DiscordTimestamp,
    val timestampEnd: DiscordTimestamp,
    val assetsLargeImage: @StringLength(max = 128) String,
    val assetsLargeText: @StringLength(max = 128) String,
    val assetsSmallImage: @StringLength(max = 128) String,
    val assetsSmallText: @StringLength(max = 128) String,
    val partyId: @StringLength(max = 128) String,
    val partyCurrentSize: int32_t,
    val partyMaxSize: int32_t,
    val partyPrivacy: Int,
    val secretsMatch: @StringLength(max = 128) String,
    val secretsJoin: @StringLength(max = 128) String,
    val secretsSpectate: @StringLength(max = 128) String,
    val instance: Boolean
) {
    fun construct() = DiscordActivity(
        type = DiscordActivityType.fromInt(this.type),
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
            privacy = DiscordActivityPartyPrivacy.fromInt(this.partyPrivacy)
        ),
        secrets = DiscordActivitySecrets(
            match = this.secretsMatch,
            join = this.secretsJoin,
            spectate = this.secretsSpectate
        ),
        instance = this.instance
    )
}

internal fun DiscordActivity.deconstruct() = DeconstructedDiscordActivity(
    type = this.type.toInt(),
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
    partyPrivacy = this.party.privacy.toInt(),
    secretsMatch = this.secrets.match,
    secretsJoin = this.secrets.join,
    secretsSpectate = this.secrets.spectate,
    instance = this.instance
)
