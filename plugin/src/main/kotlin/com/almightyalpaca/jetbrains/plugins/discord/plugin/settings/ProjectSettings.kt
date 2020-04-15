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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.intellij.openapi.components.PersistentStateComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.service
import com.intellij.openapi.project.Project
import org.jdom.Element

val Project.settings: ProjectSettings
get() = service()

interface ProjectSettings : PersistentStateComponent<Element>, OptionHolder {
    val project: Project

    val show: BooleanValue

    val nameOverrideEnabled: BooleanValue
    val nameOverrideText: StringValue

    val description: StringValue
}
