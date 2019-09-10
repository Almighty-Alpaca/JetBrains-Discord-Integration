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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ProjectNotificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.AskShowProjectNotification
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.richPresenceRenderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ProjectNotificationComponentImpl(val project: Project) : ProjectNotificationComponent, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    override fun initComponent() {
        with(project.notificationSettings) {
            launch {
                delay(10_000)
                checkAskShowProject()
            }
        }
    }

    private suspend fun ProjectNotificationSettings.checkAskShowProject() {
        if (askShowProject) {
            project.settings.show.set(AskShowProjectNotification.show())
            askShowProject = false
            richPresenceRenderService.render()
        }
    }

    override fun disposeComponent() {
        parentJob.cancel()
    }
}
