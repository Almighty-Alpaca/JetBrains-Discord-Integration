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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.intellij.ui.IdeBorderFactory
import org.jdom.Element
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

fun OptionCreator<in OptionHolder>.group(description: String) = OptionProviderImpl(this, OptionGroup(description))

class OptionGroup(description: String) : Option<Group>(description), OptionHolder, Group.Provider {
    private val value = Group(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override fun addChangeListener(listener: (Group) -> Unit) = throw Exception("Cannot listen to group changes")

    override val component: JComponent? by lazy {
        JPanel().apply panel@{
            layout = GridBagLayout()

            var y = 0
            for (option in options.values) {
                add(option.component, gbc {
                    gridx = 0
                    gridy = y++
                    gridwidth = 1
                    gridheight = 1
                    anchor = GridBagConstraints.NORTHWEST
                    fill = GridBagConstraints.HORIZONTAL
                    weightx = 1.0
                })
            }

            val fontHeight = getFontMetrics(font).height

            if (description.isNotBlank())
                border = IdeBorderFactory.createTitledBorder(description, false, Insets((fontHeight * 1.5).roundToInt(), 0, fontHeight, 0))
        }
    }

    override var isComponentEnabled: Boolean = true
        set(value) {
            field = value

            for (option in options.values) {
                option.isComponentEnabled = value
            }
        }

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

class Group(private val option: OptionGroup) : Value(), OptionHolder by option {
    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Group
    }
}
