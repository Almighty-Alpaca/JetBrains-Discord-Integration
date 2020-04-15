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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ApplicationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ProjectSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.find
import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import org.apache.commons.io.FilenameUtils

sealed class Data {
    open class Application(
        val applicationId: String,
        val applicationName: String,
        val applicationVersion: String,
        val applicationStartTime: Long,
        val applicationSettings: ApplicationSettings
    ) : Data() {
    }

    open class Project(
        applicationId: String,
        applicationName: String,
        applicationVersion: String,
        applicationStartTime: Long,
        applicationSettings: ApplicationSettings,
        val projectName: String,
        val projectSettings: ProjectSettings
    ) : Application(applicationId, applicationName, applicationVersion, applicationStartTime, applicationSettings)

    open class File(
        applicationId: String,
        applicationName: String,
        applicationVersion: String,
        applicationStartTime: Long,
        applicationSettings: ApplicationSettings,
        projectName: String,
        projectSettings: ProjectSettings,
        val fileName: String,
        val fileUniqueName: String,
        val filePath: String,
        val fileIsWriteable: Boolean
    ) : Project(
        applicationId,
        applicationName,
        applicationVersion,
        applicationStartTime,
        applicationSettings,
        projectName,
        projectSettings
    ), Matcher.Target.Provider {
        /** Path relative to the project directory */
        private val filePathRelative: String by lazy {
            FilenameUtils.separatorsToUnix(
                filePath
            )
        }

        private val fileBaseNames: Collection<String> by lazy {
            fileName.find('.')
                .mapToObj { i -> fileName.substring(0, i) }
                .toSet()
        }

        private val fileExtensions: Collection<String> by lazy {
            fileName.find('.')
                .mapToObj { i -> fileName.substring(i) }
                .toSet()
        }

        override fun getField(target: Matcher.Target) = when (target) {
            Matcher.Target.EXTENSION -> fileExtensions
            Matcher.Target.NAME -> listOf(fileName)
            Matcher.Target.BASENAME -> fileBaseNames
            Matcher.Target.PATH -> listOf(filePathRelative)
        }

    }
}
