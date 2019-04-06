package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option

interface OptionHolder : OptionCreator<Any?>, ComponentProvider {
    val options: MutableMap<String, Option<*>>

    override fun set(key: String, option: Option<out Any?>) {
        if (key in options)
            throw Exception("Option $key already exists")

        options[key] = option
    }

    var isComponentEnabled: Boolean
        get() = component.isEnabled
        set(value) {
            component.isEnabled = value
            for (component in component.components) {
                component.isEnabled = value
            }
        }

    val isModified
        get() = options.values.any { option -> option.isModified }

    val isDefault
        get() = options.values.all { option -> option.isDefault }

    fun apply() = options.values.forEach { option -> option.apply() }
    fun reset() = options.values.forEach { option -> option.reset() }
}
