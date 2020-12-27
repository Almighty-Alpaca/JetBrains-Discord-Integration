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

@file:Suppress("CanSealedSubClassBeObject")

package gamesdk.impl.events

import gamesdk.api.events.CurrentUserUpdateEvent
import gamesdk.api.events.RelationshipRefreshEvent
import gamesdk.api.events.RelationshipUpdateEvent
import gamesdk.impl.types.NativeDiscordRelationship
import gamesdk.impl.types.toDiscordRelationship
import gamesdk.impl.types.toNativeDiscordRelationship

internal sealed class NativeEvent

internal sealed class NativeUserEvent : NativeEvent()

internal class NativeCurrentUserUpdateEvent : NativeUserEvent()

internal fun CurrentUserUpdateEvent.toNativeCurrentUserUpdateEvent(): NativeCurrentUserUpdateEvent = NativeCurrentUserUpdateEvent()

internal fun NativeCurrentUserUpdateEvent.toCurrentUserUpdateEvent(): CurrentUserUpdateEvent = CurrentUserUpdateEvent()

internal sealed class NativeRelationshipEvent : NativeEvent()

internal class NativeRelationshipRefreshEvent : NativeRelationshipEvent()

internal fun RelationshipRefreshEvent.toNativeRelationshipRefreshEvent(): NativeRelationshipRefreshEvent = NativeRelationshipRefreshEvent()

internal fun NativeRelationshipRefreshEvent.toRelationshipRefreshEvent(): RelationshipRefreshEvent = RelationshipRefreshEvent()

internal class NativeRelationshipUpdateEvent(internal val relationship: NativeDiscordRelationship) : NativeRelationshipEvent()

internal fun RelationshipUpdateEvent.toNativeRelationshipUpdateEvent(): NativeRelationshipUpdateEvent = NativeRelationshipUpdateEvent(
    relationship = relationship.toNativeDiscordRelationship()
)

internal fun NativeRelationshipUpdateEvent.toRelationshipUpdateEvent(): RelationshipUpdateEvent = RelationshipUpdateEvent(
    relationship = relationship.toDiscordRelationship()
)
