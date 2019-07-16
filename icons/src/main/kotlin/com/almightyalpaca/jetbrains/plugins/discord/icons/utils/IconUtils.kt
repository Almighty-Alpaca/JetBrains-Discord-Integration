/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.icons.utils

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import java.nio.file.Files
import java.nio.file.Paths

fun getLocalIcons(theme: String): Set<String> {
    val path = Paths.get("themes/$theme")

    return Files.list(path)
        .filter { p -> Files.isRegularFile(p) }
        .map { p -> p.name }
        .filter { p -> p.endsWith(".png") }
        .map { p -> p.substring(0, p.length - 4) }
        .toSet()
}
