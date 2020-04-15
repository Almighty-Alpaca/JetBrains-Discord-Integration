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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ToolTipProvider

typealias IconValue = SimpleValue<PresenceIcon>

enum class PresenceIcon(val description: String, override val toolTip: String? = null) : ToolTipProvider {
    APPLICATION("Application") {
        override fun RenderContext.getResult() = icons?.getAsset("application").toResult()
    },
    FILE("File") {
        override fun RenderContext.getResult(): Result {
            return icons?.let { icons -> language?.findIcon(icons) }?.asset.toResult()
        }
    },
    NONE("None") {
        override fun RenderContext.getResult() = Result.Empty
    };

    protected abstract fun RenderContext.getResult(): Result

    fun get(context: RenderContext) = context.run { getResult() }

    override fun toString() = description

    object Large {
        val Application = APPLICATION to arrayOf(APPLICATION, NONE)
        val Project = APPLICATION to arrayOf(APPLICATION, NONE)
        val File = FILE to arrayOf(APPLICATION, FILE, NONE)
    }

    object Small {
        val Application = NONE to arrayOf(APPLICATION, NONE)
        val Project = NONE to arrayOf(APPLICATION, NONE)
        val File = APPLICATION to arrayOf(APPLICATION, FILE, NONE)
    }

    fun com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.Asset(this)
    }

    sealed class Result {
        object Empty : Result()
        data class Asset(val value: com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset) : Result()
    }
}
