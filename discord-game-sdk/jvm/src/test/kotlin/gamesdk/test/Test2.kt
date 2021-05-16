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

package gamesdk.test

import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.DiscordCore
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.successOrNull
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.DiscordCoreImpl
import gamesdk.api.DiscordObjectResult
import gamesdk.api.types.DiscordActivity
import gamesdk.api.types.DiscordCode
import gamesdk.api.types.DiscordCreateFlags
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Test2 {
    @OptIn(ExperimentalUnsignedTypes::class)
    private val clientId = 768507783167344680U
    private val applicationId = 768507783167344680

    @Test
    @OptIn(ExperimentalTime::class, ExperimentalUnsignedTypes::class)
    fun testActivity2() {
        runBlocking {
            var core: DiscordCore? = DiscordCoreImpl.create(clientId, DiscordCreateFlags.NoRequireDiscord).successOrNull()

            // core.setLogHook(DiscordLogLevel.Debug) { level, message ->
            //     println("Discord($level): $message")
            // }

            for (i in 0..180) {
                if (core != null) {
                    println("Running")
                    if (i % 15 == 0) {
                        val activity = DiscordActivity(applicationId = applicationId, state = "Waiting", details = "...")

                        core.activityManager.updateActivity(activity) { result ->
                            println(result)
                        }
                    }

                    val result = core.runCallbacks()
                    println("Callback result: $result")

                    if (result == DiscordCode.Failure.NotRunning) {
                        println("Discord is not running anymore")
                        core.close()
                        core = null
                    }
                } else {
                    println("Trying to reconnect")

                    core = DiscordCoreImpl.create(clientId, DiscordCreateFlags.NoRequireDiscord).successOrNull()
                }
                delay(1.seconds)
            }

            core?.activityManager?.clearActivity { result ->
                println(result)
            }

            core?.close()
        }
    }

    @ExperimentalTime
    @Test
    fun getCurrentUser() {
        runBlocking {
            val core: DiscordCore = DiscordCoreImpl.create(clientId, DiscordCreateFlags.NoRequireDiscord).successOrNull()!!

            delay(5.seconds)
            val result = core.userManager.getCurrentUser()

            println("Result: $result")

            assertThat(result).isInstanceOf(DiscordObjectResult.Success::class)

            core.close()
        }
    }
}
