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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.components.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.accessedAt
import com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.openedAt
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.richPresenceRenderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.richPresenceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray.BintraySource
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.application
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.nio.file.Paths
import java.time.OffsetDateTime

class ApplicationComponentImpl : ApplicationComponent {
    override val source: Source

    init {
        val icons: String? = System.getenv("com.almightyalpaca.jetbrains.plugins.discord.plugin.source")
        val (platform, location) = icons?.split(':', limit = 2) ?: listOf("", "")
        source = when (platform.toLowerCase()) {
            "bintray" -> BintraySource(location)
            "local" -> LocalSource(Paths.get(location))
            else -> BintraySource("almightyalpaca/JetBrains-Discord-Integration/Icons")
        }
    }

    override fun initComponent() {
        application.openedAt = System.currentTimeMillis()
        richPresenceRenderService.render()


        Toolkit.getDefaultToolkit().addAWTEventListener({ e ->
            when (e) {
                is MouseEvent, is KeyEvent -> {
                    application.accessedAt = System.currentTimeMillis()
                    richPresenceRenderService.render()
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK or AWTEvent.KEY_EVENT_MASK)
    }

    @Synchronized
    override fun disposeComponent() {
        richPresenceService.update(null)
    }

    companion object : Logging()
}
