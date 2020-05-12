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

@file:Suppress("DEPRECATION")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option
import org.jdom.Element
import javax.swing.JComponent

@Suppress("DEPRECATION")
open class HiddenOptionHolderImpl : OptionHolder {
    override val options = LinkedHashMap<String, Option<*>>()

    override val component: JComponent? = null

    fun readExternal(element: Element) {
        for ((key, option) in options) {
            option.readXml(element, key)
        }
    }

    fun writeExternal(element: Element) {
        for ((key, option) in options) {
            if (!option.isDefault) {
                option.writeXml(element, key)
            }
        }
    }
}
