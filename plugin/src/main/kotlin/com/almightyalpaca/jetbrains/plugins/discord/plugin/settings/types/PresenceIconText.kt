package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue

typealias IconTextValue = SimpleValue<PresenceIconText>

enum class PresenceIconText(val description: String) {
    APPLICATION_VERSION("Application Version") {
        override fun get(context: RenderContext) = context.application.version.toResult()
    },
    FILE_LANGUAGE("File Language") {
        override fun get(context: RenderContext) = context.match?.name.toResult()
    },
    // CUSTOM("Custom") {
    //     override fun get(context: RenderContext) = Result.Custom
    // },
    NONE("None") {
        override fun get(context: RenderContext) = Result.Empty
    };

    abstract fun get(context: RenderContext): Result

    override fun toString() = description

    object Large {
        val Application = APPLICATION_VERSION to arrayOf(APPLICATION_VERSION, NONE)
        val Project = APPLICATION_VERSION to arrayOf(APPLICATION_VERSION, NONE)
        val File = FILE_LANGUAGE to arrayOf(APPLICATION_VERSION, FILE_LANGUAGE, NONE)
    }

    object Small {
        val Application = NONE to arrayOf(APPLICATION_VERSION, NONE)
        val Project = NONE to arrayOf(APPLICATION_VERSION, NONE)
        val File = APPLICATION_VERSION to arrayOf(APPLICATION_VERSION, FILE_LANGUAGE, NONE)
    }

    fun String?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.String(this)
    }

    sealed class Result {
        object Empty : Result()
        // object Custom : Result()
        data class String(val value: kotlin.String) : Result()
    }
}
