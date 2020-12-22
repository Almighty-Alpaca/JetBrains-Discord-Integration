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

package gamesdk.impl.types

import gamesdk.api.types.*

internal class NativeDiscordImageHandle(
    val type: Int,
    val id: int64_t,
    val size: uint32_t
)

internal fun DiscordImageHandle.toNativeDiscordImageHandle(): NativeDiscordImageHandle = NativeDiscordImageHandle(
    type = this.type.toNativeDiscordImageType(),
    id = this.id,
    size = this.size
)

internal fun NativeDiscordImageHandle.toDiscordImageHandle(): DiscordImageHandle =DiscordImageHandle(
    type = this.type.toDiscordImageType(),
    id = this.id,
    size = this.size
)

