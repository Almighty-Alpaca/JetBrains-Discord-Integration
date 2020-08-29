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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.Data
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings

typealias TextValue = SimpleValue<PresenceText>

enum class PresenceText(override val text: String, override val description: String? = null) : RenderedValue<PresenceText.Result>, UiValueType {
    NONE("Empty") {
        override fun RenderContext.getResult(): Result = Result.Empty
    },
    APPLICATION_NAME("Application Name") {
        override fun RenderContext.getResult() = applicationData?.applicationName.toResult()
    },
    APPLICATION_VERSION("Application Version") {
        override fun RenderContext.getResult() = applicationData?.applicationVersion.toResult()
    },
    PROJECT_DESCRIPTION("Project Description") {
        override fun RenderContext.getResult(): Result = projectData?.projectDescription.toResult()
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

            val description = project.projectDescription
            return when {
                description.isBlank() -> name
                else -> "$name - $description"
            }.toResult()
        }
    },
    PROJECT_VCS_BRANCH("Current VCS Branch", "The Git plugin needs to be enabled for this option") {
        override fun RenderContext.getResult(): Result = projectData?.vcsBranch.toResult()
    },
    PROJECT_NAME_VCS_BRANCH("Project Name - VCS branch") {
        override fun RenderContext.getResult(): Result {
            val project = projectData ?: return Result.Empty
            val settings = project.projectSettings

            val name = when (settings.nameOverrideEnabled.getValue()) {
                true -> settings.nameOverrideText.getValue()
                else -> project.projectName
            }

            val vcsBranch = project.vcsBranch
            return when {
                vcsBranch.isNullOrBlank() -> name
                else -> "$name - $vcsBranch"
            }.toResult()
        }
    },
    FILE_NAME_PATH("File Name (unique)", "Additionally shows part of the path when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = fileData?.let { getPrefix(fileData) + fileData.fileNameUnique }.toResult()
    },
    FILE_NAME("File Name", "Only shows the file name even when there are multiple open files with the same name") {
        override fun RenderContext.getResult() = fileData?.let { getPrefix(fileData) + fileData.fileName }.toResult()
    },
    FILE_LANGUAGE("File Language") {
        override fun RenderContext.getResult() = language?.name.toResult()
    },
    CUSTOM("Custom") {
        override fun RenderContext.getResult() = Result.Custom
    };

    companion object {
        val Application1 = NONE to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME, CUSTOM)
        val Application2 = NONE to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME,  CUSTOM)
        val ApplicationIconLarge = APPLICATION_VERSION to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME, CUSTOM)
        val ApplicationIconSmall = NONE to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME,  CUSTOM)
        val Project1 = PROJECT_NAME to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, CUSTOM)
        val Project2 = PROJECT_DESCRIPTION to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, CUSTOM)
        val ProjectIconLarge = APPLICATION_VERSION to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, CUSTOM)
        val ProjectIconSmall = NONE to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, CUSTOM)
        val File1 = PROJECT_NAME_DESCRIPTION to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME,  PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, FILE_NAME_PATH, FILE_NAME, FILE_LANGUAGE, CUSTOM)
        val File2 = FILE_NAME_PATH to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, FILE_NAME_PATH, FILE_NAME, FILE_LANGUAGE, CUSTOM)
        val FileIconLarge = FILE_LANGUAGE to arrayOf(NONE, APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, FILE_NAME_PATH, FILE_NAME, FILE_LANGUAGE, CUSTOM)
        val FileIconSmall = APPLICATION_VERSION to arrayOf(NONE,  APPLICATION_VERSION, APPLICATION_NAME, PROJECT_DESCRIPTION, PROJECT_NAME, PROJECT_NAME_DESCRIPTION, PROJECT_VCS_BRANCH, PROJECT_NAME_VCS_BRANCH, FILE_NAME_PATH, FILE_NAME, FILE_LANGUAGE, CUSTOM)
    }

    fun String?.toResult() = when {
        this == null || isBlank() -> Result.Empty
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
