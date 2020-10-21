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
import com.intellij.openapi.application.runReadAction
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun ScheduledExecutorService.scheduleWithFixedDelay(delay: Long, initialDelay: Long = delay, unit: TimeUnit, command: () -> Unit): ScheduledFuture<*> =
    scheduleWithFixedDelay(Runnable(command), initialDelay, delay, unit)

suspend fun <T> invokeOnEventThread(runnable: () -> T): T = when {
    ApplicationManager.getApplication()?.isDispatchThread != false && SwingUtilities.isEventDispatchThread() -> runnable()
    else -> suspendCoroutine { continuation ->
        SwingUtilities.invokeLater {
            try {
                continuation.resume(runnable())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}

suspend fun <T> invokeReadAction(runnable: () -> T): T = suspendCoroutine { continuation ->
    runReadAction {
        try {
            continuation.resume(runnable())
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}
