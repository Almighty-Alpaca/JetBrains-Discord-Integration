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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project

class ProjectConfigurable(val project: Project) : SearchableConfigurable {
    private val settings = project.settings

    override fun getId() = "discord-project"

    override fun isModified(): Boolean = settings.isModified

    override fun getDisplayName() = "Discord Integration Project Settings"

    override fun apply() {
        settings.apply()

        renderService.render()
    }

    override fun reset() {
        settings.reset()
    }

    override fun createComponent() = settings.component

    override fun getHelpTopic(): String? = null
}
