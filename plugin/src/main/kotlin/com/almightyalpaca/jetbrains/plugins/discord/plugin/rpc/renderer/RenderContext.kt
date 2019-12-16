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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme

class RenderContext(source: Source, val application: ApplicationData, val mode: Renderer.Mode) {
    val theme: Theme? = source.getThemesOrNull()?.get(settings.theme.getValue())
    val icons: IconSet? = theme?.getIconSet(settings.applicationType.getValue().applicationName)

    val project = application.projects.values
        .filter { p -> p.platform.settings.show.getValue() }
        .maxBy { p -> p.accessedAt }
    val file = project?.files?.values?.maxBy { f -> f.accessedAt }

    val match by lazy { file?.let { source.getLanguagesOrNull()?.findLanguage(file) } }

    fun <T> SimpleValue<T>.getValue(): T = when (mode) {
        Renderer.Mode.NORMAL -> get()
        Renderer.Mode.PREVIEW -> getComponent()
    }

    fun createRenderer(): Renderer = renderType.createRenderer(this)
}
