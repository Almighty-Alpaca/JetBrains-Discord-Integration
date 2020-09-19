/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("DEPRECATION")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.ComponentProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import org.jdom.Element
import kotlin.reflect.KProperty

abstract class Option<T>(val text: String) : Value.Provider, ComponentProvider {
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

/**
 * Marks a type to be displayed on the UI. Usually used together with enums and [SelectionOption].
 */
interface UiValueType {
    /**
     * The display name of the item.
     */
    val text: String

    /**
     * The tooltip of the item.
     */
    val description: String?
}
