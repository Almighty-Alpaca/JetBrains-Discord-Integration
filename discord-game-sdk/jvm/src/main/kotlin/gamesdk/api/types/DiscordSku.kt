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

import com.almightyalpaca.jetbrains.plugins.discord.gamesdk.api.StringLength

public class DiscordSku(
    public val id: DiscordSnowflake,
    public val type: DiscordSkuType,
    public val name: @StringLength(max = 256) String,
    public val price: DiscordSkuPrice
)

public enum class DiscordSkuType { // TODO: native type starts at 1
    Application,
    DLC,
    Consumable,
    Bundle,
}

@OptIn(ExperimentalUnsignedTypes::class)
public typealias DiscordSkuPriceValue = UInt

public class DiscordSkuPrice(
    public val amount: DiscordSkuPriceValue,
    public val currency: @StringLength(max = 16) String
)
