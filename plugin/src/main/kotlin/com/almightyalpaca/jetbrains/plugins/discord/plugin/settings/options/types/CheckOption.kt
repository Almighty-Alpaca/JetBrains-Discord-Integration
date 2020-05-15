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
import com.intellij.ui.components.JBCheckBox
import javax.swing.JComponent

fun OptionCreator<in Boolean>.check(text: String, description: String? = null, initialValue: Boolean, enabled: Boolean = true) =
    OptionProviderImpl(this, CheckOption(text, description, initialValue, enabled))

fun OptionCreator<in Boolean>.check(text: String, initialValue: Boolean, enabled: Boolean = true) =
    OptionProviderImpl(this, CheckOption(text, null, initialValue, enabled))

class CheckOption(text: String, description: String?, initialValue: Boolean, private val enabled: Boolean) :
    SimpleOption<Boolean>(text, description, initialValue) {
    override val componentImpl by lazy {
        JBCheckBox(text, currentValue).apply {
            this.isEnabled = enabled
        }
    }

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

fun BooleanValue.toggle() = updateStoredValue { !it }
