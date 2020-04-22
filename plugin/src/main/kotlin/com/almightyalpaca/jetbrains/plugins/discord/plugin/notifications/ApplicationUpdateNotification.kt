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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.notification.*

object ApplicationUpdateNotification {
    private val title: (String) -> String = { version -> "Discord Integration updated to $version" }
    private val content = """
        Thank you for using the JetBrains Discord Integration!
        New in this version:${getChangelog()}
        Enjoying this plugin? Having issues? Join our <a href="https://discord.gg/SvuyuMP">Discord</a> server for news and support.
        """.trimIndent()

    private val group = NotificationGroup(
        "${Plugin.getId()}.update",
        NotificationDisplayType.STICKY_BALLOON,
        true
    )

    private fun getChangelog(): String =
        ApplicationUpdateNotification::class.java.getResource("/discord/changes.html").readText()

    fun show(version: String) = group.createNotification(
        title(version),
        content,
        NotificationType.INFORMATION,
        NotificationListener.UrlOpeningListener(false)
    ).run(Notifications.Bus::notify)
}
