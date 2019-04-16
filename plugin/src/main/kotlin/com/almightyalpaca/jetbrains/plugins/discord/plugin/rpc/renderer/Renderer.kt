package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.types.*
import java.time.Duration
import java.time.OffsetDateTime

abstract class Renderer(private val context: RenderContext) {
    fun forceRender(): RichPresence = context.forceRender()

    abstract fun RenderContext.forceRender(): RichPresence

    private fun RenderContext.needsRender(): Boolean {
        if (settings.hide.getValue())
            return false

        val accessedAt = file ?: project ?: application
        val duration = Duration.between(accessedAt.accessedAt, OffsetDateTime.now()).toMinutes()

        if (settings.timeoutEnabled.getValue() && duration >= settings.timeoutMinutes.getValue())
            return false

        return true
    }

    fun render() = when (context.needsRender()) {
        true -> forceRender()
        false -> null
    }

    protected fun RenderContext.forceRender(details: LineValue?,
                                            detailsCustom: StringValue?,
                                            state: LineValue?,
                                            stateCustom: StringValue?,
                                            largeIcon: IconValue?,
                                            largeIconText: IconTextValue?,
                                            smallIcon: IconValue?,
                                            smallIconText: IconTextValue?,
                                            startTimestamp: TimeValue?): RichPresence {
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
                is PresenceTime.Result.Time -> time.value
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

inline val RenderContext.renderType
    get() = when {
        project == null -> Renderer.Type.APPLICATION
        file == null -> Renderer.Type.PROJECT
        else -> Renderer.Type.FILE
    }
