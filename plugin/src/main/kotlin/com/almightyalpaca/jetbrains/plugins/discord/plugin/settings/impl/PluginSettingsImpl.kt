package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.PluginSettings
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "PluginSettings", storages = [Storage("discord.xml")])
class PluginSettingsImpl : SharedSettingsImpl(), PluginSettings {
    override fun getState() = this
    override fun loadState(state: PluginSettings) = XmlSerializerUtil.copyBean(state, this)
}
