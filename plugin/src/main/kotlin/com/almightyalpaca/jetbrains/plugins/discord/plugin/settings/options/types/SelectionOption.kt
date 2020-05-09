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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.contains
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.util.*
import javax.swing.*
import kotlin.reflect.KProperty

inline fun <reified T> OptionCreator<in T>.selection(text: String, description: String? = null, values: kotlin.Pair<T, Array<T>>)
        where T : Enum<T>, T : UiValueType =
    selection(text, description, values.first, values.second)

inline fun <reified T> OptionCreator<in T>.selection(text: String, values: kotlin.Pair<T, Array<T>>)
        where T : Enum<T>, T : UiValueType =
    selection(text, null, values.first, values.second)

inline fun <reified T> OptionCreator<in T>.selection(text: String, description: String? = null, initialValue: T)
        where T : Enum<T>, T : UiValueType =
    selection(text, description, initialValue, enumValues())

inline fun <reified T> OptionCreator<in T>.selection(text: String, initialValue: T)
        where T : Enum<T>, T : UiValueType =
    selection(text, null, initialValue, enumValues())

inline fun <reified T> OptionCreator<in T>.selection(text: String, description: String? = null, initialValue: T, selectableValues: Array<T>)
        where T : Enum<T>, T : UiValueType =
    OptionProviderImpl(this, SelectionOption(text, description, initialValue, selectableValues, enumValues()))

class SelectionOption<T>(
    text: String,
    description: String?,
    initialValue: T,
    val selectableValues: Array<T>,
    val values: Array<T>
) :
    SimpleOption<T>(text, description, initialValue) where T : Enum<T>, T : UiValueType {
    init {
        if (initialValue !in values)
            throw Exception("initialValue is not an allowed currentValue")
    }

    override val value: SelectionValue<T> = SelectionValue(this)

    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    private val model = when (currentValue) {
        in selectableValues -> DefaultComboBoxModel(selectableValues)
        else -> {
            val values = Vector<T>(selectableValues.size + 1)
            values.add(currentValue)
            values.addAll(selectableValues)

            DefaultComboBoxModel(values)
        }
    }

    override val componentImpl by lazy {
        JComboBox(model).apply box@{
            addItemListener { e ->
                if (e.stateChange == ItemEvent.DESELECTED) {
                    if (e.item !in selectableValues) {
                        this@SelectionOption.model.removeElement(e.item)
                    }
                }
            }

            renderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component? {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                    value as UiValueType

                    this.isEnabled = this@box.isEnabled
                    this.text = value.text
                    this.toolTipText = value.description

                    if (index == -1 && value !in selectableValues) {
                        this.isEnabled = false
                    }

                    return this
                }
            }
            selectedItem = currentValue
        }
    }

    override val component: JComponent by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            add(label(text), gbc {
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
            val oldValue = componentImpl.selectedItem

            if (value == oldValue) {
                return
            }

            if (value !in model) {
                model.insertElementAt(value, 0)
            }

            componentImpl.selectedItem = value

            if (oldValue !in selectableValues) {
                this@SelectionOption.model.removeElement(oldValue)
            }
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
        componentValue = currentValue
    }

    override fun toString() = currentValue.name

    override fun writeString(): String {
        return currentValue.name
    }

    override fun readString(string: String) {
        currentValue = values.find { e -> e.name == string } ?: currentValue
    }
}

class SelectionValue<T>(override val option: SelectionOption<T>) :
    SimpleValueImpl<T>(option) where T : Enum<T>, T : UiValueType {
    val values
        get() = option.values

    val selectableValues
        get() = option.selectableValues
}
