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
import gamesdk.api.types.DiscordOAuth2Token

internal class NativeDiscordOAuth2Token(
    val accessToken: @StringLength(max = 128) NativeString,
    val scopes: @StringLength(max = 1024) NativeString,
    val expires: NativeDiscordTimestamp
)

internal fun DiscordOAuth2Token.toNativeDiscordOAuth2Token(): NativeDiscordOAuth2Token = NativeDiscordOAuth2Token(
    accessToken = accessToken.toNativeString(),
    scopes = scopes.toNativeString(),
    expires = expires
)

internal fun NativeDiscordOAuth2Token.toDiscordOAuth2Token(): DiscordOAuth2Token = DiscordOAuth2Token(
    accessToken = accessToken.toKotlinString(),
    scopes = scopes.toKotlinString(),
    expires = expires
)
