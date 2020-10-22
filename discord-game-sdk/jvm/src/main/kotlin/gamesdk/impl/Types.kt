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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordActivityType
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordCreateFlags
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordResult
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordRelationship
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal typealias Pointer = Long

internal typealias DiscordResultCallback = (result:DiscordResult) -> Unit
internal typealias NativeDiscordResultCallback = (result:Int) -> Unit

internal fun DiscordResultCallback.toNative(): NativeDiscordResultCallback = { invoke(it.toDiscordResult()) }

internal fun NativeDiscordResultCallback.fromNative(): DiscordResultCallback = { invoke(it.toNative()) }

internal typealias DiscordResultObjectCallback<T> = (result:DiscordResult, other:T) -> Unit
internal typealias NativeDiscordResultObjectCallback<T> = (result:Int, other:T) -> Unit

internal fun <T> DiscordResultObjectCallback<T>.toNative(): NativeDiscordResultObjectCallback<T> = { result, other -> invoke(result.toDiscordResult(), other) }

internal fun <T>NativeDiscordResultObjectCallback<T>.fromNative(): DiscordResultObjectCallback<T> = { result, other -> invoke(result.toNative(),other) }


typealias DiscordRelationshipCallback = (relationship: DiscordRelationship) -> Boolean

internal suspend inline fun <T> suspendCallback(crossinline callback: ((T) -> Unit) -> Unit): T = suspendCoroutine { continuation ->
    callback { result ->
        continuation.resume(result)
    }
}

internal fun DiscordActivityType.toNative() = this.ordinal

internal fun Int.toDiscordActivityType() = when (this) {
    in DiscordActivityType.values().indices -> DiscordActivityType.values()[this]
    else -> throw IllegalArgumentException()
}

internal fun DiscordResult.toNative() = this.ordinal

internal fun Int.toDiscordResult() = when (this) {
    in DiscordResult.values().indices -> DiscordResult.values()[this]
    else -> throw IllegalArgumentException()
}

internal fun DiscordCreateFlags.toNative() = this.ordinal

internal fun Int.toDiscordCreateFlags() = when (this) {
    in DiscordCreateFlags.values().indices -> DiscordCreateFlags.values()[this]
    else -> throw IllegalArgumentException()
}
