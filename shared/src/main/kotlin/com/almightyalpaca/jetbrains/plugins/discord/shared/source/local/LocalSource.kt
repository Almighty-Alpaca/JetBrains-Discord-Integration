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

package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.*
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.baseName
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.extension
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.retryAsync
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

class LocalSource(location: Path, retry: Boolean = true) : Source, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    internal val path = location.resolve("icons")
    internal val pathLanguages = path.resolve("languages")
    internal val pathThemes = path.resolve("themes")
    internal val pathApplications = path.resolve("applications")

    private val languageJob: Deferred<LanguageMap> = when (retry) {
        true -> retryAsync { readLanguages() }
        false -> async { readLanguages() }
    }

    private val themeJob: Deferred<ThemeMap> = when (retry) {
        true -> retryAsync { readThemes() }
        false -> async { readThemes() }
    }

    override fun getLanguagesAsync(): Deferred<LanguageMap> = languageJob
    override fun getThemesAsync(): Deferred<ThemeMap> = themeJob

    private fun readLanguages(): LanguageMap {
        val mapper = ObjectMapper(YAMLFactory())

        val map = Files.list(pathLanguages)
            .filter { p -> p.extension.toLowerCase() == "yaml" }
            .map { p ->
                val node: JsonNode = mapper.readTree(Files.newInputStream(p))
                LanguageSource(p.baseName.toLowerCase(), node)
            }
            .map { p -> p.id to p }
            .toMap()

        return LocalLanguageSourceMap(this, map).toLanguageMap()
    }

    private fun readThemes(): ThemeMap {
        val mapper = ObjectMapper(YAMLFactory())

        val map = Files.list(pathThemes)
            .filter { p -> p.extension.toLowerCase() == "yaml" }
            .map { p ->
                val node: JsonNode = mapper.readTree(Files.newInputStream(p))
                ThemeSource(p.baseName.toLowerCase(), node)
            }
            .map { p -> p.id to p }
            .toMap()

        return LocalThemeSourceMap(this, map).toThemeMap()
    }
}
