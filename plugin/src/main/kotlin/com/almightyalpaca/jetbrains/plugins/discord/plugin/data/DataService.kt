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
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ProjectShow
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeActive
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeOpened
import com.almightyalpaca.jetbrains.plugins.discord.plugin.time.timeService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.EditorTabPresentationUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.xdebugger.XDebuggerManager

val dataService: DataService
    get() = service()

@Service
class DataService {
    suspend fun getData(mode: Renderer.Mode) = tryOrNull { mode.run { getData() } }

    @JvmName("getDataInternal")
    private suspend fun (Renderer.Mode).getData(): Data {
        DiscordPlugin.LOG.debug("Getting data")

        val applicationSettings = settings

        if (!settings.show.getStoredValue()) {
            return Data.None
        } else if (timeService.idle && settings.timeoutEnabled.getStoredValue()) {
            return Data.None
        }

        val application = ApplicationManager.getApplication()
        val applicationInfo = ApplicationInfoEx.getInstance()
        val applicationName = settings.applicationType.getValue().applicationNameReadable
        val applicationVersion = applicationInfo.fullVersion
        val applicationTimeOpened = application.timeOpened
        val applicationTimeActive = application.timeActive

        val project: Project?
        val editor: FileEditor?

        val window = IdeFocusManager.getGlobalInstance().lastFocusedFrame

        project = window?.project

        editor = project?.let { invokeOnEventThread { FileEditorManager.getInstance(project)?.selectedEditor } }

        if (project != null) {
            if (project.settings.show.getValue() <= ProjectShow.DISABLE) {
                return Data.None
            } else if (!project.isDefault && project.settings.show.getValue() >= ProjectShow.PROJECT) {
                val projectName = project.name
                val projectDescription = project.settings.description.getValue()
                val projectTimeOpened = project.timeOpened
                val projectTimeActive = project.timeActive
                val projectSettings = project.settings
                val debuggerActive: Boolean = XDebuggerManager.getInstance(project).currentSession != null

                if (editor != null) {
                    val file = editor.file

                    if (file != null
                        && project.settings.show.getValue() >= ProjectShow.PROJECT_FILES
                        && !(settings.fileHideVcsIgnored.getValue() && isVcsIgnored(project, file))
                    ) {
                        val fileName = file.name
                        val fileUniqueName = when (DumbService.isDumb(project)) {
                            true -> fileName
                            false -> invokeReadAction {
                                tryOrDefault(fileName) {
                                    EditorTabPresentationUtil.getUniqueEditorTabTitle(project, file, null)
                                }
                            }
                        }

                        val fileTimeOpened = file.timeOpened
                        val fileTimeActive = file.timeActive
                        val filePath = file.path
                        val fileIsWriteable = file.isWritable
                        val editorIsTextEditor: Boolean
                        val caretLine: Int
                        val lineCount: Int
                        val fileSize: Int

                        if (editor is TextEditor) {
                            editorIsTextEditor = true
                            caretLine = editor.editor.caretModel.primaryCaret.logicalPosition.line + 1
                            lineCount = editor.editor.document.lineCount
                            fileSize = editor.editor.document.textLength
                        } else {
                            editorIsTextEditor = false
                            caretLine = 0
                            lineCount = 0
                            fileSize = 0
                        }

                        data class ModuleData(val moduleName: String?, val pathInModule: String)

                        val moduleData = runReadAction action@{
                            val module = ModuleUtil.findModuleForFile(file, project)
                            val moduleName = module?.name
                            val moduleDirPath = module?.guessModuleDir()
                            val pathInModule = if (moduleDirPath != null) file.path.removePrefix(moduleDirPath.path) else ""
                            return@action ModuleData(moduleName, pathInModule)
                        }

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
                            debuggerActive,
                            fileName,
                            fileUniqueName,
                            fileTimeOpened,
                            fileTimeActive,
                            filePath,
                            fileIsWriteable,
                            editorIsTextEditor,
                            caretLine,
                            lineCount,
                            moduleData.moduleName,
                            moduleData.pathInModule,
                            fileSize
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
                    vcsBranch,
                    debuggerActive
                )
            }
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
