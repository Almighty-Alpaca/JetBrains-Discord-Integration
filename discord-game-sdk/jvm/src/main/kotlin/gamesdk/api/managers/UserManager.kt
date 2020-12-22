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

package gamesdk.api.managers

import gamesdk.api.*
import gamesdk.api.events.CurrentUserUpdateEvent
import gamesdk.api.events.EventBus
import gamesdk.api.types.DiscordUserFlag
import gamesdk.api.types.DiscordUserId

public interface UserManager : NativeObject {
    public val currentUserUpdates: EventBus<CurrentUserUpdateEvent>

    public fun getCurrentUser(): DiscordUserResult

    public fun getUser(userId: DiscordUserId, callback: DiscordUserResultCallback)
    public suspend fun getUser(userId: DiscordUserId): DiscordUserResult

    public fun getCurrentUserPremiumType(): DiscordPremiumTypeResult

    public fun currentUserHasFlag(flag: DiscordUserFlag): DiscordBooleanResult
}
