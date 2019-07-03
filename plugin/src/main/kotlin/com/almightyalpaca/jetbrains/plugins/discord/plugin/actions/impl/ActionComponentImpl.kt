package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionItem
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items.ApplicationHideAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items.ProjectHideAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.icons.Icons
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.pluginId
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
        manager.registerAction("$pluginId.Group", discord)

        val tools = manager.getAction("ToolsMenu") as DefaultActionGroup
        tools.add(discord)

        ProjectHideAction().run { create() }
        ApplicationHideAction().run { create() }
    }

    override fun ActionItem.addAction(action: AnAction) {
        val id = this::class.java.simpleName

        manager.registerAction("$pluginId.$id", action, pluginId)
        discord.add(action)
    }
}
