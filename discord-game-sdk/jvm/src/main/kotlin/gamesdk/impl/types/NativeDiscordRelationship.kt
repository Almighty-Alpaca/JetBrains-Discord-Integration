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

import gamesdk.api.types.DiscordRelationship
import gamesdk.api.types.DiscordRelationshipType

internal class NativeDiscordRelationship(
    internal val type: NativeDiscordRelationshipType,
    internal val user: NativeDiscordUser,
    internal val presence: NativeDiscordPresence
)

internal fun DiscordRelationship.toNativeDiscordRelationship(): NativeDiscordRelationship = NativeDiscordRelationship(
    type = type.toNativeDiscordRelationshipType(),
    user = user.toNativeDiscordUser(),
    presence = presence.toNativeDiscordPresence()
)

internal fun NativeDiscordRelationship.toDiscordRelationship(): DiscordRelationship = DiscordRelationship(
    type = type.toDiscordRelationshipType(),
    user = user.toDiscordUser(),
    presence = presence.toDiscordPresence()
)

internal typealias NativeDiscordRelationshipType = Int

internal fun DiscordRelationshipType.toNativeDiscordRelationshipType(): NativeDiscordRelationshipType = this.ordinal

internal fun NativeDiscordRelationshipType.toDiscordRelationshipType(): DiscordRelationshipType =
    when (this) {
        in DiscordRelationshipType.values().indices -> DiscordRelationshipType.values()[this]
        else -> throw IllegalArgumentException()
    }
