package com.almightyalpaca.jetbrains.plugins.discord.app.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JLabel

class DiscordConfigurable : Configurable {
    private val form by lazy { JLabel("Test") }

    override fun isModified() = true

    override fun getDisplayName() = "Test Settings"

    override fun apply() = Unit

    override fun createComponent() = form
}
