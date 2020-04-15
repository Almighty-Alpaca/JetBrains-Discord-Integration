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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Icons
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class RenderUpdateAction : DumbAwareAction() {
    init {
        templatePresentation.apply {
            icon = Icons.DISCORD_BLURPLE
            text = "Update now"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        renderService.render()
    }
}
