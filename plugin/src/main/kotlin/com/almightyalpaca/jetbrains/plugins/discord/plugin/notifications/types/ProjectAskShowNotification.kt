package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.CHANNEL
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.impl.NotificationActionProvider
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

class AskShowNotification(val callback: AskShowNotification.(Boolean) -> Unit) : NotificationActionProvider, Notification(
    CHANNEL,
    "Show project in Rich Presence?",
    "You have selected to be asked for every project whether or not the show the project in Discord.",
    NotificationType.INFORMATION
) {
    init {
        setListener(object : NotificationListener.Adapter() {
            override fun hyperlinkActivated(notification: Notification, e: HyperlinkEvent) {
                when (e.description) {
                    "show" -> callback(true)
                    "hide" -> callback(false)
                    else -> return
                }

                expire()
            }
        })
    }

    override fun getActions(listener: HyperlinkListener): Array<NotificationActionProvider.Action> {
        return arrayOf(
            NotificationActionProvider.Action(listener, "show", "Show"),
            NotificationActionProvider.Action(listener, "hide", "Hide")
        )
    }
}
