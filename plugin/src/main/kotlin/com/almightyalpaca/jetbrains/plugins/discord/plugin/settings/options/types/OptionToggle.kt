/*
 * Copyright 2017-2019 Aljoscha Grebe
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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import org.jdom.Element
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Box
import javax.swing.JPanel
import kotlin.reflect.KProperty

fun <T> OptionCreator<in Toggle<T>>.toggleable(enabled: Boolean = true) =
    OptionProviderImpl(this, OptionToggle(enabled))

val Toggle<Boolean>.toggle
    get() = toggle { it }

fun <T : Enum<T>> Toggle<T>.enableOn(vararg values: T) = toggle { it in values }
fun <T : Enum<T>> Toggle<T>.disableOn(vararg values: T) = toggle { it !in values }

class OptionToggle<T>(private val enabled: Boolean) : Option<Toggle<T>>(""), Toggle.Provider<T> {
    lateinit var toggle: Triple<String, SimpleOption<out T>, (T) -> Boolean>
    lateinit var option: kotlin.Pair<String, Option<*>>

    val isToggleInitialized: Boolean
        get() = this::toggle.isInitialized

    val isOptionInitialized: Boolean
        get() = this::option.isInitialized

    private val value = Toggle(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override val component by lazy {
        JPanel().apply panel@{
            layout = GridBagLayout()

            val toggleComponent = toggle.second.component
            val optionComponent = option.second.component

            if (enabled)
                toggle.second.addChangeListener { value -> option.second.isComponentEnabled = toggle.third(value) }

            add(toggleComponent, gbc {
                gridx = 0
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

//            add(Box.createHorizontalStrut(20))

            add(optionComponent, gbc {
                ipadx = 20
                gridx = 1
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

            add(Box.createHorizontalGlue(), gbc {
                gridx = 2
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            })

//            add(Box.createHorizontalGlue())
        }
    }
    override var isComponentEnabled: Boolean = true
        set(value) {
            field = value

            toggle.second.isComponentEnabled = value
            option.second.isComponentEnabled = value
        }

    override fun addChangeListener(listener: (Toggle<T>) -> Unit) =
        throw Exception("Cannot listen to toggleable changes")

    override val isModified: Boolean
        get() = toggle.second.isModified || option.second.isModified

    override val isDefault: Boolean
        get() = toggle.second.isDefault && option.second.isDefault

    override fun apply() {
        toggle.second.apply()
        option.second.apply()
    }

    override fun reset() {
        toggle.second.reset()
        option.second.reset()
    }

    override fun writeXml(element: Element, key: String) {
        if (!toggle.second.isDefault)
            toggle.second.writeXml(element, toggle.first)
        if (!option.second.isDefault)
            option.second.writeXml(element, option.first)
    }

    override fun readXml(element: Element, key: String) {
        toggle.second.readXml(element, toggle.first)
        option.second.readXml(element, option.first)
    }
}

class Toggle<T>(private val optionToggle: OptionToggle<T>) : Value() {
    fun toggle(predicate: (T) -> Boolean) = object : OptionCreator<T> {
        override fun set(key: String, option: Option<out T>) {
            if (option !is SimpleOption)
                throw Exception("Toggle has to be an option with a currentValue")
            else if (optionToggle.isToggleInitialized)
                throw Exception("Toggle has already been initialized")

            optionToggle.toggle = Triple(key, option, predicate)
        }
    }

    val option = object : OptionCreator<Any?> {
        override fun set(key: String, option: Option<out Any?>) {
            if (optionToggle.isOptionInitialized)
                throw Exception("Option has already been initialized")

            optionToggle.option = Pair(key, option)
        }
    }

    interface Provider<T> : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Toggle<T>
    }
}
