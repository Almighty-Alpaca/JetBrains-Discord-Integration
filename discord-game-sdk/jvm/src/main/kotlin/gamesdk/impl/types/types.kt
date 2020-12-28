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

public typealias int8_t = Byte
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint8_t = UByte
public typealias int32_t = Int
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint32_t = UInt
public typealias int64_t = Long
@OptIn(ExperimentalUnsignedTypes::class)
public typealias uint64_t = ULong

public typealias NativeDiscordClientId = uint64_t
public typealias NativeDiscordSnowflake = int64_t
public typealias NativeDiscordTimestamp = int64_t
public typealias NativeDiscordUserId = NativeDiscordSnowflake
public typealias NativeDiscordLocale = @StringLength(max = 128) String
public typealias NativeDiscordBranch = @StringLength(max = 4096) String
public typealias NativeDiscordMetadataKey = @StringLength(max = 256) String
public typealias NativeDiscordMetadataValue = @StringLength(max = 4096) String
public typealias NativeDiscordNetworkPeerId = uint64_t
public typealias NativeDiscordNetworkChannelId = uint8_t
public typealias NativeDiscordPath = @StringLength(max = 4096) String
public typealias NativeDiscordDateTime = @StringLength(max = 64) String

public typealias NativeSteamId = uint32_t

