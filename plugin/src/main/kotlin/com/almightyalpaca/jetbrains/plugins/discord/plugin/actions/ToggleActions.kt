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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareToggleAction
import com.intellij.openapi.project.Project

class ApplicationShowAction : AbstractToggleAction(settings.show)

abstract class AbstractToggleAction(private val currentValue: (Project) -> BooleanValue, text: String, description: String?) :
    DumbAwareToggleAction(text, description, null) {
    constructor(currentValue: BooleanValue, text: String, description: String?) : this({ currentValue }, text, description)
    constructor(currentValue: BooleanValue) : this(currentValue, currentValue.text, currentValue.description)

    override fun isSelected(e: AnActionEvent) = e.project?.let { currentValue(it).getStoredValue() } == true

    override fun setSelected(e: AnActionEvent, state: Boolean) = e.project?.let { currentValue(it).setStoredValue(state) } ?: Unit
}
