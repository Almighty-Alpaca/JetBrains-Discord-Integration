package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.GridBagConstraints
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

fun <T : Enum<T>> OptionCreator<in T>.selection(description: String, values: kotlin.Pair<T, Array<T>>) = selection(description, values.first, values.second)

inline fun <reified T : Enum<T>> OptionCreator<in T>.selection(description: String, initialValue: T) = selection(description, initialValue, enumValues())

fun <T : Enum<T>> OptionCreator<in T>.selection(description: String, initialValue: T, values: Array<T>) = OptionProviderImpl(this, SelectionOption(description, initialValue, values))

class SelectionOption<T : Enum<T>>(description: String, initialValue: T, private val values: Array<T>) : SimpleOption<T>(description, initialValue) {
    init {
        if (initialValue !in values)
            throw Exception("initialValue is not an allowed currentValue")
    }

    override val componentImpl by lazy {
        JComboBox<T>(values).apply box@{
            renderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                    isEnabled = this@box.isEnabled

                    return this
                }
            }
            selectedItem = currentValue
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

            add(Box.createHorizontalGlue(), GridBagConstraints {
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
