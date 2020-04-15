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
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val title = "Show project in Rich Presence?"
private const val content =
    "Select if this project should be visible. You can change this later at any time under Settings > Tools > Discord > Project"

private val group = NotificationGroup(
    Plugin.getIdString() + ".project.show",
    NotificationDisplayType.STICKY_BALLOON,
    true
)

object AskShowProjectNotification {
    suspend fun show(project: Project) = suspendCoroutine<Boolean> { continuation ->
        group.createNotification(title, null, content, NotificationType.INFORMATION)
            .apply {
                addAction(DumbAwareAction.create("Show") {
                    expire()
                    continuation.resume(true)
                })
                addAction(DumbAwareAction.create("Hide") {
                    expire()
                    continuation.resume(false)
                })
            }.run { Notifications.Bus.notify(this, project) }
    }
}
