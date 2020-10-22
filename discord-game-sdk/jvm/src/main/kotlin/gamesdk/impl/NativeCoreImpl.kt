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
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordResult
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Failure
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Result
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.Success
import gamesdk.api.ActivityManager
import gamesdk.api.ClientId
import gamesdk.api.Core
import gamesdk.impl.utils.CloseableNativeObject
import gamesdk.impl.utils.Native
import gamesdk.impl.utils.NativeLoader
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class NativeCoreImpl private constructor(pointer: Pointer) : CloseableNativeObject(pointer), Core {
    override val activityManager: ActivityManager by nativeLazy { NativeActivityManagerImpl(this) }

    private var runner: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        runner.scheduleAtFixedRate({
            val result = runCallbacks()
            if (result != DiscordResult.Ok) {
                println("Error running callbacks: $result")
            }
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    private fun runCallbacks(): DiscordResult = native { corePointer -> runCallbacks(corePointer).toDiscordResult() }

    override val destructor: Native.(Pointer) -> Unit = Native::destroy

    @Synchronized
    override fun close() {
        runner.shutdown()
        runner.awaitTermination(10, TimeUnit.SECONDS)
        runner.shutdownNow()

        super.close()
    }

    companion object {
        init {
            NativeLoader.loadLibraries(NativeCoreImpl::class.java.classLoader, "discord_game_sdk", "discord_game_sdk_cpp", "discord_game_sdk_kotlin")
        }

        fun create(clientId: ClientId, createFlags: DiscordCreateFlags): Result<Core, DiscordResult> {
            return when (val result = nativeCreate(clientId, createFlags.toNative())) {
                is Long -> Success(NativeCoreImpl(result))
                is Int -> Failure(result.toDiscordResult())
                else -> throw IllegalStateException() // This should never happen unless the native method returns garbage
            }
        }
    }
}

/**
 * This one can't have Native as receiver because it's creating the object
 *
 * @return Either an Int or a Long
 */
private external fun nativeCreate(clientId: ClientId, createFlags: Int): Any

private external fun Native.destroy(core: Pointer)

private external fun Native.runCallbacks(core: Pointer): Int
