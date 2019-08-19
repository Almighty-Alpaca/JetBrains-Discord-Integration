/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractThemeSourceMap

class BintrayThemeSourceMap(private val source: BintraySource, map: Map<String, ThemeSource>) : AbstractThemeSourceMap(map) {
    override fun createThemeMap(themes: Map<String, Theme>, default: Theme) = BintrayThemeMap(themes, default)

    override fun createTheme(id: String, name: String, description: String, applications: Map<String, Long>) = BintrayTheme(id, name, description, applications)
}
