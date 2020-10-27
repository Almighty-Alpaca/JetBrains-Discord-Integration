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

package gamesdk.api

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.DiscordResult
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Core implementation that automatically runs callbacks
 */
class ThreadedCore private constructor(private val core: Core) : Core by core {
    private var runner: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        runner.scheduleAtFixedRate({
            val result = core.runCallbacks()
            if (result != DiscordResult.Ok) {
                println("Error running callbacks: $result")
            }
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    // TODO: Find out how to synchronize this
    override fun close() {
        runner.shutdown()
        runner.awaitTermination(5, TimeUnit.SECONDS)
        runner.shutdownNow()

        core.close()
    }

    companion object {
        fun create(core: Core): ThreadedCore {
            return when (core) {
                is ThreadedCore -> core
                else -> ThreadedCore(core)
            }
        }
    }
}
