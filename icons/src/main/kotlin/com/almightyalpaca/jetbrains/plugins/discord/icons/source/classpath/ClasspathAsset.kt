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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractAsset
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ClasspathAsset(private val source: ClasspathSource, id: String, theme: Theme, private val applicationName: String) : AbstractAsset(id, theme) {
    override fun getImage(size: Int?): BufferedImage? = when (id) {
        "application" -> {
            source.loadResource("${source.pathApplications}/${theme.id}/$applicationName.png")
                ?: source.loadResource("${source.pathApplications}/$applicationName.png")
        }
        else -> source.loadResource("${source.pathThemes}/${theme.id}/$id.png")
    }?.use(ImageIO::read)
}
