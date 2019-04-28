package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.GridBagConstraints
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.intellij.ui.components.JBTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun OptionCreator<in String>.text(description: String, initialValue: String, charLimit: Int = 0) = OptionProviderImpl(this, TextOption(description, initialValue, charLimit))

class TextOption(description: String, initialValue: String, private val charLimit: Int) : SimpleOption<String>(description, initialValue) {
    override val componentImpl by lazy {
        JBTextField().apply {
            // TODO: text field width
            // columns = charLimit
            text = currentValue
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
                fill = GridBagConstraints.HORIZONTAL
                weightx = 2.0
            })

//            add(Box.createHorizontalGlue(), GridBagConstraints {
//                gridx = 2
//                gridy = 0
//                gridwidth = 1
//                gridheight = 1
//                anchor = GridBagConstraints.WEST
//                fill = GridBagConstraints.HORIZONTAL
//                weightx = 1.0
//            })
        }
    }

    override val componentValue: String
        get() = componentImpl.text

    override fun addChangeListener(listener: (String) -> Unit) {
        componentImpl.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?): Unit = listener(componentImpl.text)
            override fun insertUpdate(e: DocumentEvent?): Unit = listener(componentImpl.text)
            override fun removeUpdate(e: DocumentEvent?): Unit = listener(componentImpl.text)
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
