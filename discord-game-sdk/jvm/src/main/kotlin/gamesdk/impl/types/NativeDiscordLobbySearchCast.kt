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

import gamesdk.api.types.DiscordLobbySearchCast
import gamesdk.api.types.DiscordLobbySearchComparison
import gamesdk.api.types.DiscordLobbySearchDistance
import gamesdk.api.types.DiscordLobbyType

internal typealias NativeDiscordLobbySearchCast = Int

internal fun DiscordLobbySearchCast.toNativeDiscordLobbySearchCast(): NativeDiscordLobbySearchCast = this.ordinal + 1

internal fun NativeDiscordLobbySearchCast.toDiscordLobbySearchCast(): DiscordLobbySearchCast =
    when (this - 1) {
        in DiscordLobbySearchCast.values().indices -> DiscordLobbySearchCast.values()[this - 1]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordLobbySearchComparison = Int

internal fun DiscordLobbySearchComparison.toNativeDiscordLobbySearchComparison(): NativeDiscordLobbySearchComparison = this.ordinal - 2

internal fun NativeDiscordLobbySearchComparison.toDiscordLobbySearchComparison(): DiscordLobbySearchComparison =
    when (this + 2) {
        in DiscordLobbySearchComparison.values().indices -> DiscordLobbySearchComparison.values()[this + 2]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordLobbySearchDistance = Int

internal fun DiscordLobbySearchDistance.toNativeDiscordLobbySearchDistance(): NativeDiscordLobbySearchDistance = this.ordinal

internal fun NativeDiscordLobbySearchDistance.toDiscordLobbySearchDistance(): DiscordLobbySearchDistance =
    when (this) {
        in DiscordLobbySearchDistance.values().indices -> DiscordLobbySearchDistance.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordLobbyType = Int

internal fun DiscordLobbyType.toNativeDiscordLobbyType(): NativeDiscordLobbyType = this.ordinal + 1

internal fun NativeDiscordLobbyType.toDiscordLobbyType(): DiscordLobbyType =
    when (this - 1) {
        in DiscordLobbyType.values().indices -> DiscordLobbyType.values()[this - 1]
        else -> throw IllegalArgumentException()
    }
