package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Value
import kotlin.reflect.KProperty

interface OptionProvider<T : Any?> {
    operator fun provideDelegate(thisRef: OptionHolder, prop: KProperty<*>): Value.Provider
}
