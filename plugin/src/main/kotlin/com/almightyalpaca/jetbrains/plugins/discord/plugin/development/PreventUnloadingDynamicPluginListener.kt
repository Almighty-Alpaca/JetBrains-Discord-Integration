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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.development

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor

class PreventUnloadingDynamicPluginListener : DynamicPluginListener {
    override fun checkUnloadPlugin(pluginDescriptor: IdeaPluginDescriptor) {
        DiscordPlugin.LOG.debug("Processing unload event for ${pluginDescriptor.pluginId?.idString}")

        if (pluginDescriptor.pluginId == Plugin.pluginId) {
            DiscordPlugin.LOG.info("Preventing plugin unload")

            val e = Class
                .forName("com.intellij.ide.plugins.CannotUnloadPluginException")
                .constructors
                .first()
                .newInstance("unsupported") as Exception

            throw e
        }
    }
}
