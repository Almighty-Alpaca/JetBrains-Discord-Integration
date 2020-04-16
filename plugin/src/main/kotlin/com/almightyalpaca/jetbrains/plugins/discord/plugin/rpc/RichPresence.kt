/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.*
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import java.awt.image.BufferedImage
import java.net.URL
import java.time.OffsetDateTime
import javax.imageio.ImageIO

class RichPresence(
    val appId: Long?,
    state: String? = null,
    details: String? = null,
    var startTimestamp: OffsetDateTime? = null,
    var endTimestamp: OffsetDateTime? = null,
    var largeImage: Image? = null,
    var smallImage: Image? = null,
    partyId: String? = null,
    var partySize: Int = 0,
    var partyMax: Int = 0,
    matchSecret: String? = null,
    joinSecret: String? = null,
    spectateSecret: String? = null,
    var instance: Boolean = false
) {
    constructor(appId: Long?, initializer: RichPresence.() -> Unit) : this(appId) {
        if (appId != null) {
            initializer()
        }
    }

    var state by limitingLength(state, 2..128, true)
    var details by limitingLength(details, 2..128, true)
    var partyId by verifyingLength(partyId, 0..128)
    var matchSecret by verifyingLength(matchSecret, 0..128)
    var joinSecret by verifyingLength(joinSecret, 0..128)
    var spectateSecret by verifyingLength(spectateSecret, 0..128)

    class Image(var asset: Asset?, text: String?) {
        val key = asset?.id?.limit(0..32, false)
        var text by limitingLength(text, 2..128, true)
    }
}

sealed class User {
    abstract val name: String
    abstract val tag: String?
    abstract fun getAvatar(size: Int? = null): BufferedImage?

    class Normal(override val name: String, override val tag: String, val id: Long, val avatarId: String) : User() {
        override fun getAvatar(size: Int?) = when (size) {
            null -> URL("https://cdn.discordapp.com/avatars/$id/$avatarId.png?size=128")
            else -> URL(
                "https://cdn.discordapp.com/avatars/$id/$avatarId.png?size=${size.roundToNextPowerOfTwo()
                    .coerceIn(16..4096)}"
            )
        }.getImage()
    }

    object CLYDE : User() {
        override val name = "Clyde"
        override val tag: String? = null

        // URL: https://discordapp.com/assets/f78426a064bc9dd24847519259bc42af.png
        override fun getAvatar(size: Int?): BufferedImage =
            ImageIO.read(CLYDE::class.java.getResourceAsStream("/discord/images/avatars/clyde.png"))
    }

    companion object
}
