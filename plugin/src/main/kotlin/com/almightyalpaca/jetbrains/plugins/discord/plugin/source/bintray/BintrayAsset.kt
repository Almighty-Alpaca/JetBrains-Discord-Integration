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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractAsset
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getCompletedOrNull
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.roundToNextPowerOfTwo
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

class BintrayAsset(id: String, private val iconSet: BintrayIconSet) : AbstractAsset(id, iconSet.theme) {
    override fun getImage(size: Int?): BufferedImage? = getIconURL(size)?.get { stream ->
        ImageIO.read(stream)
    }

    private fun getIconURL(size: Int?): URL? {
        val applicationId = iconSet.applicationId
        val iconId = iconSet.iconIdJob.getCompletedOrNull()?.get(id)

        return if (applicationId != null && iconId != null) {
            when (size) {
                null -> URL("https://cdn.discordapp.com/app-assets/$applicationId/$iconId.png")
                else -> URL("https://cdn.discordapp.com/app-assets/$applicationId/$iconId.png?size=${size.roundToNextPowerOfTwo().coerceIn(16..4096)}")
            }
        } else {
            null
        }
    }
}
