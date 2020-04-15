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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.Data
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ToolTipProvider
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

typealias LineValue = SimpleValue<PresenceLine>

enum class PresenceLine(val description: String, override val toolTip: String? = null) : ToolTipProvider {
    NONE("Empty") {
        override fun RenderContext.getResult(): Result = Result.Empty
    },
    PROJECT_DESCRIPTION("Project Description") {
        override fun RenderContext.getResult(): Result = projectData?.projectSettings?.description?.getValue().toResult()
    },
    PROJECT_NAME("Project Name") {
        override fun RenderContext.getResult(): Result {
            val settings = projectData?.projectSettings

            return when (settings?.nameOverrideEnabled?.getValue()) {
                true -> settings.nameOverrideText.getValue()
                else -> projectData?.projectName
            }.toResult()
        }
    },
    PROJECT_NAME_DESCRIPTION("Project Name - Description") {
        override fun RenderContext.getResult(): Result {
            val project = projectData ?: return Result.Empty

            val settings = project.projectSettings
            val name = when (settings.nameOverrideEnabled.getValue()) {
                true -> settings.nameOverrideText.getValue()
                else -> project.projectName
            }

            return when (val description = project.projectSettings.description.getValue()) {
                "" -> name
                else -> "$name - $description"
            }.toResult()
        }
    },
    FILE_NAME_PATH("File Name (+ Path)", "Additionally shows part of the path when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = fileData?.let { getPrefix(fileData) + fileData.fileUniqueName }.toResult()
    },
    FILE_NAME("File Name", "Only shows the file name even when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = fileData?.let { getPrefix(fileData) + fileData.fileName }.toResult()
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

    fun String?.toResult() = when {
        this == null || trim().isBlank() -> Result.Empty
        else -> Result.String(trim())
    }

    sealed class Result {
        object Empty : Result()
        object Custom : Result()
        data class String(val value: kotlin.String) : Result()
    }
}

private fun RenderContext.getPrefix(file: Data.File): String {
    return when (settings.filePrefixEnabled.getValue()) {
        true -> when (file.fileIsWriteable) {
            true -> "Editing "
            false -> "Reading "
        }
        false -> ""
    }
}
