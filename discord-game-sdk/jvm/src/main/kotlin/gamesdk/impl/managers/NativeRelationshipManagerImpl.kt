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

package gamesdk.impl.managers

import gamesdk.api.DiscordRelationshipFilter
import gamesdk.api.DiscordRelationshipListSizeResult
import gamesdk.api.DiscordRelationshipResult
import gamesdk.api.events.EventBus
import gamesdk.api.events.RelationshipRefreshEvent
import gamesdk.api.events.RelationshipUpdateEvent
import gamesdk.api.managers.DiscordRelationshipListSize
import gamesdk.api.managers.RelationshipManager
import gamesdk.api.types.DiscordUserId
import gamesdk.impl.NativeCoreImpl
import gamesdk.impl.NativeObjectImpl

internal class NativeRelationshipManagerImpl(private val core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), RelationshipManager {
    override val refreshes: EventBus<RelationshipRefreshEvent>
        get() = core.events.relationshipRefreshes

    override val relationshipUpdates: EventBus<RelationshipUpdateEvent>
        get() = core.events.relationshipUpdates

    override fun count(): DiscordRelationshipListSizeResult =
        TODO("Not yet implemented")

    override fun filter(filter: DiscordRelationshipFilter): Unit =
        TODO("Not yet implemented")

    override fun get(userId: DiscordUserId): DiscordRelationshipResult =
        TODO("Not yet implemented")

    override fun getAt(index: DiscordRelationshipListSize): DiscordRelationshipResult =
        TODO("Not yet implemented")
}
