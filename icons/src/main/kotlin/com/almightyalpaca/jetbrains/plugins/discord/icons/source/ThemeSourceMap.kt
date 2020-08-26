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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source

import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.stream
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.toMap
import com.fasterxml.jackson.databind.JsonNode

interface ThemeSourceMap : Map<String, ThemeSource> {
    fun createThemeMap(themes: Map<String, Theme>, default: Theme): ThemeMap
    fun createTheme(id: String, name: String, description: String, applications: Map<String, Long>): Theme

    fun toThemeMap(): ThemeMap {
        val themes = stream()
            .filter { (key, _) -> key != "default" }
            .map { (key, value) -> key to value.asTheme() }
            .toMap()

        val defaultTheme = themes.getValue(this.getValue("default").node.textValue())

        return createThemeMap(themes, defaultTheme)
    }

    fun ThemeSource.asTheme(): Theme {
        val name: String = node["name"]?.textValue()!!
        val description: String = node["description"]?.textValue()!!
        val applications: Map<String, Long> = node["applications"].asApplications()

        return createTheme(id, name, description, applications)
    }

    private fun JsonNode.asApplications(): Map<String, Long> = when {
        isNull -> emptyMap()
        isObject -> this.fields().asSequence().associate { (key, value) -> key to value.longValue() }
        else -> throw RuntimeException()
    }
}
