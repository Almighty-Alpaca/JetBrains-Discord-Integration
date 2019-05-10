package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.intellij.ui.components.JBCheckBox
import javax.swing.JComponent

fun OptionCreator<in Boolean>.check(description: String, initialValue: Boolean) = OptionProviderImpl(this, CheckOption(description, initialValue))

class CheckOption(description: String, initialValue: Boolean) : SimpleOption<Boolean>(description, initialValue) {
    override val componentImpl by lazy { JBCheckBox(description, currentValue) }
    override val component: JComponent
        get() = componentImpl

    override var componentValue: Boolean
        get() = componentImpl.isSelected
        set(value) {
            componentImpl.isSelected = value
        }

    override fun addChangeListener(listener: (Boolean) -> Unit) {
        componentImpl.addChangeListener { listener(componentImpl.isSelected) }

        listener(componentImpl.isSelected)
    }

    override val isModified
        get() = currentValue != componentImpl.isSelected

    override fun apply() {
        currentValue = componentImpl.isSelected
    }

    override fun reset() {
        componentImpl.isSelected = currentValue
    }

    override fun readString(string: String) {
        currentValue = string.toBoolean()
    }
}

typealias BooleanValue = SimpleValue<Boolean>

fun BooleanValue.toggle() {
    set(!get())
}
