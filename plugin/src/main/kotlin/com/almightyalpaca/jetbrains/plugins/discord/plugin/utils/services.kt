/*
 * Copyright 2017-2019 Aljoscha Grebe
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
import com.intellij.openapi.project.Project
import org.jetbrains.concurrency.Promise
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Promise<T>.toSuspendFunction(): T? = suspendCoroutine { continuation ->
    onError(continuation::resumeWithException)
    onProcessed(continuation::resume)
}

inline fun <reified T> Project.service() = service(T::class.java)

fun <T> Project.service(clazz: Class<T>): T {
//    println("GETTING PROJECT SERVICE: $clazz")

    val service: T = this.getService(clazz)

    if (service == null) {
        println("COULDN'T FIND PROJECT SERVICE $clazz")
        NullPointerException().printStackTrace()
    } else {
//        println("FOUND PROJECT SERVICE: $clazz")
    }

    return service
}

inline fun <reified T> service(): T = service(T::class.java)

fun <T> service(clazz: Class<T>): T {
//    println("GETTING SERVICE: $clazz")

    val service: T = ApplicationManager.getApplication().getService(clazz)

    if (service == null) {
        println("COULDN'T FIND SERVICE $clazz")
        NullPointerException().printStackTrace()
    } else {
//        println("FOUND SERVICE $service")
    }

    return service
}
