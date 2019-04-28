package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue

typealias IconValue = SimpleValue<PresenceIcon>

enum class PresenceIcon(val description: String) {
    APPLICATION("Application") {
        override fun RenderContext.getResult() = icons?.getAsset("application").toResult()
    },
    FILE("File") {
        override fun RenderContext.getResult(): Result {
            return icons?.let { icons -> match?.findIcon(icons) }?.asset.toResult()
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
