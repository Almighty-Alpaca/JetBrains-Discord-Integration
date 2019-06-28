package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ApplicationNotificationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.showUpdateNotification

class ApplicationNotificationComponentImpl : ApplicationNotificationComponent {

    override fun initComponent() {
        showUpdateNotification()
    }

    override fun disposeComponent() {}
}
