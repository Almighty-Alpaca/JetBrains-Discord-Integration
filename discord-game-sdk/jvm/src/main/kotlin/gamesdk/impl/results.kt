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

import gamesdk.api.DiscordObjectResult
import gamesdk.api.DiscordResult
import gamesdk.api.types.DiscordCode
import gamesdk.impl.types.*

internal typealias NativeDiscordResult = NativeDiscordCode

internal sealed class NativeDiscordObjectResult<out TResult> {
    internal data class Success<out TResult>(val value: TResult) : NativeDiscordObjectResult<TResult>()
    internal data class Failure(val reason: NativeDiscordCode) : NativeDiscordObjectResult<Nothing>()
}

internal typealias NativeDiscordImageDimensionsResult = NativeDiscordObjectResult<NativeDiscordImageDimensions>

internal typealias NativeDiscordUserResult = NativeDiscordObjectResult<NativeDiscordUser>

internal typealias NativeDiscordPremiumTypeResult = NativeDiscordObjectResult<NativeDiscordPremiumType>

internal typealias NativeDiscordBooleanResult = NativeDiscordObjectResult<Boolean>

internal fun DiscordResult.toNativeDiscordResult(): NativeDiscordResult {
    return when (this) {
        is DiscordResult.Success -> DiscordCode.Ok.toNativeDiscordCode()
        is DiscordResult.Failure -> code.toNativeDiscordCode()
    }
}

internal fun NativeDiscordResult.toDiscordResult(): DiscordResult {
    return when (this) {
        DiscordCode.Ok.toNativeDiscordCode() -> DiscordResult.Success
        else -> DiscordResult.Failure(toDiscordFailureCode())
    }
}

//internal fun <T> DiscordObjectResult<T>.toNativeDiscordObjectResult(): NativeDiscordObjectResult<T> =
//    toNativeDiscordObjectResult { it }

internal inline fun <TN, T> DiscordObjectResult<T>.toNativeDiscordObjectResult(converter: (T) -> TN): NativeDiscordObjectResult<TN> {
    return when (this) {
        is DiscordObjectResult.Success<T> -> NativeDiscordObjectResult.Success(converter(value))
        is DiscordObjectResult.Failure -> NativeDiscordObjectResult.Failure(code.toNativeDiscordCode())
    }
}

internal fun <T> NativeDiscordObjectResult<T>.toDiscordObjectResult(): DiscordObjectResult<T> =
    toDiscordObjectResult { it }

internal inline fun <TN, T> NativeDiscordObjectResult<TN>.toDiscordObjectResult(converter: (TN) -> T): DiscordObjectResult<T> {
    return when (this) {
        is NativeDiscordObjectResult.Success -> DiscordObjectResult.Success(converter(value))
        is NativeDiscordObjectResult.Failure -> DiscordObjectResult.Failure(reason.toDiscordFailureCode())
    }
}
