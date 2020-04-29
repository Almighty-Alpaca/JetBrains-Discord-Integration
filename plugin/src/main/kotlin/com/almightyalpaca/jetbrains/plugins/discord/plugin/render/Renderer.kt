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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

abstract class Renderer(private val context: RenderContext) {
    fun render(): RichPresence = context.render()

    abstract fun RenderContext.render(): RichPresence

    protected fun RenderContext.render(
        details: LineValue?,
        detailsCustom: StringValue?,
        state: LineValue?,
        stateCustom: StringValue?,
        largeIcon: IconValue?,
        largeIconText: IconTextValue?,
        smallIcon: IconValue?,
        smallIconText: IconTextValue?,
        startTimestamp: TimeValue?
    ): RichPresence {
        DiscordPlugin.LOG.debug("Rendering presence, data=${context.data}, mode=${context.mode}")
        DiscordPlugin.LOG.debug("Themes: ${context.source.getThemesOrNull()}")
        DiscordPlugin.LOG.debug("languages: ${context.source.getLanguagesOrNull()}")
        DiscordPlugin.LOG.debug("Icons: ${context.icons}")
        DiscordPlugin.LOG.debug("Data: ${context.data}")
        DiscordPlugin.LOG.debug("Mode: ${context.mode}")

        if (context.icons == null) {
            DiscordPlugin.LOG.debug("RenderContext.icons=null")
        }

        return RichPresence(context.icons?.applicationId) presence@{
            this@presence.details = when (val line = details?.getValue()?.get(context)) {
                null, PresenceLine.Result.Empty -> null
                PresenceLine.Result.Custom -> detailsCustom?.getValue()
                is PresenceLine.Result.String -> line.value
            }

            this@presence.state = when (val line = state?.getValue()?.get(context)) {
                null, PresenceLine.Result.Empty -> null
                PresenceLine.Result.Custom -> stateCustom?.getValue()
                is PresenceLine.Result.String -> line.value
            }

            this@presence.startTimestamp = when (val time = startTimestamp?.getValue()?.get(context)) {
                null, PresenceTime.Result.Empty -> null
                is PresenceTime.Result.Time ->
                    OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(time.value),
                        ZoneId.systemDefault()
                    )
            }

            this@presence.largeImage = when (val icon = largeIcon?.getValue()?.get(context)) {
                null, PresenceIcon.Result.Empty -> null
                is PresenceIcon.Result.Asset -> {
                    val caption = when (val text = largeIconText?.getValue()?.get(context)) {
                        null, PresenceIconText.Result.Empty -> null
                        is PresenceIconText.Result.String -> text.value
                    }
                    RichPresence.Image(icon.value, caption)
                }
            }

            this@presence.smallImage = when (val icon = smallIcon?.getValue()?.get(context)) {
                null, PresenceIcon.Result.Empty -> null
                is PresenceIcon.Result.Asset -> {
                    val caption = when (val text = smallIconText?.getValue()?.get(context)) {
                        null, PresenceIconText.Result.Empty -> null
                        is PresenceIconText.Result.String -> text.value
                    }
                    RichPresence.Image(icon.value, caption)
                }
            }

            this.partyId = Plugin.version?.toString()
        }
    }

    enum class Mode {
        NORMAL,
        PREVIEW
    }

    enum class Type(val createRenderer: (RenderContext) -> Renderer) {
        APPLICATION({ context -> ApplicationRenderer(context) }),
        PROJECT({ context -> ProjectRenderer(context) }),
        FILE({ context -> FileRenderer(context) });
    }
}
