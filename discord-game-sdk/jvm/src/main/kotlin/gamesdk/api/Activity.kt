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

import gamesdk.impl.ActivityImpl

typealias ApplicationId = Long

interface Activity {
    var type: Type
    var applicationId: ApplicationId
    var name: String
    var state: String

    // TODO: Implement other fields and types

    companion object {
        operator fun invoke(): Activity = ActivityImpl()
    }

    @Suppress("unused")
    enum class Type {
        Playing,
        Streaming,
        Listening,
        Watching;

        internal fun toNative() = this.ordinal
    }
}

internal fun Int.toActivityType() = when (this) {
    in Activity.Type.values().indices -> Activity.Type.values()[this]
    else -> throw IllegalArgumentException()
}
