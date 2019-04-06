package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option

interface OptionCreator<T> {
    operator fun set(key: String, option: Option<out T>)
}
