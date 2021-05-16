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
import gamesdk.api.types.DiscordUser
import gamesdk.api.types.DiscordUserFlag
import gamesdk.api.types.DiscordUserFlags
import java.util.*

internal class NativeDiscordUser(
    val id: NativeDiscordUserId,
    val username: @StringLength(max = 256) NativeString,
    val discriminator: @StringLength(max = 8) NativeString,
    val avatar: @StringLength(max = 128) NativeString,
    val bot: Boolean
)

internal fun DiscordUser.toNativeDiscordUser(): NativeDiscordUser = NativeDiscordUser(
    id = id,
    username = username.toNativeString(),
    discriminator = discriminator.toNativeString(),
    avatar = avatar.toNativeString(),
    bot = bot
)

internal fun NativeDiscordUser.toDiscordUser(): DiscordUser = DiscordUser(
    id = id,
    username = username.toKotlinString(),
    discriminator = discriminator.toKotlinString(),
    avatar = avatar.toKotlinString(),
    bot = bot
)

internal typealias  NativeDiscordUserFlag = Int

internal fun DiscordUserFlag.toNativeDiscordUserFlag(): NativeDiscordUserFlag = this.ordinal

internal fun NativeDiscordUserFlag.toDiscordUserFlag(): DiscordUserFlag =
    when (this) {
        in DiscordUserFlag.values().indices -> DiscordUserFlag.values()[this]
        else -> throw IllegalArgumentException()
    }
internal typealias  NativeDiscordUserFlags = Int

internal fun DiscordUserFlags.toNativeDiscordUserFlags(): NativeDiscordUserFlags =
    this.fold(0) { acc, i -> acc + (1 shl i.offset) }

internal fun NativeDiscordUserFlags.toDiscordUserFlags(): DiscordUserFlags =
    DiscordUserFlag
        .values()
        .filterTo(EnumSet.noneOf(DiscordUserFlag::class.java)) {
            (this shr it.offset) and 1 == 1
        }
