package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option
import kotlin.reflect.KProperty

class OptionProviderImpl<S, T : Option<S>>(private val options: OptionCreator<in S>, private val option: T) : OptionProvider<S> {
    override operator fun provideDelegate(thisRef: OptionHolder, prop: KProperty<*>): T {
        val name = prop.name

        options[name] = option

        return option
    }
}
