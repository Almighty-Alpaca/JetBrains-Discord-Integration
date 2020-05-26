/*
 * Copyright 2017-2020 Aljoscha Grebe
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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ProjectShow
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationStartupActivity : StartupActivity.Background, DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    override fun runActivity(project: Project) {
        launch {
            checkUpdate()
            checkAskShowProject(project)
        }
    }

    private fun checkUpdate() {
        DiscordPlugin.LOG.info("Checking for plugin update")

        val version = Plugin.version
        if (version != null && version.toString() != settings.applicationLastUpdateNotification.getStoredValue() && version.isStable()) {
            DiscordPlugin.LOG.info("Plugin update found, showing changelog")

            settings.applicationLastUpdateNotification.setStoredValue(version.toString())

            launch { ApplicationUpdateNotification.show(version.toString()) }
        }
    }

    private suspend fun checkAskShowProject(project: Project) {
        DiscordPlugin.LOG.info("Checking for project confirmation")

        val settings = project.settings

        if (settings.show.getStoredValue() == ProjectShow.ASK) {
            DiscordPlugin.LOG.info("Showing project confirmation dialog")

            val result = ProjectShowNotification.show(project)

            DiscordPlugin.LOG.info("Project confirmation result=$result")

            settings.show.setStoredValue(result)
            renderService.render()
        }
    }
}
