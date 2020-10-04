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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.CustomTemplate
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.intellij.icons.AllIcons
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import java.awt.Desktop
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.net.URI
import javax.swing.JComponent
import javax.swing.JPanel

fun OptionCreator<in CustomTemplate>.template(text: String, description: String? = null, initialValue: String) =
    OptionProviderImpl(this, TemplateOption(text, description, initialValue))

fun OptionCreator<in CustomTemplate>.template(text: String, initialValue: String) =
    OptionProviderImpl(this, TemplateOption(text, null, initialValue))

class TemplateOption(text: String, description: String?, initialValue: String) : SimpleOption<CustomTemplate>(text, description, CustomTemplate(initialValue)) {

    private val textFieldInfoExtension = ExtendableTextComponent.Extension.create(AllIcons.General.Information, "Supports templates") {
        Desktop.getDesktop().browse(URI.create(Plugin.branchBase + "/plugin/templates.adoc"))
    }

    override val componentImpl by lazy {
        ExtendableTextField().apply {
            this.text = currentValue.template

            this.addExtension(textFieldInfoExtension)

            // TODO: Add dialog box with more info about templates, overview of methods/variables etc.

            // com.intellij.openapi.ui.cellvalidators.ValidationUtils

            // this.addExtension(ExtendableTextComponent.Extension.create(AllIcons.Actions.Edit, "") {
            //
            //     val dialog = TemplateDialog(text, componentValue.template)
            //     val result = dialog.showAndGet()
            //     if (result) {
            //         val value = dialog.value
            //         componentValue = CustomTemplate(value)
            //     }
            // })

            addPropertyChangeListener("enabled") { event ->
                if (event.newValue == true) {
                    addExtension(textFieldInfoExtension)
                } else {
                    removeExtension(textFieldInfoExtension)
                }
            }
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

    override var componentValue: CustomTemplate = CustomTemplate("")
        get() {
            if (field.template != componentImpl.text) {
                field = CustomTemplate(componentImpl.text)
            }
            return field
        }
        set(value) {
            field = value
            componentImpl.text = value.template
        }

    override fun addChangeListener(listener: (CustomTemplate) -> Unit) = throw UnsupportedOperationException("Not implemented")

    override val isModified
        get() = currentValue.template != componentImpl.text.trim()

    override fun apply() {
        currentValue = CustomTemplate(componentImpl.text)
    }

    override fun reset() {
        componentImpl.text = currentValue.template
    }

    override fun writeString(): String = currentValue.template

    override fun readString(string: String) {
        currentValue = CustomTemplate(string)
    }
}

typealias TemplateValue = SimpleValue<CustomTemplate>
