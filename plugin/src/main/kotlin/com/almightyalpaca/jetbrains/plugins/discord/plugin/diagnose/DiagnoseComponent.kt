package com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.application
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.components.BaseComponent
import kotlinx.coroutines.Deferred

interface DiagnoseComponent : BaseComponent {
    val ide: Deferred<IDE>
    val discord: Deferred<Discord>

    enum class IDE(val message: String) {
        SNAP("${ApplicationNamesInfo.getInstance().fullProductName} is running as a Snap package. This will most likely prevent prevent the plugin from connection to your Discord client!"),
        OTHER("")
    }

    enum class Discord(val message: String) {
        SNAP("It seems like Discord is running in a Snap package. This will most likely prevent prevent the plugin from connecting to your Discord client!"),
        BROWSER("It seems like Discord is running in the browser. The plugin will not be able to connect to the Discord client!"),
        CLOSED("Could not detect a running Discord client!"),
        OTHER("")
    }

    // TODO: periodically re-check Discord
    fun reportDiscordConnectionChange()
    // fun reportInternetConnectionChange()

    companion object {
        inline val instance: DiagnoseComponent
            get() = application.getComponent(DiagnoseComponent::class.java)
    }
}
