package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import java.time.OffsetDateTime

typealias TimeValue = SimpleValue<PresenceTime>

enum class PresenceTime(val description: String) {
    APPLICATION("Application") {
        override fun get(context: RenderContext) = context.application.openedAt.toResult()
    },
    PROJECT("Project") {
        override fun get(context: RenderContext) = context.project?.openedAt.toResult()
    },
    FILE("File") {
        override fun get(context: RenderContext) = context.file?.openedAt.toResult()
    },
    HIDE("Hide") {
        override fun get(context: RenderContext) = Result.Empty
    };

    abstract fun get(context: RenderContext): Result

    override fun toString() = description

    companion object {
        val Application = APPLICATION to arrayOf(APPLICATION, HIDE)
        val Project = APPLICATION to arrayOf(APPLICATION, PROJECT, HIDE)
        val File = APPLICATION to arrayOf(APPLICATION, PROJECT, FILE, HIDE)
    }

    fun OffsetDateTime?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.Time(this)
    }

    sealed class Result {
        object Empty : Result()
        data class Time(val value: OffsetDateTime) : Result()
    }
}