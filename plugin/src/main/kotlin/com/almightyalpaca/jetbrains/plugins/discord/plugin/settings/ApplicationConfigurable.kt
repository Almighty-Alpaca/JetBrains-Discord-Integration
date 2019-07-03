package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.ApplicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose.DiagnoseComponent
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

        ApplicationComponent.instance.update()
    }

    override fun reset() {
        settings.reset()
    }

    override fun createComponent() = JPanel().apply panel@{
        layout = BoxLayout(this@panel, BoxLayout.Y_AXIS)

        val diagnose = DiagnoseComponent.instance
        diagnose.discord.asCompletableFuture().thenAcceptAsync { discord ->
            if (discord != DiagnoseComponent.Discord.OTHER) {
                SwingUtilities.invokeLater { add(createErrorMessage(discord.message), 0) }
            }
        }

        diagnose.ide.asCompletableFuture().thenAcceptAsync { ide ->
            if (ide != DiagnoseComponent.IDE.OTHER) {
                SwingUtilities.invokeLater { add(createErrorMessage(ide.message), 0) }
            }
        }

        add(settings.component)
    }

    override fun getHelpTopic(): String? = null
}
