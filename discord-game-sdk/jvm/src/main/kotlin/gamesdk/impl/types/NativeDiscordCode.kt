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

import gamesdk.api.types.DiscordCode

internal typealias NativeDiscordCode = Int

internal fun DiscordCode.toNativeDiscordCode(): NativeDiscordCode = this.ordinal

internal fun NativeDiscordCode.toDiscordCode(): DiscordCode =
    when (this) {
        in DiscordCode.VALUES.indices -> DiscordCode.VALUES[this]
        else -> throw IllegalArgumentException()
    }

internal fun NativeDiscordCode.toDiscordFailureCode(): DiscordCode.Failure =
    when (this) {
        in DiscordCode.Failure.VALUES.indices -> DiscordCode.Failure.VALUES[this]
        else -> throw IllegalArgumentException()
    }
