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

import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.DiscordRelationshipListSizeResult
import gamesdk.api.DiscordRelationshipResult
import gamesdk.api.events.EventBus
import gamesdk.api.events.RelationshipRefreshEvent
import gamesdk.api.events.RelationshipUpdateEvent
import gamesdk.api.types.DiscordUserId

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordRelationshipListSize = UInt

public interface RelationshipManager {
    public val refreshes: EventBus<RelationshipRefreshEvent>
    public val relationshipUpdates: EventBus<RelationshipUpdateEvent>

    public fun filter(filter: DiscordRelationshipFilter)
    public fun count(): DiscordRelationshipListSizeResult
    public fun get(userId: DiscordUserId): DiscordRelationshipResult
    public fun getAt(index: DiscordRelationshipListSize): DiscordRelationshipResult
}
