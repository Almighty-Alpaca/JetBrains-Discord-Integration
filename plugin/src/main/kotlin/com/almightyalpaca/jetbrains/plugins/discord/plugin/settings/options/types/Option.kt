@file:Suppress("DEPRECATION")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.ComponentProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import org.jdom.Element
import kotlin.reflect.KProperty

abstract class Option<T>(var description: String) : Value.Provider, ComponentProvider {
    abstract fun addChangeListener(listener: (T) -> Unit)
    abstract var isComponentEnabled: Boolean

    abstract val isModified: Boolean
    abstract val isDefault: Boolean

    abstract fun apply()
    abstract fun reset()

    abstract fun writeXml(element: Element, key: String)
    abstract fun readXml(element: Element, key: String)
}

abstract class Value {
    interface Provider {
        operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Value
    }
}
