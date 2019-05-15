package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ApplicationNotificationComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "DiscordApplicationNotificationSettings", storages = [Storage("discord.xml")])
class ApplicationNotificationComponentImpl : ApplicationNotificationComponent {
    override fun initComponent() {}
    override fun disposeComponent() {}
}
