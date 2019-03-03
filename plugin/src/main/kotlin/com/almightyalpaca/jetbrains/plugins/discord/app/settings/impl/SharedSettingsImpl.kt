package com.almightyalpaca.jetbrains.plugins.discord.app.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.app.settings.SharedSettings

abstract class SharedSettingsImpl : SharedSettings {
    override var enabled: Boolean = true
}
