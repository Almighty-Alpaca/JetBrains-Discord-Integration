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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ProjectNotificationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.AskShowNotification
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

class ProjectNotificationComponentImpl(val project: Project) : ProjectNotificationComponent {
    override fun initComponent() {
        showAskShowProject()
    }

    private fun showAskShowProject() {
        if (project.notificationSettings.askShowProject) {
            val notification = AskShowNotification { show ->
                project.settings.show.set(show)
                project.notificationSettings.askShowProject = false
            }
            Notifications.Bus.notify(notification)
        }
    }

    override fun disposeComponent() {}
}
