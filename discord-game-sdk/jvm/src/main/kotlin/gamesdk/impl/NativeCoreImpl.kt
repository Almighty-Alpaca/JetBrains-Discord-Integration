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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordCreateFlags
import gamesdk.api.ActivityManager
import gamesdk.api.ClientId
import gamesdk.api.Core
import gamesdk.impl.utils.CloseableNativeObject
import gamesdk.impl.utils.NativeLoader

internal class NativeCoreImpl(clientId: ClientId, createFlags: DiscordCreateFlags) : CloseableNativeObject(nativeCreate(clientId, createFlags.toInt())), Core {
    override val activityManager: ActivityManager by nativeLazy { NativeActivityManagerImpl(this) }

    companion object {
        init {
            NativeLoader.loadLibraries(NativeCoreImpl::class.java.classLoader, "discord_game_sdk", "discord_game_sdk_cpp", "discord_game_sdk_kotlin")
        }
    }
}

// This one can't have Native as receiver because it's creating the object
private external fun nativeCreate(clientId: ClientId, createFlags: Int): Pointer
