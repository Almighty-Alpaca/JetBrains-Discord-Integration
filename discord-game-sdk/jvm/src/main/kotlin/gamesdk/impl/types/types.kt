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

internal typealias int8_t = Byte
@OptIn(ExperimentalUnsignedTypes::class)
internal typealias uint8_t = UByte
internal typealias int32_t = Int
@OptIn(ExperimentalUnsignedTypes::class)
internal typealias uint32_t = UInt
internal typealias int64_t = Long
@OptIn(ExperimentalUnsignedTypes::class)
internal typealias uint64_t = ULong

internal typealias NativeString = ByteArray

internal fun String.toNativeString(): NativeString = this.encodeToByteArray()

internal fun NativeString.toKotlinString(): String = this.decodeToString()

internal typealias NativeDiscordClientId = uint64_t
internal typealias NativeDiscordSnowflake = int64_t
internal typealias NativeDiscordTimestamp = int64_t
internal typealias NativeDiscordUserId = NativeDiscordSnowflake
internal typealias NativeDiscordLocale = @StringLength(max = 128) NativeString
internal typealias NativeDiscordBranch = @StringLength(max = 4096) NativeString
internal typealias NativeDiscordMetadataKey = @StringLength(max = 256) NativeString
internal typealias NativeDiscordMetadataValue = @StringLength(max = 4096) NativeString
internal typealias NativeDiscordNetworkPeerId = uint64_t
internal typealias NativeDiscordNetworkChannelId = uint8_t
internal typealias NativeDiscordPath = @StringLength(max = 4096) NativeString
internal typealias NativeDiscordDateTime = @StringLength(max = 64) NativeString

internal typealias NativeSteamId = uint32_t

