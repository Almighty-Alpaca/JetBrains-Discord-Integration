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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.DiscordPlugin
import com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions.VcsInfoExtension
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ApplicationType
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeActive
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeOpened
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.invokeSuspend
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.isVcsIgnored
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.tryOrDefault
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.tryOrNull
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.EditorTabPresentationUtil
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager

val dataService: DataService
    get() = service()

@Service
class DataService {
    suspend fun getData(mode: Renderer.Mode) = mode.run { getData() }

    @JvmName("getDataInternal")
    private suspend fun (Renderer.Mode).getData(): Data? = tryOrNull {
        DiscordPlugin.LOG.debug("Getting data")

        val project: Project?
        val editor: FileEditor?

        val window = IdeFocusManager.getGlobalInstance().lastFocusedFrame

        project = window?.project

        editor = project?.let { invokeSuspend { FileEditorManager.getInstance(project)?.selectedEditor } }

        val application = ApplicationManager.getApplication()
        val applicationInfo = ApplicationInfoEx.getInstance()

        val applicationName = settings.applicationType.getValue().applicationNameReadable
        val applicationVersion = applicationInfo.fullVersion
        val applicationTimeOpened = application.timeOpened
        val applicationTimeActive = application.timeActive
        val applicationSettings = settings

        if (project != null && !project.isDefault && project.settings.show.getValue().showProject) {
            val projectName = project.name
            val projectDescription = project.settings.description.getValue()
            val projectTimeOpened = project.timeOpened
            val projectTimeActive = project.timeActive
            val projectSettings = project.settings

            if (editor != null) {
                val file = editor.file

                if (file != null
                    && project.settings.show.getValue().showFiles
                    && !(settings.fileHideVcsIgnored.getValue() && isVcsIgnored(project, file))
                ) {
                    val fileName = file.name
                    val fileUniqueName = when (DumbService.isDumb(project)) {
                        true -> fileName
                        false -> ReadAction.compute<String, Exception> {
                            tryOrDefault(fileName) {
                                if (!project.isDisposed) {
                                    EditorTabPresentationUtil.getUniqueEditorTabTitle(project, file, null)
                                } else {
                                    fileName
                                }
                            }
                        }
                    }
                    val fileTimeOpened = file.timeOpened
                    val fileTimeActive = file.timeActive
                    val filePath = file.path
                    val fileIsWriteable = file.isWritable

                    val vcsBranch = VcsInfoExtension.getCurrentVcsBranch(project, file)

                    DiscordPlugin.LOG.debug("Returning file data")

                    return Data.File(
                        applicationName,
                        applicationVersion,
                        applicationTimeOpened,
                        applicationTimeActive,
                        applicationSettings,
                        projectName,
                        projectDescription,
                        projectTimeOpened,
                        applicationTimeActive,
                        projectSettings,
                        vcsBranch,
                        fileName,
                        fileUniqueName,
                        fileTimeOpened,
                        fileTimeActive,
                        filePath,
                        fileIsWriteable
                    )
                }
            }

            val vcsBranch = VcsInfoExtension.getCurrentVcsBranch(project, null)

            DiscordPlugin.LOG.debug("Returning project data")

            return Data.Project(
                applicationName,
                applicationVersion,
                applicationTimeOpened,
                applicationTimeActive,
                applicationSettings,
                projectName,
                projectDescription,
                projectTimeOpened,
                projectTimeActive,
                projectSettings,
                vcsBranch
            )
        }

        DiscordPlugin.LOG.debug("Returning application data")

        return Data.Application(
            applicationName,
            applicationVersion,
            applicationTimeOpened,
            applicationTimeActive,
            applicationSettings
        )
    }
}
