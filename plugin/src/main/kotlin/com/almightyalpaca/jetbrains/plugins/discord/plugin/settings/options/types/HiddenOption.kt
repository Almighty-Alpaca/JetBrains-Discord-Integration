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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import org.jdom.Element
import javax.swing.JComponent
import kotlin.reflect.KProperty

fun OptionCreator<Any?>.hidden() = OptionProviderImpl(this, HiddenOption())

class HiddenOption : Option<Hidden>(""), OptionHolder, Hidden.Provider {
    private val value = Hidden(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override fun addChangeListener(listener: (Hidden) -> Unit) = throw Exception("Cannot listen to hidden changes")

    override val component: JComponent? = null

    override var isComponentEnabled: Boolean
        get() = false
        set(_) {}

    override val options: MutableMap<String, Option<*>> = LinkedHashMap()

    override val isModified: Boolean
        get() = options.values.any(Option<*>::isModified)
    override val isDefault: Boolean
        get() = options.values.all(Option<*>::isDefault)

    override fun apply() = super.apply()
    override fun reset() = super.reset()

    override fun writeXml(element: Element, key: String) {
        for ((childKey, option) in options) {
            if (!option.isDefault) {
                option.writeXml(element, childKey)
            }
        }
    }

    override fun readXml(element: Element, key: String) {
        for ((childKey, option) in options) {
            option.readXml(element, childKey)
        }
    }
}

class Hidden(private val option: HiddenOption) : Value(), OptionHolder by option {
    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Hidden
    }
}
