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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.intellij.ui.components.JBTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun OptionCreator<in String>.text(text: String, description: String? = null, initialValue: String, charLimit: Int = 0) =
    OptionProviderImpl(this, TextOption(text, description, initialValue, charLimit))

fun OptionCreator<in String>.text(text: String, initialValue: String, charLimit: Int = 0) =
    OptionProviderImpl(this, TextOption(text, null, initialValue, charLimit))

class TextOption(text: String, description: String?, initialValue: String, private val charLimit: Int) : SimpleOption<String>(text, description, initialValue) {
    override val componentImpl by lazy {
        JBTextField().apply {
            // TODO: text field width
            // columns = charLimit
            this.text = currentValue
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

    override var componentValue: String
        get() = componentImpl.text
        set(value) {
            componentImpl.text = value
        }

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
