package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.GridBagConstraints
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.intellij.ui.JBIntSpinner
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

fun OptionCreator<in Int>.spinner(description: String, initialValue: Int, minValue: Int = Int.MIN_VALUE, maxValue: Int = Int.MAX_VALUE, step: Int = 1, format: String = "#") =
    OptionProviderImpl(this, IntSpinnerOption(description, initialValue, step, minValue, maxValue, format))

fun OptionCreator<in Int>.spinner(description: String, initialValue: Int, range: IntRange, step: Int = 1, format: String = "#") =
    spinner(description, initialValue, range.start, range.endInclusive, step, format)

class IntSpinnerOption(
    description: String,
    initialValue: Int,
    private val step: Int,
    private val minValue: Int = Int.MIN_VALUE,
    private val maxValue: Int = Int.MAX_VALUE,
    private val format: String
) : SimpleOption<Int>(description, initialValue.coerceIn(minValue, maxValue)) {
    override fun transformValue(value: Int) = value.coerceIn(minValue, maxValue)

    override val componentImpl by lazy {
        JBIntSpinner(currentValue, minValue, maxValue).apply spinner@{
            model = SpinnerNumberModel(currentValue, minValue, maxValue, step)
            editor = JSpinner.NumberEditor(this@spinner, format)
        }
    }

    override val component by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            add(label(description), GridBagConstraints {
                gridx = 0
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })

            add(componentImpl, GridBagConstraints {
                gridx = 1
                gridy = 0
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.WEST
            })
        }
    }

    override val componentValue: Int
        get() = componentImpl.number

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
