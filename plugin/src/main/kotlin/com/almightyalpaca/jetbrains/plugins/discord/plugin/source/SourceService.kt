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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.source

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.local.LocalSource
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.nio.file.Paths

val sourceService: SourceService
    get() = service()

@Service
class SourceService {
    val source: Source

    init {
        val env = System.getenv("com.almightyalpaca.jetbrains.plugins.discord.plugin.source")?.split(':', limit = 2) ?: listOf("")
        val platform = env[0]
        val location = env.getOrNull(1)

        source = when (platform.lowercase()) {
            "local" -> LocalSource(Paths.get(location ?: throw IllegalStateException("LocalSource needs a path")))
            "classpath" -> ClasspathSource(location ?: "discord")
            else -> ClasspathSource("discord")
        }
    }
}
