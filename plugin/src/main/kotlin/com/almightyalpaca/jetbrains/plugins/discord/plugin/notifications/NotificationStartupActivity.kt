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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class NotificationStartupActivity : StartupActivity, DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    override fun runActivity(project: Project) {
        launch {
            checkUpdate()
            checkAskShowProject(project)
        }
    }

    private fun checkUpdate() {
        val version = Plugin.Version.toString()
        if (version != notificationSettings.lastUpdateNotification && Plugin.Version.isStable) {
            notificationSettings.lastUpdateNotification = version
            launch { ApplicationUpdateNotification.show() }
        }
    }

    private suspend fun checkAskShowProject(project: Project) {
        val settings = project.settings
        val notificationSettings = project.notificationSettings

        if (notificationSettings.askShowProject) {
            settings.show.set(AskShowProjectNotification.show(project))
            notificationSettings.askShowProject = false
            renderService.render()
        }
    }
}
