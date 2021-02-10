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

public typealias DiscordApplicationId = DiscordSnowflake

public class DiscordActivity(
    public val type: DiscordActivityType = DiscordActivityType.Playing,
    public val applicationId: DiscordApplicationId,
    public val name: @StringLength(max = 128) String = "",
    public val state: @StringLength(max = 128) String = "",
    public val details: @StringLength(max = 128) String = "",
    public val timestamps: DiscordActivityTimestamps = DiscordActivityTimestamps(),
    public val assets: DiscordActivityAssets = DiscordActivityAssets(),
    public val party: DiscordActivityParty = DiscordActivityParty(),
    public val secrets: DiscordActivitySecrets = DiscordActivitySecrets(),
    public val instance: Boolean = false
)

public enum class DiscordActivityActionType {
    Join,
    Spectate,
}

public class DiscordActivityAssets(
    public val large_image: @StringLength(max = 128) String = "",
    public val large_text: @StringLength(max = 128) String = "",
    public val small_image: @StringLength(max = 128) String = "",
    public val small_text: @StringLength(max = 128) String = ""
)

public enum class DiscordActivityJoinRequestReply {
    No,
    Yes,
    Ignore,
}

public class DiscordActivityParty(
    public val id: @StringLength(max = 128) String = "",
    public val size: DiscordPartySize = DiscordPartySize(),
    public val privacy: DiscordActivityPartyPrivacy = DiscordActivityPartyPrivacy.Private
)

public enum class DiscordActivityPartyPrivacy {
    Private,
    Public,
}

public class DiscordActivitySecrets(
    public val match: @StringLength(max = 128) String = "",
    public val join: @StringLength(max = 128) String = "",
    public val spectate: @StringLength(max = 128) String = ""
)

public class DiscordActivityTimestamps(
    public val start: DiscordTimestamp = 0,
    public val end: DiscordTimestamp = 0
)

public enum class DiscordActivityType {
    Playing,
    Streaming,
    Listening,
    Watching,
}
