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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.CustomTemplate
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.asCustomTemplateContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.TemplateValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.Plugin
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

abstract class Renderer(protected val context: RenderContext) {
    fun render(): RichPresence = context.render()

    protected abstract fun RenderContext.render(): RichPresence

    protected fun RenderContext.render(
        details: TextValue?,
        detailsCustom: TemplateValue?,
        state: TextValue?,
        stateCustom: TemplateValue?,
        largeIcon: IconValue?,
        largeIconText: TextValue?,
        largeIconTextCustom: TemplateValue?,
        smallIcon: IconValue?,
        smallIconText: TextValue?,
        smallIconTextCustom: TemplateValue?,
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
            val customTemplateContext by lazy { context.asCustomTemplateContext() }

            this@presence.details = when (val line = details?.getValue()?.get(context)) {
                null, PresenceText.Result.Empty -> null
                PresenceText.Result.Custom -> detailsCustom?.getValue()?.execute(customTemplateContext)
                is PresenceText.Result.String -> line.value
            }

            this@presence.state = when (val line = state?.getValue()?.get(context)) {
                null, PresenceText.Result.Empty -> null
                PresenceText.Result.Custom -> stateCustom?.getValue()?.execute(customTemplateContext)
                is PresenceText.Result.String -> line.value
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
                        null, PresenceText.Result.Empty -> null
                        is PresenceText.Result.String -> text.value
                        PresenceText.Result.Custom -> largeIconTextCustom?.getValue()?.execute(customTemplateContext)
                    }
                    RichPresence.Image(icon.value, caption)
                }
            }

            this@presence.smallImage = when (val icon = smallIcon?.getValue()?.get(context)) {
                null, PresenceIcon.Result.Empty -> null
                is PresenceIcon.Result.Asset -> {
                    val caption = when (val text = smallIconText?.getValue()?.get(context)) {
                        null, PresenceText.Result.Empty -> null
                        is PresenceText.Result.String -> text.value
                        PresenceText.Result.Custom -> smallIconTextCustom?.getValue()?.execute(customTemplateContext)
                    }
                    RichPresence.Image(icon.value, caption)
                }
            }

            this.partyId = Plugin.version?.toString()
        }
    }

    enum class Mode {
        NORMAL,
        PREVIEW;

        fun <T> SimpleValue<T>.getValue() = getValue(this@Mode)

        fun <T> SimpleValue<T>.setValue(value: T) = setValue(this@Mode, value)

        fun <T> SimpleValue<T>.updateValue(block: (T) -> T) = updateValue(this@Mode, block)
    }

    sealed class Type {
        abstract fun createRenderer(context: RenderContext): Renderer?

        open class None protected constructor() : Type() {
            override fun createRenderer(context: RenderContext): Renderer? = null

            companion object : None()
        }

        open class Idle protected constructor() : Type() {
            override fun createRenderer(context: RenderContext): Renderer? = IdleRenderer(context)

            companion object : Idle()
        }

        open class Application protected constructor() : Type() {
            override fun createRenderer(context: RenderContext): Renderer = ApplicationRenderer(context)

            companion object : Application()
        }

        open class Project protected constructor() : Application() {
            override fun createRenderer(context: RenderContext): Renderer = ProjectRenderer(context)

            companion object : Project()
        }

        open class File protected constructor() : Project() {
            override fun createRenderer(context: RenderContext): Renderer = FileRenderer(context)

            companion object : File()
        }
    }
}
