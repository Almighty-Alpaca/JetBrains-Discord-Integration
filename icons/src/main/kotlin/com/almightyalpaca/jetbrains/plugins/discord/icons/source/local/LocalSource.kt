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

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.*
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.baseName
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.extension
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.retryAsync
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.toMap
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

    val pathBase: Path = location.resolve("icons")
    val pathLanguages: Path = pathBase.resolve("languages")
    val pathThemes: Path = pathBase.resolve("themes")
    val pathApplications: Path = pathBase.resolve("applications")

    private val languageJob: Deferred<LanguageMap> = when (retry) {
        true -> retryAsync { readLanguages() }
        false -> async { readLanguages() }
    }

    private val themeJob: Deferred<ThemeMap> = when (retry) {
        true -> retryAsync { readThemes() }
        false -> async { readThemes() }
    }

    private val applicationJob: Deferred<ApplicationMap> = when (retry) {
        true -> retryAsync { readApplications() }
        false -> async { readApplications() }
    }

    override fun getLanguagesAsync(): Deferred<LanguageMap> = languageJob
    override fun getThemesAsync(): Deferred<ThemeMap> = themeJob
    override fun getApplicationsAsync(): Deferred<ApplicationMap> = applicationJob

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

    private fun readApplications(): ApplicationMap {
        val mapper = ObjectMapper(YAMLFactory())

        val map = Files.list(pathApplications)
            .filter { p -> p.extension.toLowerCase() == "yaml" }
            .map { p ->
                val node: JsonNode = mapper.readTree(Files.newInputStream(p))
                ApplicationSource(p.baseName, node)
            }
            .map { p -> p.id to p }
            .toMap()

        return LocalApplicationSourceMap(map).toApplicationMap()
    }
}
