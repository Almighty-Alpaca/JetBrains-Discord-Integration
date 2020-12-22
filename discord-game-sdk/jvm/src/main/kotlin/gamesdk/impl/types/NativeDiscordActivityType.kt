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

import gamesdk.api.types.DiscordActivityType

internal typealias NativeDiscordActivityType = Int

internal fun DiscordActivityType.toNativeDiscordActivityType(): NativeDiscordActivityType = this.ordinal

internal fun NativeDiscordActivityType.toDiscordActivityType(): DiscordActivityType =
    when (this) {
        in DiscordActivityType.values().indices -> DiscordActivityType.values()[this]
        else -> throw IllegalArgumentException()
    }
