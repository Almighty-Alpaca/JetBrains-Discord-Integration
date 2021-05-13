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

package gamesdk.api.events

public interface Subscription {
    public suspend fun join()
}

public object CompletedSubscription : Subscription {
    override suspend fun join(): Unit = Unit
}

public interface EventBus<T : Event> {
    public fun subscribeUntil(listener: suspend (event: T) -> Boolean): Subscription

    public fun unsubscribe(subscription: Subscription)
}

public inline fun <T : Event> EventBus<T>.subscribeOnce(crossinline listener: suspend (event: T) -> Unit): Subscription =
    subscribeUntil remove@{
        listener(it)
        return@remove true
    }

public inline fun <T : Event> EventBus<T>.subscribe(times: Int, crossinline listener: suspend (event: T) -> Unit): Subscription =
    if (times <= 0)
        CompletedSubscription
    else {
        var invocationsLeft = times
        subscribeUntil remove@{
            listener(it)
            invocationsLeft--
            return@remove invocationsLeft == 0
        }
    }

public inline fun <T : Event> EventBus<T>.subscribe(crossinline listener: suspend (event: T) -> Unit): Subscription =
    subscribeUntil remove@{
        listener(it)
        return@remove false
    }
