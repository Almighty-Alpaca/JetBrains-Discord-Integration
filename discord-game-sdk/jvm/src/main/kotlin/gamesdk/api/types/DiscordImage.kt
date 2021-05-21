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

package gamesdk.api.types

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordImageSize = UInt

public class DiscordImageHandle(
    public val type: DiscordImageType,
    public val id: DiscordUserId,
    public val size: DiscordImageSize
) {
    public companion object {
        @Suppress("FunctionName")
        @OptIn(ExperimentalUnsignedTypes::class)
        public fun User(id: DiscordUserId, size: DiscordImageSize = 128u): DiscordImageHandle =
            DiscordImageHandle(DiscordImageType.User, id, size)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordImageDimension = UInt

public class DiscordImageDimensions(
    public val width: DiscordImageDimension,
    public val height: DiscordImageDimension
)

public enum class DiscordImageType {
    User,
}
