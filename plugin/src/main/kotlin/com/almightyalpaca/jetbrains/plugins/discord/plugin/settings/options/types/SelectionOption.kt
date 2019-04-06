package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.boxLayoutPanel
import javax.swing.JComboBox

fun <T : Enum<T>> OptionCreator<in T>.selection(description: String, values: kotlin.Pair<T, Array<T>>) = selection(description, values.first, values.second)

fun <T : Enum<T>> OptionCreator<in T>.selection(description: String, initialValue: T, values: Array<T>) = OptionProviderImpl(this, EnumSelectionOption(description, initialValue, values))

class EnumSelectionOption<T : Enum<T>>(description: String, initialValue: T, private val values: Array<T>) : SimpleOption<T>(description, initialValue) {
    init {
        if (initialValue !in values)
            throw Exception("initialValue is not an allowed currentValue")
    }

    override val componentImpl by lazy {
        JComboBox<T>(values).apply {
            selectedItem = currentValue
        }
    }
    override val component by lazy { boxLayoutPanel(label(description), componentImpl) }

    @Suppress("UNCHECKED_CAST")
    override val componentValue: T
        get() = componentImpl.selectedItem as T

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
