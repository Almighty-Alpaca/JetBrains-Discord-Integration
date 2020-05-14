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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source.local

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractAsset
import java.awt.image.BufferedImage
import java.nio.file.Files
import javax.imageio.ImageIO

class LocalAsset(private val source: LocalSource, id: String, theme: Theme, private val applicationName: String) : AbstractAsset(id, theme) {
    override fun getImage(size: Int?): BufferedImage? = when (id) {
        "application" -> {
            val application = source.pathApplications.resolve("${theme.id}/$applicationName.png")
            if (Files.exists(application)) {
                application
            } else {
                source.pathApplications.resolve("$applicationName.png")
            }
        }
        else -> source.pathThemes.resolve("${theme.id}/$id.png")
    }.let { p -> Files.newInputStream(p) }.use(ImageIO::read)
}
