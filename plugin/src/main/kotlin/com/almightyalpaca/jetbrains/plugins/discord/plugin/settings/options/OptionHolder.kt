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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option

interface OptionHolder : OptionCreator<Any?>, ComponentProvider {
    val options: MutableMap<String, Option<*>>

    override fun set(key: String, option: Option<out Any?>) {
        if (key in options)
            throw Exception("Option $key already exists")

        options[key] = option
    }

    var isComponentEnabled: Boolean
        get() = component?.isEnabled ?: true
        set(value) {
            component?.let { c ->
                c.isEnabled = value
                for (component in c.components) {
                    component.isEnabled = value
                }
            }
        }

    val isModified
        get() = options.values.any { option -> option.isModified }

    val isDefault
        get() = options.values.all { option -> option.isDefault }

    fun apply() = options.values.forEach { option -> option.apply() }
    fun reset() = options.values.forEach { option -> option.reset() }
}
