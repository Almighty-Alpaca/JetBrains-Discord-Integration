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
import java.util.*

public class DiscordUser(
    public val id: DiscordUserId,
    public val username: @StringLength(max = 256) String,
    public val discriminator: @StringLength(max = 8) String,
    public val avatar: @StringLength(max = 128) String,
    public val bot: Boolean
)

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordUserAchievementPercentage = UByte

public class DiscordUserAchievement(
    public val userId: DiscordUserId,
    public val achievementId: DiscordSnowflake,
    public val percentComplete: DiscordUserAchievementPercentage,
    public val unlockedAt: DiscordDateTime
)

public enum class DiscordUserFlag(public val offset: Int) {
    Partner(1),
    HypeSquadEvents(2),
    HypeSquadHouse1(6),
    HypeSquadHouse2(7),
    HypeSquadHouse3(8),
}

public typealias DiscordUserFlags = EnumSet<DiscordUserFlag>
