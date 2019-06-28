package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.plugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.pluginId
import com.intellij.notification.*

private val title = "Discord Integration updated to ${plugin.version}"
private val content = """
    Thanks you for using the JetBrains Discord Integration!
    New in this version:${getChangelog()}
    Enjoying this plugin? Having issues? Join our <a href="https://discord.gg/SvuyuMP">Discord</a> server for news and support.
""".trimIndent()

fun getChangelog(): String = ApplicationComponent::class.java.getResource("/discord/changes.html").readText()

fun showUpdateNotification() {
    if (plugin.version != notificationSettings.lastUpdateNotification) {
        notificationSettings.lastUpdateNotification = plugin.version

        val group = NotificationGroup(
            pluginId.idString + ".update",
            NotificationDisplayType.STICKY_BALLOON,
            true
        )

        val notification = group.createNotification(title, content, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER)
        Notifications.Bus.notify(notification)
    }
}
