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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.items

import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.ActionItem
import com.almightyalpaca.jetbrains.plugins.discord.plugin.actions.types.ToggleAction
import com.almightyalpaca.jetbrains.plugins.discord.plugin.icons.Icons
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

class ProjectHideAction : ActionItem {
    override fun ActionComponent.create() {

        val hidden = ToggleAction.View {
            text = "Show project in Rich Presence"
            // description = ""
            icon = Icons.DISCORD_WHITE
            hoveredIcon = Icons.DISCORD_BLURPLE
        }

        val shown = ToggleAction.View {
            text = "Hide project in Rich Presence"
            // description = ""
            icon = Icons.DISCORD_BLURPLE
            hoveredIcon = Icons.DISCORD_WHITE
        }

        addAction(ToggleAction({ project -> project.settings.show }, shown, hidden))
    }
}
