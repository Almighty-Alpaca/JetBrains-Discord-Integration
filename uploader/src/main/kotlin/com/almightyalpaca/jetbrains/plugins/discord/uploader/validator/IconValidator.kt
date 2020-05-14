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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.validator

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import com.almightyalpaca.jetbrains.plugins.discord.uploader.utils.getIcons
import kotlinx.coroutines.runBlocking
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() = runBlocking {
    val source = ClasspathSource("discord", retry = false)
    val themes = source.getThemes()

    var violation = false

    for (theme in themes.keys) {
        val icons = source.getIcons(theme)

        violation = source.validate(theme, icons)
    }

    if (violation)
        exitProcess(-1)
}

private fun ClasspathSource.validate(theme: String, icons: Set<String>): Boolean {
    var violation = false

    // 149 because the application icon isn't in the list making it 150 in total
    if (icons.size > 149)
        println("Theme $theme has too many icons: ${icons.size + 1}")

    for (icon in icons) {
        val path = "$pathThemes/$theme/$icon.png"

        val image = ImageIO.read(loadResource(path))

        val width = image.width
        val height = image.height

        if (width != 1024 || height != 1024) {
            violation = true

            println("Icon $theme/$icon.png has wrong dimensions: ${width}x$height")
        }
    }

    return violation
}
