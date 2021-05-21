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
import gamesdk.api.DiscordObjectResult
import gamesdk.api.ThreadedCore
import gamesdk.api.events.Subscription
import gamesdk.api.events.subscribe
import gamesdk.api.events.subscribeOnce
import gamesdk.api.types.*
import gamesdk.impl.events.NativeCurrentUserUpdateEvent
import gamesdk.impl.events.NativeNotifiableEventBus
import gamesdk.impl.events.toCurrentUserUpdateEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class Test1 {
    @OptIn(ExperimentalUnsignedTypes::class)
    private val clientId = 768507783167344680U
    private val applicationId = 768507783167344680

    @Test
    @OptIn(ExperimentalTime::class)
    fun testActivity() {
        when (val result = ThreadedCore.create(clientId, createFlags = DiscordCreateFlags.NoRequireDiscord)) {
            is DiscordObjectResult.Failure -> println(result.code)
            is DiscordObjectResult.Success -> result.value.use { core ->
                val activity = DiscordActivity(applicationId = applicationId, state = "Testing \u2665")

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
        when (val result = ThreadedCore.create(clientId, createFlags = DiscordCreateFlags.NoRequireDiscord)) {
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

                                assertThat(result.code).isEqualTo(DiscordCode.Ok)

                                result as DiscordObjectResult.Success<DiscordUser>

                                val user = result.value

                                println(user.username + "#" + user.discriminator)

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
    @OptIn(ExperimentalTime::class, ExperimentalUnsignedTypes::class)
    @Timeout(value = 1500, unit = TimeUnit.SECONDS)
    fun testGetImage() = runBlocking {
        ThreadedCore.create(clientId, createFlags = DiscordCreateFlags.NoRequireDiscord).checkSuccess().value.use { core ->
            val imageManager = core.imageManager

            println("REACHED 1")

            val image = imageManager.getImage(DiscordImageHandle.User(id = 107490111414882304, size = 1024u)).checkSuccess().value

            println("REACHED 2")

            println("size = (${image.width},${image.height})")

            JFrame().apply {
                add(JLabel(ImageIcon(image)))

                pack()
                isAlwaysOnTop = true
                setLocationRelativeTo(null)

                isVisible = true
            }
        }

        delay(30.seconds)
    }

    @Test
    @OptIn(ExperimentalTime::class)
    fun testEvents() {
        println("Start")

        @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
        when (val result = ThreadedCore.create(clientId, DiscordCreateFlags.NoRequireDiscord)) {
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
}
