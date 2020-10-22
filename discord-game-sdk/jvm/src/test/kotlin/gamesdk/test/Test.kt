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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.*
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.orInvalidDiscord
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils.unwrap
import gamesdk.api.Core
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Test {
    @Test
    @OptIn(ExperimentalTime::class)
    fun testActivity() {
        Core(768507783167344680, DiscordCreateFlags.NoRequireDiscord).use { core ->
            val activity = DiscordActivity(768507783167344680, state = "Testing...")

            runBlocking {
                val updateResult = core.activityManager.updateActivity(activity)
                println(updateResult)

                delay(10.seconds)

                val clearResult = core.activityManager.clearActivity()
                println(clearResult)

                delay(5.seconds)
            }
        }
    }

    @Test
    @OptIn(ExperimentalTime::class, ExperimentalUnsignedTypes::class)
    fun testActivity2() {
        var core = DiscordCoreImpl.create(310270644849737729UL, DiscordCreateFlags.NoRequireDiscord).orInvalidDiscord()

//        core.setLogHook(DiscordLogLevel.Debug) {
//            level, message ->
//            println("Discord($level): $message")
//        }

        lateinit var activityManager: DiscordActivityManager

        var discordRunning = core.isValid()
        if (discordRunning) {
            activityManager = core.getActivityManager()
        }

        runBlocking {
            for (i in 0..180) {
                if (discordRunning) {
                    println("Running")
                    if (i % 15 == 0) {
                        val activity = DiscordActivity(310270644849737729, state = "Waiting", details = "...")

                        activityManager.updateActivity(activity) { result ->
                            println(result)
                        }
                    }

                    val result = core.runCallbacks()
                    println("Callback result: $result")

                    if (result == DiscordResult.NotRunning) {
                        discordRunning = false
                        println("Discord is not running anymore")
                        core.destroy()
                    }
                } else {
                    println("Trying to reconnect")
                    core = DiscordCoreImpl.create(310270644849737729UL, DiscordCreateFlags.NoRequireDiscord).orInvalidDiscord()
                    discordRunning = core.isValid()
                    if (discordRunning) {
                        activityManager = core.getActivityManager()
                    }
                }
                delay(1.seconds)
            }
            activityManager.clearActivity { result ->
                println(result)
            }

            core.destroy()
        }
    }
}
