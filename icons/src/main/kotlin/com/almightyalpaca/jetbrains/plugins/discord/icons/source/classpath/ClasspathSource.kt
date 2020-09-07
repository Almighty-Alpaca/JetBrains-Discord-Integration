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

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.*
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.local.LocalApplicationSourceMap
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.retryAsync
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.*
import org.apache.commons.io.FilenameUtils
import java.io.Closeable
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

class ClasspathSource(path: String, retry: Boolean = true) : Source, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    val basePath = "/$path"
    val pathLanguages = "$basePath/languages"
    val pathThemes = "$basePath/themes"
    val pathApplications = "$basePath/applications"

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

    private fun readLanguages() = ClasspathLanguageSourceMap(this, read(pathLanguages, ::LanguageSource)).toLanguageMap()
    private fun readThemes() = ClasspathThemeSourceMap(this, read(pathThemes, ::ThemeSource)).toThemeMap()
    private fun readApplications() = LocalApplicationSourceMap(read(pathApplications, ::ApplicationSource)).toApplicationMap()

    private fun <T> read(path: String, factory: (String, JsonNode) -> T): Map<String, T> {
        val mapper = ObjectMapper(YAMLFactory())

        return listResources(path, ".yaml")
            .map { p ->
                try {
                    val node: JsonNode = mapper.readTree(loadResource(p))
                    val id = FilenameUtils.getBaseName(p)
                    id to factory(id, node)
                } catch (e: Exception) {
                    throw IllegalStateException("Error while generating $p")
                }
            }
            .toMap()
    }

    fun loadResource(location: String): InputStream? = ClasspathSource::class.java.getResourceAsStream(location)

    fun checkResourceExists(location: String): Boolean = loadResource(location)?.run(Closeable::close) != null

    fun listResources(path: String, extension: String): Sequence<String> {
        return (loadResource("$path/index")
            ?.bufferedReader()
            ?.lineSequence()
            ?: throw IllegalStateException("could not find index for $path"))
            .filter { it.endsWith(extension) }
            .map { p -> "$path/$p" }
    }
}
