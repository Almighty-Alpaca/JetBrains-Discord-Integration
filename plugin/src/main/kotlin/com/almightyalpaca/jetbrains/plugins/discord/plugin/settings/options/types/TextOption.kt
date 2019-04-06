package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.boxLayoutPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun OptionCreator<in String>.text(description: String, initialValue: String) = OptionProviderImpl(this, TextOption(description, initialValue))

class TextOption(description: String, initialValue: String) : SimpleOption<String>(description, initialValue) {
    override val componentImpl by lazy { JTextField(currentValue) }
    override val component by lazy { boxLayoutPanel(label(description), componentImpl) }

    override val componentValue: String
        get() = componentImpl.text

    override fun addChangeListener(listener: (String) -> Unit) {
        componentImpl.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) = listener(componentImpl.text)
            override fun insertUpdate(e: DocumentEvent?) = listener(componentImpl.text)
            override fun removeUpdate(e: DocumentEvent?) = listener(componentImpl.text)
        })

        listener(componentImpl.text)
    }

    override val isModified
        get() = currentValue != componentImpl.text.trim()

    override fun apply() {
        currentValue = componentImpl.text.trim()
    }

    override fun reset() {
        componentImpl.text = currentValue.trim()
    }

    override fun readString(string: String) {
        currentValue = string
    }
}

typealias StringValue = SimpleValue<String>
