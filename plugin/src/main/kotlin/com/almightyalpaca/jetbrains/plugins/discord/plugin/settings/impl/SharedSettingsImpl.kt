package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.SharedSettings

abstract class SharedSettingsImpl : SharedSettings {
    override var enabled: Boolean = true
}
