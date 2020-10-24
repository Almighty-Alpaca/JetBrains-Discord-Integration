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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.*
import gamesdk.api.DiscordResultCallback
import gamesdk.api.DiscordResultObjectCallback
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal typealias NativePointer = Long

internal typealias NativeDiscordResultCallback = (result: NativeDiscordResult) -> Unit

internal fun DiscordResultCallback.toNativeDiscordResultCallback(): NativeDiscordResultCallback =
    { invoke(it.toDiscordResult()) }

internal fun NativeDiscordResultCallback.fromDiscordResultCallback(): DiscordResultCallback =
    { invoke(it.toNativeDiscordResult()) }

internal typealias NativeDiscordResultObjectCallback<T> = (result: Int, other: T) -> Unit

internal fun <T> DiscordResultObjectCallback<T>.toNativeDiscordResultObjectCallback(): NativeDiscordResultObjectCallback<T> =
    { result, other -> invoke(result.toDiscordResult(), other) }

internal fun <T> NativeDiscordResultObjectCallback<T>.toDiscordResultObjectCallback(): DiscordResultObjectCallback<T> =
    { result, other -> invoke(result.toNativeDiscordResult(), other) }

internal suspend inline fun <T> suspendCallback(crossinline callback: ((T) -> Unit) -> Unit): T = suspendCoroutine { continuation ->
    callback { result ->
        continuation.resume(result)
    }
}

internal typealias NativeDiscordActivityType = Int

internal fun DiscordActivityType.toNativeDiscordActivityType(): NativeDiscordActivityType = this.ordinal

internal fun NativeDiscordActivityType.toDiscordActivityType(): DiscordActivityType =
    when (this) {
        in DiscordActivityType.values().indices -> DiscordActivityType.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordResult = Int

internal fun DiscordResult.toNativeDiscordResult(): NativeDiscordResult = this.ordinal

internal fun NativeDiscordResult.toDiscordResult(): DiscordResult =
    when (this) {
        in DiscordResult.values().indices -> DiscordResult.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordCreateFlags = Int

internal fun DiscordCreateFlags.toNativeDiscordCreateFlags(): NativeDiscordCreateFlags = this.ordinal

internal fun NativeDiscordCreateFlags.toDiscordCreateFlags(): DiscordCreateFlags =
    when (this) {
        in DiscordCreateFlags.values().indices -> DiscordCreateFlags.values()[this]
        else -> throw IllegalArgumentException()
    }

internal typealias NativeDiscordActivityJoinRequestReply = Int

internal fun DiscordActivityJoinRequestReply.toNativeDiscordActivityJoinRequestReply(): NativeDiscordActivityJoinRequestReply = this.ordinal

internal fun NativeDiscordActivityJoinRequestReply.toDiscordActivityJoinRequestReply(): DiscordActivityJoinRequestReply =
    when (this) {
        in DiscordActivityJoinRequestReply.values().indices -> DiscordActivityJoinRequestReply.values()[this]
        else -> throw IllegalArgumentException()
    }

typealias  NativeDiscordActivityActionType = Int

internal fun DiscordActivityActionType.toNativeDiscordActivityActionType(): NativeDiscordActivityActionType = this.ordinal

internal fun NativeDiscordActivityActionType.toDiscordActivityActionType(): DiscordActivityActionType =
    when (this) {
        in DiscordActivityActionType.values().indices -> DiscordActivityActionType.values()[this]
        else -> throw IllegalArgumentException()
    }
