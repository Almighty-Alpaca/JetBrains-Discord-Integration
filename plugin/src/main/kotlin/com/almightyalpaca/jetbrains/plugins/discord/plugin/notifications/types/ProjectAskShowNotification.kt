/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
