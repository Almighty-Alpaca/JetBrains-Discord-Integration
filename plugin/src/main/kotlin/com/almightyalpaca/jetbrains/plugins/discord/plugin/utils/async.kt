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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.openapi.application.ApplicationManager
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun ScheduledExecutorService.scheduleWithFixedDelay(delay: Long, initialDelay: Long = delay, unit: TimeUnit, action: () -> Unit): ScheduledFuture<*> =
    scheduleWithFixedDelay(Runnable(action), initialDelay, delay, unit)

suspend fun <T> invokeOnEventThread(action: () -> T): T {
    val app = ApplicationManager.getApplication()

    return when {
        app.isDispatchThread -> action()
        else -> invokeSuspending(action, app::invokeLater)
    }
}

suspend fun <T> invokeReadAction(action: () -> T): T {
    val app = ApplicationManager.getApplication()

    return invokeSuspending(action, app::runReadAction)
}

suspend fun <T> invokeSuspending(action: () -> T, executor: (Runnable) -> Unit): T =
    suspendCoroutine { continuation ->
        executor(Runnable {
            try {
                continuation.resume(action())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        })
    }
