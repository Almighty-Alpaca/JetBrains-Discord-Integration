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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionItem
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items.ApplicationHideAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items.ProjectHideAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.icons.Icons
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup

class ActionComponentImpl : ActionComponent {
    private val manager: ActionManager by lazy(ActionManager::getInstance)
    private lateinit var discord: DefaultActionGroup

    override fun initComponent() {
        discord = DefaultActionGroup("Discord", true).apply {
            templatePresentation.icon = Icons.DISCORD_BLURPLE
        }
        manager.registerAction("${Plugin.pluginId}.Group", discord)

        val tools = manager.getAction("ToolsMenu") as DefaultActionGroup
        tools.add(discord)

        ProjectHideAction().run { create() }
        ApplicationHideAction().run { create() }
    }

    override fun ActionItem.addAction(action: AnAction) {
        val id = this::class.java.simpleName

        manager.registerAction("${Plugin.pluginId}.$id", action, Plugin.pluginId)
        discord.add(action)
    }
}
