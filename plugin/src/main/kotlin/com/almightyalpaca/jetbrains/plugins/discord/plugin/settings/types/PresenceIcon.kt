package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue

typealias IconValue = SimpleValue<PresenceIcon>

enum class PresenceIcon(val description: String) {
    APPLICATION("Application") {
        private val result = "application".toResult()
        override fun get(context: RenderContext) = result
    },
    FILE("File") {
        override fun get(context: RenderContext): Result {
            return context.match?.findIcon(context.icons)?.asset.toResult()
        }
    },
    NONE("None") {
        override fun get(context: RenderContext) = Result.Empty
    };

    abstract fun get(context: RenderContext): Result

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

    fun String?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.String(this)
    }

    sealed class Result {
        object Empty : Result()
        data class String(val value: kotlin.String) : Result()
    }
}