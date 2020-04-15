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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.toggle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import javax.swing.Icon

abstract class AbstractToggleAction(
    private val value: (Project) -> BooleanValue,
    private val enabled: View,
    private val disabled: View
) : DumbAwareAction() {
    constructor(value: BooleanValue, enabled: View, disabled: View) : this({ value }, enabled, disabled)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return

        value(project).toggle()
        renderService.render()
    }

    override fun update(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT) ?: return

        if (value(project).get()) {
            enabled.apply(e.presentation)
        } else {
            disabled.apply(e.presentation)
        }
    }

    class View(
        var text: String = "",
        var description: String = "",
        var icon: Icon? = null,
        var hoveredIcon: Icon? = null
    ) {
        constructor(block: View.() -> Unit) : this() {
            apply(block)
        }

        fun apply(presentation: Presentation) {
            presentation.text = text
            presentation.description = description
            presentation.icon = icon
            // presentation.hoveredIcon = hoveredIcon
            presentation.selectedIcon = hoveredIcon // For some reason this is actually the hover icon ¯\_(ツ)_/¯
        }
    }
}
