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
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import org.jdom.Element
import javax.swing.JComponent

fun OptionCreator<in Unit?>.info(text: String, bold: Boolean = false) = OptionProviderImpl(this, InfoText(text, bold))

class InfoText(text: String, bold: Boolean) :
    SimpleOption<Unit?>(text, null, null) {
    override val componentImpl: JComponent by lazy {
        val styledText: String = when {
            bold -> "<html><b>$text</b></html>"
            else -> "<html>$text"
        }

        JBLabel(styledText)
            .withBorder(JBUI.Borders.empty(2, 0, 10, 0))
    }

    override val component: JComponent
        get() = componentImpl

    override var componentValue: Unit?
        get() = null
        set(value) = Unit

    override fun addChangeListener(listener: (Unit?) -> Unit) = Unit

    override val isModified = false
    override val isDefault = true

    override fun apply() = Unit
    override fun reset() = Unit

    override fun writeXml(element: Element, key: String) = Unit
    override fun readXml(element: Element, key: String) = Unit

    override fun writeString() = ""
    override fun readString(string: String) = Unit
}

typealias DummyValue = SimpleValue<Unit?>
