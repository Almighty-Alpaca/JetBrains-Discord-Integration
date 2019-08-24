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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ApplicationNotificationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.ApplicationNotificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.notificationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.notifications.types.UpdateNotification
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.plugin
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ApplicationNotificationComponentImpl : ApplicationNotificationComponent, CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    override fun initComponent() {
        with(notificationSettings) {
            checkUpdate()
        }
    }

    private fun ApplicationNotificationSettings.checkUpdate() {
        val version = plugin.version
        if (version != lastUpdateNotification && "eap" !in version) {
            notificationSettings.lastUpdateNotification = plugin.version

            launch {
                delay(10_000)
                UpdateNotification.show()
            }
        }
    }

    override fun disposeComponent() {}
}
