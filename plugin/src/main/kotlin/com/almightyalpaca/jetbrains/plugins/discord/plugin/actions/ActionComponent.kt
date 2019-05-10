package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.components.ApplicationComponent

interface ActionComponent : ApplicationComponent {
    fun ActionItem.addAction(action: AnAction)
}
