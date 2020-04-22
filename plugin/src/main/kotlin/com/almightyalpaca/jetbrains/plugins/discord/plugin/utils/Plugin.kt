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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

object Plugin {
    private val pluginId: PluginId? by lazy { PluginId.getId("com.almightyalpaca.intellij.plugins.discord") }
    private val plugin: IdeaPluginDescriptor? by lazy { PluginManagerCore.getPlugin(pluginId) }

    fun getId() = pluginId?.idString

    val version: Version? by lazy { plugin?.version?.let { Version(it) } }

    class Version(private val asString: String) {
        override fun toString(): String = asString

        fun isStable() = asString.matches(Regex("""\d+\.\d+\.\d+"""))
    }
}
