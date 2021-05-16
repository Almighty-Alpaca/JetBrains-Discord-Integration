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

import gamesdk.api.DiscordBooleanResult
import gamesdk.api.DiscordPremiumTypeResult
import gamesdk.api.DiscordUserResult
import gamesdk.api.DiscordUserResultCallback
import gamesdk.api.managers.UserManager
import gamesdk.api.types.DiscordUserFlag
import gamesdk.api.types.DiscordUserId
import gamesdk.impl.*
import gamesdk.impl.types.*

internal class NativeUserManagerImpl(private val core: NativeCoreImpl) : NativeObjectImpl.Delegate(core), UserManager {
    override val currentUserUpdates
        get() = core.events.currentUserUpdates

    override fun getCurrentUser(): DiscordUserResult =
        native { pointer -> getCurrentUser(pointer).toDiscordObjectResult(NativeDiscordUser::toDiscordUser) }

    override fun getUser(userId: DiscordUserId, callback: DiscordUserResultCallback) =
        native { pointer -> getUser(pointer, userId, callback.toNativeDiscordResultObjectCallback(NativeDiscordUser::toDiscordUser)) }

    override suspend fun getUser(userId: DiscordUserId): DiscordUserResult =
        suspendCallback { callback -> getUser(userId, callback) }

    override fun getCurrentUserPremiumType(): DiscordPremiumTypeResult =
        native { pointer -> getCurrentUserPremiumType(pointer).toDiscordObjectResult(NativeDiscordPremiumType::toDiscordPremiumType) }

    override fun currentUserHasFlag(flag: DiscordUserFlag): DiscordBooleanResult =
        native { pointer -> currentUserHasFlag(pointer, flag.toNativeDiscordUserFlag()).toDiscordObjectResult() }
}

private external fun Native.getCurrentUser(pointer: NativePointer): NativeDiscordUserResult

private external fun Native.getUser(pointer: NativePointer, userId: DiscordUserId, callback: NativeDiscordUserResultCallback)

private external fun Native.getCurrentUserPremiumType(pointer: NativePointer): NativeDiscordPremiumTypeResult

private external fun Native.currentUserHasFlag(pointer: NativePointer, flag: NativeDiscordUserFlag): NativeDiscordBooleanResult
