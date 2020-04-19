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
import com.intellij.ui.JBIntSpinner
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

fun OptionCreator<in Int>.spinner(description: String, initialValue: Int, minValue: Int = Int.MIN_VALUE, maxValue: Int = Int.MAX_VALUE, step: Int = 1, format: String = "#", enabled :Boolean = true) =
    OptionProviderImpl(this, IntSpinnerOption(description, initialValue, step, minValue, maxValue, format, enabled))

fun OptionCreator<in Int>.spinner(description: String, initialValue: Int, range: IntRange, step: Int = 1, format: String = "#", enabled :Boolean = true) =
    spinner(description, initialValue, range.first, range.last, step, format, enabled)

class IntSpinnerOption(
    description: String,
    initialValue: Int,
    private val step: Int,
    private val minValue: Int = Int.MIN_VALUE,
    private val maxValue: Int = Int.MAX_VALUE,
    private val format: String,
    private val enabled: Boolean
) : SimpleOption<Int>(description, initialValue.coerceIn(minValue, maxValue)) {
    override fun transformValue(value: Int) = value.coerceIn(minValue, maxValue)

    override val componentImpl by lazy {
        JBIntSpinner(currentValue, minValue, maxValue).apply spinner@{
            model = SpinnerNumberModel(currentValue, minValue, maxValue, step)
            editor = JSpinner.NumberEditor(this@spinner, format)

            this.isEnabled = enabled
        }
    }

    override val component by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            val label = label(description).apply {
                this.isEnabled = enabled
            }
            add(label, gbc {
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
        }
    }

    override var componentValue: Int
        get() = componentImpl.number
        set(value) {
            componentImpl.number = value
        }

    override fun addChangeListener(listener: (Int) -> Unit) {
        componentImpl.addChangeListener { listener(componentImpl.number) }

        listener(componentImpl.number)
    }

    override val isModified
        get() = currentValue != componentImpl.number

    override fun apply() {
        currentValue = componentImpl.number
    }

    override fun reset() {
        componentImpl.number = currentValue
    }

    override fun readString(string: String) {
        currentValue = string.toIntOrNull()?.let { v -> transformValue(v) } ?: currentValue
    }
}

typealias IntValue = SimpleValue<Int>
