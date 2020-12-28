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

package gamesdk.api.types

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.StringLength

public typealias DiscordLobbyId = DiscordSnowflake

public typealias DiscordLobbySecret = @StringLength(max = 128) String

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordLobbyCapacity = UInt

public class DiscordLobby(
    public val id: DiscordLobbyId,
    public val type: DiscordLobbyType,
    public val ownerId: DiscordUserId,
    public val secret: DiscordLobbySecret,
    public val capacity: DiscordLobbyCapacity,
    public val locked: Boolean
)

public enum class DiscordLobbySearchCast {
    String,
    Number,
}

public enum class DiscordLobbySearchComparison {
    LessThanOrEqual,
    LessThan,
    Equal,
    GreaterThan,
    GreaterThanOrEqual,
    NotEqual,
}

public enum class DiscordLobbySearchDistance {
    Local,
    Default,
    Extended,
    Global,
}

public enum class DiscordLobbyType {
    Private,
    Public,
}
