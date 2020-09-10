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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType

typealias TimeValue = SimpleValue<PresenceTime>

enum class PresenceTime(override val text: String, override val description: String? = null) : RenderedValue<PresenceTime.Result>, UiValueType {
    APPLICATION("Application (active)", "Time since the application has been opened and the IDE has not been idle") {
        override fun RenderContext.getResult() = applicationData?.applicationTimeActive.toResult()
    },
    APPLICATION_TOTAL("Application (total)", "Time since the application has been started") {
        override fun RenderContext.getResult() = applicationData?.applicationTimeOpened.toResult()
    },
    PROJECT("Project (active)", "Time since the project has been opened and the IDE has not been idle") {
        override fun RenderContext.getResult() = projectData?.projectTimeActive.toResult()
    },
    PROJECT_TOTAL("Project (total)", "Time since the project has been opened") {
        override fun RenderContext.getResult() = projectData?.projectTimeOpened.toResult()
    },
    FILE("File (active)", "Time since the file has been opened and the IDE has not been idle") {
        override fun RenderContext.getResult() = fileData?.fileTimeActive.toResult()
    },
    FILE_TOTAL("File (total)", "Time since the file has been opened") {
        override fun RenderContext.getResult() = fileData?.fileTimeOpened.toResult()
    },
    HIDE("Hide") {
        override fun RenderContext.getResult() = Result.Empty
    };

    companion object {
        val Application = APPLICATION to arrayOf(APPLICATION, APPLICATION_TOTAL, HIDE)
        val Project = APPLICATION to arrayOf(APPLICATION, APPLICATION_TOTAL, PROJECT, PROJECT_TOTAL, HIDE)
        val File = APPLICATION to arrayOf(APPLICATION, APPLICATION_TOTAL, PROJECT, PROJECT_TOTAL, FILE, FILE_TOTAL, HIDE)
    }

    fun Long?.toResult() = when (this) {
        null -> Result.Empty
        else -> Result.Time(this)
    }

    sealed class Result {
        object Empty : Result()
        data class Time(val value: Long) : Result()
    }
}
