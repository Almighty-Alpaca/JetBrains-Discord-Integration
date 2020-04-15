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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.Data
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source

class RenderContext(source: Source, val data: Data, val mode: Renderer.Mode) {
    val icons: IconSet? by lazy {
        source.getThemesOrNull()
            ?.get(settings.theme.getValue())
            ?.getIconSet(settings.applicationType.getValue().applicationName)
    }

    val applicationData = data as? Data.Application
    val projectData = data as? Data.Project
    val fileData = data as? Data.File

    val language by lazy { fileData?.let { source.getLanguagesOrNull()?.findLanguage(fileData) } }

    fun <T> SimpleValue<T>.getValue(): T = when (mode) {
        Renderer.Mode.NORMAL -> get()
        Renderer.Mode.PREVIEW -> getComponent()
    }

    fun createRenderer(): Renderer = renderType.createRenderer(this)
}
