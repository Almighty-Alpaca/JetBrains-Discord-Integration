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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.gui.themes.ThemeDialog
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.gbc
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.label
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.throwing
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.intellij.openapi.util.JDOMExternalizerUtil
import kotlinx.coroutines.future.asCompletableFuture
import org.jdom.Element
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel
import kotlin.reflect.KProperty

fun OptionCreator<in ThemeValue>.themeChooser(description: String) = OptionProviderImpl(this, ThemeOption(description))

class ThemeOption(description: String) : Option<ThemeValue>(description), ThemeValue.Provider {
    private val source: Source = sourceService.source

    private val listeners = mutableListOf<(ThemeValue) -> Unit>()
    override fun addChangeListener(listener: (ThemeValue) -> Unit) {
        listeners += listener
    }

    var currentValue: String? = null
    var componentValue: String? = null
        private set

    private val componentImpl = JButton().apply button@{
        isEnabled = false
        text = "Loading..."

        addActionListener {
            val themes = this@ThemeOption.source.getThemesOrNull()

            if (themes != null) {
                val dialog = ThemeDialog(themes, this@ThemeOption.componentValue)
                val result = dialog.showAndGet()

                if (result) {
                    val value = dialog.value

                    this@ThemeOption.componentValue = value
                    text = themes[value]!!.name
                }
            }
        }
    }

    override val component by lazy {
        JPanel().apply {
            layout = GridBagLayout()

            add(label(description), gbc {
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

    override var isComponentEnabled by throwing<Boolean> { UnsupportedOperationException() } // TODO

    init {
        source.getThemesAsync().asCompletableFuture().thenAcceptAsync { themes ->
            var value = this.currentValue
            if (value == null || value !in themes.keys) {
                value = themes.default.id
                this.currentValue = value
            }

            this.componentValue = value

            this.componentImpl.isEnabled = true
            this.componentImpl.text = themes[value]!!.name
        }
    }

    private val value = ThemeValue(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = this.value

    override val isModified: Boolean
        get() = this.currentValue != this.componentValue

    override val isDefault: Boolean
        get() = this.source.getThemesOrNull()?.default?.id?.equals(this.currentValue) ?: false

    override fun apply() {
        this.currentValue = this.componentValue
    }

    override fun reset() {
        this.componentValue = this.currentValue
    }

    override fun writeXml(element: Element, key: String) {
        JDOMExternalizerUtil.writeField(element, key, this.currentValue)
    }

    override fun readXml(element: Element, key: String) {
        JDOMExternalizerUtil.readField(element, key)?.let { s -> this.currentValue = s }
    }
}

class ThemeValue(private val option: ThemeOption) : SimpleValue<String?>() {
    override fun get() = this.option.currentValue
    override fun getComponent() = this.option.componentValue
    override fun set(value: String?) {
        this.option.currentValue = value
    }

    override val description: String
        get() = this.option.description

    interface Provider : SimpleValue.Provider<String?> {
        override fun getValue(thisRef: OptionHolder, property: KProperty<*>): ThemeValue
    }
}
