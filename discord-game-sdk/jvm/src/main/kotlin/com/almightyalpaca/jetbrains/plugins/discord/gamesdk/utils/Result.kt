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

package com.almightyalpaca.jetbrains.plugins.discord.gamesdk.utils

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordCore
import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.DiscordCoreImpl

class FailureException constructor(reason: String) : RuntimeException(reason)

sealed class Result<out T, out E>

data class Success<out T>(val value: T) : Result<T, Nothing>()
data class Failure<out E>(val reason: E) : Result<Nothing, E>()

fun <T, E> Result<T, E>.unwrap(): T {
    when (this) {
        is Success<T> -> {
            return this.value
        }
        is Failure<E> -> {
            throw FailureException(this.reason.toString())
        }
    }
}

fun <E> Result<DiscordCore, E>.orInvalidDiscord() = when (this) {
    is Success -> {
        this.value
    }
    is Failure -> {
        DiscordCoreImpl(0)
    }
}
