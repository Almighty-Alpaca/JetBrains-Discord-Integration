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

package gamesdk.impl

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DeconstructedDiscordActivity
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordActivity
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordResult
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.deconstruct
import gamesdk.api.ActivityManager
import gamesdk.api.SteamId
import gamesdk.impl.utils.DelegateNativeObject
import gamesdk.impl.utils.Native

internal class NativeActivityManagerImpl(core: NativeCoreImpl) : DelegateNativeObject(core), ActivityManager {
    override fun registerCommand(command: String): DiscordResult = native { corePointer -> registerCommand(corePointer, command).toDiscordResult() }
    override fun registerSteam(steamId: SteamId): DiscordResult = native { corePointer -> registerSteam(corePointer, steamId).toDiscordResult() }

    override suspend fun updateActivity(activity: DiscordActivity): DiscordResult =
        suspendCallback { callback ->
            native { corePointer ->
                updateActivity(corePointer, activity.deconstruct(), callback.toNative())
            }
        }

    override suspend fun clearActivity(): DiscordResult = suspendCallback { callback ->
        native { corePointer ->
            clearActivity(corePointer, callback.toNative())
        }
    }
}

private external fun Native.registerCommand(core: Pointer, command: String): Int

private external fun Native.registerSteam(core: Pointer, steamId: SteamId): Int

private external fun Native.updateActivity(core: Pointer, activity: DeconstructedDiscordActivity, callback: NativeDiscordResultCallback)

private external fun Native.clearActivity(core: Pointer, callback: NativeDiscordResultCallback)
