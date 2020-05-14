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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.find

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.stream
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.toSet
import com.almightyalpaca.jetbrains.plugins.discord.uploader.utils.getIcons
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.system.exitProcess

fun main() = runBlocking {
    val source = ClasspathSource("discord", retry = false)
    val languages = source.getLanguages()
    val themes = source.getThemes()

    val assets = languages.stream()
        .flatMap { language -> language.assetIds.stream() }
        .filter(Objects::nonNull)
        .toSet()

    var violation = false

    for (theme in themes.keys) {
        val icons = source.getIcons(theme)

        for (icon in icons) {
            if (icon !in assets) {
                violation = true
                println("$icon in $theme is unused")
            }
        }
    }

    if (violation)
        exitProcess(-1)
}
