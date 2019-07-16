package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ToolTipProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

typealias LineValue = SimpleValue<PresenceLine>

enum class PresenceLine(val description: String, override val toolTip: String? = null) : ToolTipProvider {
    NONE("Empty") {
        override fun RenderContext.getResult(): Result = Result.Empty
    },
    PROJECT_DESCRIPTION("Project Description") {
        override fun RenderContext.getResult(): Result = project?.platformProject?.settings?.description?.getValue().toResult()
    },
    PROJECT_NAME("Project Name") {
        override fun RenderContext.getResult(): Result {
            val settings = project?.platformProject?.settings

            return when (settings?.nameOverrideEnabled?.getValue()) {
                true -> settings.nameOverrideText.getValue()
                else -> project?.name
            }.toResult()
        }
    },
    PROJECT_NAME_DESCRIPTION("Project Name - Description") {
        override fun RenderContext.getResult(): Result {
            val project = project ?: return Result.Empty

            val settings = project.platformProject.settings
            val name = when (settings.nameOverrideEnabled.getValue()) {
                true -> settings.nameOverrideText.getValue()
                else -> project.name
            }

            return when (val description = project.platformProject.settings.description.getValue()) {
                "" -> name
                else -> "$name - $description"
            }.toResult()
        }
    },
    FILE_NAME_PATH("File Name (+ Path)", "Additionally shows part of the path when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = file?.let { file -> project?.getUniqueName(file.virtualFile) }.toResult()
    },
    FILE_NAME("File Name", "Only shows the file name even when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = file?.virtualFile?.name.toResult()
    },
    CUSTOM("Custom") {
        override fun RenderContext.getResult() = Result.Custom
    };

    protected abstract fun RenderContext.getResult(): Result

    fun get(context: RenderContext) = context.run { getResult() }

    override fun toString() = description

    companion object {
        val Application1 = NONE to arrayOf(NONE, CUSTOM)
        val Application2 = NONE to arrayOf(NONE, CUSTOM)
        val Project1 = PROJECT_NAME to arrayOf(NONE, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, CUSTOM)
        val Project2 = PROJECT_DESCRIPTION to arrayOf(NONE, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, CUSTOM)
        val File1 = PROJECT_NAME_DESCRIPTION to arrayOf(NONE, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, FILE_NAME_PATH, FILE_NAME, CUSTOM)
        val File2 = FILE_NAME_PATH to arrayOf(NONE, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, FILE_NAME_PATH, FILE_NAME, CUSTOM)
    }

    fun String?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.String(this)
    }

    sealed class Result {
        object Empty : Result()
        object Custom : Result()
        data class String(val value: kotlin.String) : Result()
    }
}
