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
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.toMap
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlinx.coroutines.*
import org.apache.commons.io.FilenameUtils
import org.intellij.lang.annotations.RegExp
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import java.io.Closeable
import java.io.InputStream
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

class ClasspathSource(path: String, retry: Boolean = true) : Source, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    val pathBase = "/$path"
    val pathLanguages = "$pathBase/languages"
    val pathThemes = "$pathBase/themes"
    val pathApplications = "$pathBase/applications"

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

        val map = listResources(pathLanguages, Regex(""".*\.yaml"""))
            .map { p ->
                val node: JsonNode = mapper.readTree(loadResource(p))
                LanguageSource(FilenameUtils.getBaseName(p).toLowerCase(), node)
            }
            .map { p -> p.id to p }
            .toMap()

        return ClasspathLanguageSourceMap(this, map).toLanguageMap()
    }

    private fun readThemes(): ThemeMap {
        val mapper = ObjectMapper(YAMLFactory())

        val map = listResources(pathThemes, Regex(""".*\.yaml"""))
            .map { p ->
                val node: JsonNode = mapper.readTree(loadResource(p))
                ThemeSource(FilenameUtils.getBaseName(p).toLowerCase(), node)
            }
            .map { p -> p.id to p }
            .toMap()

        return ClasspathThemeSourceMap(this, map).toThemeMap()
    }

    private fun readApplications(): ApplicationMap {
        val mapper = ObjectMapper(YAMLFactory())

        val map = listResources(pathApplications, Regex(""".*\.yaml"""))
            .map { p ->
                val node: JsonNode = mapper.readTree(loadResource(p))
                ApplicationSource(FilenameUtils.getBaseName(p), node)
            }
            .map { p -> p.id to p }
            .toMap()

        return LocalApplicationSourceMap(map).toApplicationMap()
    }

    fun loadResource(location: String): InputStream? = ClasspathSource::class.java.getResourceAsStream(location)

    fun checkResourceExists(location: String): Boolean = ClasspathSource::class.java.getResourceAsStream(location)?.run(Closeable::close) != null

    fun listResources(path: String, @RegExp pattern: Regex): Stream<String> =
        Reflections(path.substring(1).replace('/', '.'), ResourcesScanner())
            .getResources(pattern.toPattern())
            .stream()
            .map { p -> "/$p" }
}
