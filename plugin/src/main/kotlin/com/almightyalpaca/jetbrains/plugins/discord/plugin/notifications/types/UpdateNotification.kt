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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.plugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.pluginId
import com.intellij.notification.*

object UpdateNotification {
    private val title = "Discord Integration updated to ${plugin.version}"
    private val content = """
        Thanks you for using the JetBrains Discord Integration!
        New in this version:${getChangelog()}
        Enjoying this plugin? Having issues? Join our <a href="https://discord.gg/SvuyuMP">Discord</a> server for news and support.
        """.trimIndent()

    private val group = NotificationGroup(
            pluginId.idString + ".update",
            NotificationDisplayType.STICKY_BALLOON,
            true
    )

    private fun getChangelog(): String = ApplicationComponent::class.java.getResource("/discord/changes.html").readText()

    fun show() {
        val notification = group.createNotification(title, content, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER)
        Notifications.Bus.notify(notification)
    }
}