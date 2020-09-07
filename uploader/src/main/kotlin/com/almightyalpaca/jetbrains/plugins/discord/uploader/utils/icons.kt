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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.utils

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.local.LocalSource
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.toSet
import org.apache.commons.io.FilenameUtils
import java.nio.file.Files

fun LocalSource.getIcons(theme: String): Set<String> {
    return Files.list(pathThemes.resolve(theme))
        .filter { p -> Files.isRegularFile(p) }
        .map { p -> p.name }
        .filter { p -> p.endsWith(".png") }
        .map { p -> p.substring(0, p.length - 4) }
        .toSet()
}

fun ClasspathSource.getIcons(theme: String): Set<String> {
    return listResources("$pathThemes/$theme", ".png")
        .map { p -> FilenameUtils.getBaseName(p) }
        .toSet()
}
