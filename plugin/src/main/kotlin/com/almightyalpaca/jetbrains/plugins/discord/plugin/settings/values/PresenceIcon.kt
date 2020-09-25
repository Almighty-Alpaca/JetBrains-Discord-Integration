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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Asset as SourceAsset

typealias IconValue = SimpleValue<PresenceIcon>

enum class PresenceIcon(override val text: String, override val description: String? = null) : RenderedValue<PresenceIcon.Result>, UiValueType {
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

    fun SourceAsset?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.Asset(this)
    }

    sealed class Result {
        object Empty : Result()
        data class Asset(val value: SourceAsset) : Result()
    }
}
