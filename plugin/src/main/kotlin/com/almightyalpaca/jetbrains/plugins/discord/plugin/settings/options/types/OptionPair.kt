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
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.reflect.KProperty

fun OptionCreator<in Pair>.pair() = OptionProviderImpl(this, OptionPair())

class OptionPair : Option<Pair>(""), Pair.Provider {
    lateinit var first: kotlin.Pair<String, Option<*>>
    lateinit var second: kotlin.Pair<String, Option<*>>

    val isFirstInitialized: Boolean
        get() = this::first.isInitialized

    val isSecondInitialized: Boolean
        get() = this::second.isInitialized

    private val value = Pair(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override val component: JComponent? by lazy {
        JPanel().apply panel@{
            layout = BoxLayout(this@panel, BoxLayout.X_AXIS)

            add(first.second.component)
            add(Box.createHorizontalStrut(20))
            add(second.second.component)

            add(Box.createHorizontalGlue())
        }
    }
    override var isComponentEnabled: Boolean = true
        set(value) {
            field = value

            first.second.isComponentEnabled = value
            second.second.isComponentEnabled = value
        }

    override fun addChangeListener(listener: (Pair) -> Unit) = throw Exception("Cannot listen to pair changes")

    override val isModified: Boolean
        get() = first.second.isModified || second.second.isModified

    override val isDefault: Boolean
        get() = first.second.isDefault && second.second.isDefault

    override fun apply() {
        first.second.apply()
        second.second.apply()
    }

    override fun reset() {
        first.second.reset()
        second.second.reset()
    }

    override fun writeXml(element: Element, key: String) {
        if (!first.second.isDefault)
            first.second.writeXml(element, first.first)
        if (!second.second.isDefault)
            second.second.writeXml(element, second.first)
    }

    override fun readXml(element: Element, key: String) {
        first.second.readXml(element, first.first)
        second.second.readXml(element, second.first)
    }
}

class Pair(private val optionPair: OptionPair) : Value() {
    val first = object : OptionCreator<Any?> {
        override fun set(key: String, option: Option<out Any?>) {
            if (optionPair.isFirstInitialized)
                throw Exception("Option has already been initialized")

            optionPair.first = Pair(key, option)
        }
    }

    val second = object : OptionCreator<Any?> {
        override fun set(key: String, option: Option<out Any?>) {
            if (optionPair.isSecondInitialized)
                throw Exception("Option has already been initialized")

            optionPair.second = Pair(key, option)
        }
    }

    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Pair
    }
}
