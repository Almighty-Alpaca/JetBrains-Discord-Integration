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

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isLessThanOrEqualTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun withCallbackContext(block: CallbackAssertionContext.() -> Unit): CallbackAssertionResult {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return CallbackAssertionContext().apply(block).getResult()
}

class CallbackAssertionContext {
    var invocations = 0
        private set

    fun registerInvocation() {
        invocations++
    }

    fun getResult(): CallbackAssertionResult = CallbackAssertionResult(this)

}

class CallbackAssertionResult(private val context: CallbackAssertionContext) {
    fun assertMaxInvocations(maxInvocations: Int) {
        assertThat(context::invocations).isLessThanOrEqualTo(maxInvocations)
    }

    fun assertInvocations(exactInvocations: Int) {
        assertThat(context::invocations).isEqualTo(exactInvocations)
    }

    fun assertMinInvocations(minInvocations: Int) {
        assertThat(context::invocations).isGreaterThanOrEqualTo(minInvocations)
    }
}
