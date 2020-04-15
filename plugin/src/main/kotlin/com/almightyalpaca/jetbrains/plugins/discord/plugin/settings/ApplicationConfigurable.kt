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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose.DiagnoseService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose.diagnoseService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.renderService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.createErrorMessage
import com.intellij.openapi.options.SearchableConfigurable
import kotlinx.coroutines.future.asCompletableFuture
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

class ApplicationConfigurable : SearchableConfigurable {
    override fun getId() = "discord-application"

    override fun isModified(): Boolean = settings.isModified

    override fun getDisplayName() = "Discord Integration Application Settings"

    override fun apply() {
        settings.apply()

        renderService.render()
    }

    override fun reset() {
        settings.reset()
    }

    override fun createComponent() = JPanel().apply panel@{
        layout = BoxLayout(this@panel, BoxLayout.Y_AXIS)

        val service = diagnoseService

        service.discord.asCompletableFuture().thenAcceptAsync { discord ->
            if (discord != DiagnoseService.Discord.OTHER) {
                SwingUtilities.invokeLater { add(createErrorMessage(discord.message), 0) }
            }
        }

        service.plugins.asCompletableFuture().thenAcceptAsync { plugins ->
            if (plugins != DiagnoseService.Plugins.NONE) {
                SwingUtilities.invokeLater { add(createErrorMessage(plugins.message), 0) }
            }
        }

        service.ide.asCompletableFuture().thenAcceptAsync { ide ->
            if (ide != DiagnoseService.IDE.OTHER) {
                SwingUtilities.invokeLater { add(createErrorMessage(ide.message), 0) }
            }
        }

        add(settings.component)
    }

    override fun getHelpTopic(): String? = null
}
