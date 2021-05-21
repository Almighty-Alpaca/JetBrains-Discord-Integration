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

@file:OptIn(ExperimentalUnsignedTypes::class)

package gamesdk.api.types

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.StringLength

public typealias DiscordClientId = ULong
public typealias DiscordSnowflake = Long
public typealias DiscordTimestamp = Long
public typealias DiscordUserId = DiscordSnowflake
public typealias DiscordLocale = @StringLength(max = 128) String
public typealias DiscordBranch = @StringLength(max = 4096) String
public typealias DiscordMetadataKey = @StringLength(max = 256) String
public typealias DiscordMetadataValue = @StringLength(max = 4096) String
public typealias DiscordNetworkPeerId = ULong
public typealias DiscordNetworkChannelId = UByte
public typealias DiscordPath = @StringLength(max = 4096) String
public typealias DiscordDateTime = @StringLength(max = 64) String

public typealias SteamId = UInt
