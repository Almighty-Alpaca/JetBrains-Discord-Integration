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

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun withAssertionContext(block: Assertion.Context.() -> Unit): Assertion {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val assertion = Assertion()
    assertion.execute(block)

    return assertion
}

@OptIn(ExperimentalContracts::class)
suspend fun withSuspendAssertionContext(block: suspend Assertion.Context.() -> Unit): Assertion {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val assertion = Assertion()
    assertion.execute(block)

    return assertion
}

class Assertion {
    private val context = Context()

    fun result(block: Result.() -> Unit = {}) = Result(context.invocations).apply(block)

    @OptIn(ExperimentalContracts::class)
    fun <T> execute(block: Context.() -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return with(context) { block() }
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun <T> execute(block: suspend Context.() -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return with(context) { block() }
    }

    class Context {
        var invocations = 0
            private set

        fun registerInvocation() {
            invocations++
        }
    }

    class Result(val invocations: Int)
}
