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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

fun <T> OptionCreator<in T>.selection(description: String, values: kotlin.Pair<T, Array<T>>)
        where T : Enum<T>, T : ToolTipProvider =
    selection(description, values.first, values.second)

inline fun <reified T> OptionCreator<in T>.selection(description: String, initialValue: T)
        where T : Enum<T>, T : ToolTipProvider =
    selection(description, initialValue, enumValues())

fun <T> OptionCreator<in T>.selection(description: String, initialValue: T, values: Array<T>)
        where T : Enum<T>, T : ToolTipProvider =
    OptionProviderImpl(this, SelectionOption(description, initialValue, values))

class SelectionOption<T>(description: String, initialValue: T, private val values: Array<T>) : SimpleOption<T>(description, initialValue) where T : Enum<T>, T : ToolTipProvider {
    init {
        if (initialValue !in values)
            throw Exception("initialValue is not an allowed currentValue")
    }

    override val componentImpl by lazy {
        JComboBox(values).apply box@{
            renderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                    isEnabled = this@box.isEnabled
                    toolTipText = (value as ToolTipProvider).toolTip

                    return this
                }
            }
            selectedItem = currentValue
        }
    }

    override val component by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            add(label(description), gbc {
                gridx = 0
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

            add(componentImpl, gbc {
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
        }
    }

    @Suppress("UNCHECKED_CAST")
    override var componentValue: T
        get() = componentImpl.selectedItem as T
        set(value) {
            componentImpl.selectedItem = value
        }

    @Suppress("UNCHECKED_CAST")
    override fun addChangeListener(listener: (T) -> Unit) {
        componentImpl.addActionListener { listener(componentImpl.selectedItem as T) }

        listener(componentImpl.selectedItem as T)
    }

    override val isModified
        get() = currentValue != componentImpl.selectedItem

    @Suppress("UNCHECKED_CAST")
    override fun apply() {
        currentValue = componentImpl.selectedItem as T
    }

    override fun reset() {
        componentImpl.selectedItem = currentValue
    }

    override fun toString() = currentValue.name

    override fun writeString(): String {
        return currentValue.name
    }

    override fun readString(string: String) {
        currentValue = values.find { e -> e.name == string } ?: currentValue
    }
}

interface ToolTipProvider {
    val toolTip: String?
}
