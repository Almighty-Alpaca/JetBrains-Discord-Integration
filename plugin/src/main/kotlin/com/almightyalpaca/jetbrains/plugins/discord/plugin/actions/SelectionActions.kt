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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SelectionValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ProjectShow
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareToggleAction
import com.intellij.openapi.project.Project

class ProjectShowAction : AbstractSelectionAction<ProjectShow>(enumValues(), { it.settings.show })

abstract class AbstractSelectionAction<T>(
    values: Array<T>,
    val currentValue: (Project) -> SelectionValue<T>
) :
    DefaultActionGroup(), DumbAware where T : Enum<T>, T : UiValueType {

    constructor(values: Array<T>, value: SelectionValue<T>) : this(values, { value })

    init {
        isPopup = true

        for (value in values) {
            add(ToggleAction(value, currentValue))
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        val presentation = e.presentation

        val project = e.project
        if (project == null) {
            presentation.isEnabledAndVisible = false
        } else {
            presentation.isEnabledAndVisible = true
            presentation.text = currentValue(project).text
        }
    }

    class ToggleAction<T>(val value: T, val currentValue: (Project) -> SelectionValue<T>) :
        DumbAwareToggleAction(value.text, value.description, null) where T : Enum<T>, T : UiValueType {
        override fun isSelected(e: AnActionEvent) = value == e.project?.let { currentValue(it).getStoredValue() }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if (state) {
                e.project?.let { currentValue(it).setStoredValue(value) }
            }
        }

        override fun update(e: AnActionEvent) {
            super.update(e)

            val presentation = e.presentation

            val project = e.project
            if (project == null) {
                presentation.isEnabledAndVisible = false
            } else {
                val selectionValue = currentValue(project)
                if (value in selectionValue.selectableValues) {
                    presentation.isEnabledAndVisible = true
                } else {
                    presentation.isVisible = value == selectionValue.getStoredValue()
                    presentation.isEnabled = false
                }
            }
        }
    }
}
