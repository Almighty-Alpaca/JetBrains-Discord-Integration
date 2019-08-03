/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.diagnose

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.application
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.components.BaseComponent
import kotlinx.coroutines.Deferred

interface DiagnoseComponent : BaseComponent {
    val ide: Deferred<IDE>
    val discord: Deferred<Discord>

    enum class IDE(val message: String) {
        SNAP("${ApplicationNamesInfo.getInstance().fullProductName} is running as a Snap package. This will most likely prevent prevent the plugin from connection to your Discord client!"),
        OTHER("")
    }

    enum class Discord(val message: String) {
        SNAP("It seems like Discord is running in a Snap package. This will most likely prevent prevent the plugin from connecting to your Discord client!"),
        BROWSER("It seems like Discord is running in the browser. The plugin will not be able to connect to the Discord client!"),
        CLOSED("Could not detect a running Discord client!"),
        OTHER("")
    }

    // TODO: periodically re-check Discord
    // fun reportDiscordConnectionChange()
    // fun reportInternetConnectionChange()

    companion object {
        inline val instance: DiagnoseComponent
            get() = application.getComponent(DiagnoseComponent::class.java)
    }
}
