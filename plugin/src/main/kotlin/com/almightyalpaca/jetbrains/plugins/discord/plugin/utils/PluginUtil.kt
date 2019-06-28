package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId

val pluginId by lazy { PluginId.getId("com.almightyalpaca.jetbrains.plugins.discord") }
val plugin by lazy { PluginManager.getPlugin(pluginId)!! }
