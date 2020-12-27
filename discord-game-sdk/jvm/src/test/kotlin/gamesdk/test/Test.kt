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
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.DiscordCore
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.Failure
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.Success
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.impl.DiscordCoreImpl
import gamesdk.api.*
import gamesdk.api.events.Subscription
import gamesdk.api.events.subscribe
import gamesdk.api.events.subscribeOnce
import gamesdk.api.types.DiscordActivity
import gamesdk.api.types.DiscordCode
import gamesdk.api.types.DiscordCreateFlags
import gamesdk.impl.events.NativeCurrentUserUpdateEvent
import gamesdk.impl.events.NativeNotifiableEventBus
import gamesdk.impl.events.toCurrentUserUpdateEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Test {
    @Test
    @OptIn(ExperimentalTime::class)
    fun testActivity() {
        @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
        when (val result = ThreadedCore.create(clientId = 768507783167344680U, createFlags = DiscordCreateFlags.NoRequireDiscord)) {
            is DiscordObjectResult.Failure -> println(result.code)
            is DiscordObjectResult.Success -> result.value.use { core ->
                val activity = DiscordActivity(applicationId = 768507783167344680, state = "Testing...")

                runBlocking {
                    val updateResult = core.activityManager.updateActivity(activity)
                    println(updateResult.code)

                    delay(10.seconds)

                    val clearResult = core.activityManager.clearActivity()
                    println(clearResult.code)

                    delay(5.seconds)
                }
            }
        }
        println("Done")
    }

    @Test
    @OptIn(ExperimentalTime::class)
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    fun testGetCurrentUser() {
        @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
        when (val result = ThreadedCore.create(clientId = 768507783167344680U, createFlags = DiscordCreateFlags.NoRequireDiscord)) {
            is DiscordObjectResult.Failure ->
                println("ERROR: ${result.code}")
            is DiscordObjectResult.Success -> result.value.use { core ->
                val userManager = core.userManager

                runBlocking {
                    withSuspendAssertionContext {
                        val subscription = userManager.currentUserUpdates.subscribeOnce {
                            try {
                                val result = userManager.getCurrentUser()

                                assertThat(result).isInstanceOf(DiscordObjectResult.Success::class)

                                registerInvocation()
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }

                        userManager.getCurrentUser()

                        subscription.join()
                    }.result { assertThat(::invocations).isEqualTo(1) }
                }
            }
        }
    }

    @Test
    @OptIn(ExperimentalTime::class)
    fun testEvents() {
        println("Start")

        @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
        when (val result = ThreadedCore.create(768507783167344680U, DiscordCreateFlags.NoRequireDiscord)) {
            is DiscordObjectResult.Failure -> println(result.code)
            is DiscordObjectResult.Success -> result.value.use { core ->
                runBlocking {
                    core.userManager.currentUserUpdates.subscribe {
                        println("User Update!")
                    }

                    core.relationshipManager.refreshes.subscribe {
                        println("Relationship Refresh!")
                    }

                    core.relationshipManager.relationshipUpdates.subscribe {
                        println("Relationship Update!")
                    }

                    delay(30.seconds)
                }
            }
        }

        println("Done")
    }

    @Test
    @OptIn(ExperimentalTime::class)
    fun testEventBus() {
        val eventBus = NativeNotifiableEventBus.create(NativeCurrentUserUpdateEvent::toCurrentUserUpdateEvent)

        val assertion1 = withAssertionContext {
            eventBus.subscribeOnce { registerInvocation() }
        }

        val assertion2 = withAssertionContext {
            var i = 2
            eventBus.subscribeUntil {
                registerInvocation()
                return@subscribeUntil --i == 0
            }
        }

        val subscription3: Subscription
        val assertion3 = withAssertionContext {
            subscription3 = eventBus.subscribe { registerInvocation() }
        }

        val assertion4 = withAssertionContext {
            eventBus.subscribe { registerInvocation() }
        }

        eventBus.notify(NativeCurrentUserUpdateEvent())
        eventBus.notify(NativeCurrentUserUpdateEvent())
        eventBus.notify(NativeCurrentUserUpdateEvent())
        eventBus.unsubscribe(subscription3)
        eventBus.notify(NativeCurrentUserUpdateEvent())

        assertion1.result { assertThat(::invocations).isEqualTo(1) }
        assertion2.result { assertThat(::invocations).isEqualTo(2) }
        assertion3.result { assertThat(::invocations).isEqualTo(3) }
        assertion4.result { assertThat(::invocations).isEqualTo(4) }
    }

    @Test
    @OptIn(ExperimentalTime::class, ExperimentalUnsignedTypes::class)
    fun testActivity2() {
        runBlocking {
            var core: DiscordCore? = when (val result = DiscordCoreImpl.create(310270644849737729UL, DiscordCreateFlags.NoRequireDiscord)) {
                is Success -> result.value
                is Failure -> {
                    println("Error " + result.reason)
                    null
                }
            }

            // core.setLogHook(DiscordLogLevel.Debug) { level, message ->
            //     println("Discord($level): $message")
            // }

            for (i in 0..180) {
                if (core != null) {
                    println("Running")
                    if (i % 15 == 0) {
                        val activity = DiscordActivity(310270644849737729, state = "Waiting", details = "...")

                        core.getActivityManager().updateActivity(activity) { result ->
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

                    core = when (val result = DiscordCoreImpl.create(310270644849737729UL, DiscordCreateFlags.NoRequireDiscord)) {
                        is Success -> result.value
                        is Failure -> {
                            println("Error " + result.reason)
                            null
                        }
                    }
                }
                delay(1.seconds)
            }

            core?.getActivityManager()?.clearActivity { result ->
                println(result)
            }

            core?.close()
        }
    }
}
