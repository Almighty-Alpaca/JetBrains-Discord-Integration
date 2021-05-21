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

import gamesdk.api.DiscordObjectResultCallback
import gamesdk.api.DiscordResult
import gamesdk.api.DiscordResultCallback
import gamesdk.impl.types.NativeDiscordImageHandle
import gamesdk.impl.types.NativeDiscordPremiumType
import gamesdk.impl.types.NativeDiscordRelationship
import gamesdk.impl.types.NativeDiscordUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal fun interface NativeCallback<P, R> {
    fun invoke(p: P): R
}

internal typealias NativeDiscordResultCallback = NativeCallback<NativeDiscordResult, Unit>

internal typealias NativeDiscordObjectResultCallback<T> = NativeCallback<NativeDiscordObjectResult<T>, Unit>

internal typealias NativeDiscordImageHandleResultCallback =  NativeDiscordObjectResultCallback<NativeDiscordImageHandle>

internal typealias NativeDiscordUserResultCallback = NativeDiscordObjectResultCallback<NativeDiscordUser>

internal typealias NativeDiscordDiscordPremiumTypeResultCallback = NativeDiscordObjectResultCallback<NativeDiscordPremiumType>

internal typealias NativeDiscordRelationshipFilter = NativeCallback<NativeDiscordRelationship, Boolean>

internal fun DiscordResultCallback.toNativeDiscordResultCallback(): NativeDiscordResultCallback =
    mapToNativeCallback(NativeDiscordResult::toDiscordResult)

internal fun NativeDiscordResultCallback.fromDiscordResultCallback(): DiscordResultCallback =
    mapToCallback(DiscordResult::toNativeDiscordResult)

//internal fun <T> DiscordObjectResultCallback<T>.toNativeDiscordResultObjectCallback(): NativeDiscordObjectResultCallback<T> =
//    toNativeDiscordResultObjectCallback {it}

internal inline fun <T, TN> DiscordObjectResultCallback<T>.toNativeDiscordResultObjectCallback(crossinline converter: (TN) -> T): NativeDiscordObjectResultCallback<TN> =
    mapToNativeCallback { it.toDiscordObjectResult(converter) }

//internal fun <T> NativeDiscordObjectResultCallback<T>.toDiscordResultObjectCallback(): DiscordObjectResultCallback<T> =
//    mapCallback(DiscordObjectResult<T>::toNativeDiscordObjectResult)

internal inline fun <TN, T> NativeDiscordObjectResultCallback<TN>.toDiscordResultObjectCallback(crossinline converter: (T) -> TN): DiscordObjectResultCallback<T> =
    mapToCallback { it.toNativeDiscordObjectResult(converter) }

internal suspend inline fun <T> suspendCallback(crossinline callback: ((T) -> Unit) -> Unit): T =
    suspendCoroutine { continuation ->
        callback { result ->
            continuation.resume(result)
        }
    }

internal inline fun <TFrom, TTo, TResult> (NativeCallback<TTo, TResult>).mapToCallback(crossinline converter: (TFrom) -> TTo): (TFrom) -> TResult =
    { from: TFrom -> invoke(converter(from)) }

internal inline fun <TFrom, TTo, TResult> ((TTo) -> TResult).mapToNativeCallback(crossinline converter: (TFrom) -> TTo): NativeCallback<TFrom, TResult> =
    NativeCallback { from: TFrom -> invoke(converter(from)) }
