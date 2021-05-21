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

package gamesdk.api

import gamesdk.api.managers.DiscordRelationshipListSize
import gamesdk.api.types.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public sealed class DiscordResult {
    public abstract val code: DiscordCode

    @OptIn(ExperimentalContracts::class)
    public fun checkSuccess(): Success {
        contract {
            returns() implies (this@DiscordResult is Success)
        }

        when (this) {
            is Failure -> throw DiscordException(code)
            is Success -> return this
        }
    }

    public object Success : DiscordResult() {
        override val code: DiscordCode = DiscordCode.Ok
    }

    public data class Failure(override val code: DiscordCode.Failure) : DiscordResult()
}

public sealed class DiscordObjectResult<out T> {
    public abstract val code: DiscordCode

    @OptIn(ExperimentalContracts::class)
    public fun checkSuccess(): Success<T> {
        contract {
            returns() implies (this@DiscordObjectResult is Success<T>)
        }

        when (this) {
            is Failure -> throw DiscordException(code)
            is Success<T> -> return this
        }
    }

    public data class Success<out T>(val value: T) : DiscordObjectResult<T>() {
        override val code: DiscordCode = DiscordCode.Ok
    }

    public data class Failure(override val code: DiscordCode.Failure) : DiscordObjectResult<Nothing>()
}

public class DiscordException(public val code: DiscordCode.Failure) : Exception("Discord returned error code $code")

public inline fun <T, R> DiscordObjectResult<T>.map(block: (T) -> R): DiscordObjectResult<R> =
    flatMap { DiscordObjectResult.Success(block(it)) }

public inline fun <T, R> DiscordObjectResult<T>.flatMap(block: (T) -> DiscordObjectResult<R>): DiscordObjectResult<R> = when (this) {
    is DiscordObjectResult.Success -> block(value)
    is DiscordObjectResult.Failure -> this
}

//sealed class DiscordObjectObjectResult<out T1, out T2>(val code: DiscordCode) {
//    data class Success<out T1, out T2>(val first: T1, val second: T2) : DiscordObjectObjectResult<T1, T2>(DiscordCode.Ok)
//    data class Failure(val reason: DiscordCode.Failure) : DiscordObjectObjectResult<Nothing, Nothing>(reason)
//}

public typealias DiscordBooleanResult = DiscordObjectResult<Boolean>

public typealias DiscordCoreResult = DiscordObjectResult<Core>

public typealias DiscordImageDimensionsResult = DiscordObjectResult<DiscordImageDimensions>

public typealias DiscordImageHandleResult = DiscordObjectResult<DiscordImageHandle>

public typealias DiscordPremiumTypeResult = DiscordObjectResult<DiscordPremiumType>

public typealias DiscordRelationshipListSizeResult = DiscordObjectResult<DiscordRelationshipListSize>

public typealias DiscordRelationshipResult = DiscordObjectResult<DiscordRelationship>

public typealias DiscordUserResult = DiscordObjectResult<DiscordUser>
