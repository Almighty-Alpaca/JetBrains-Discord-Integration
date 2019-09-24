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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.richpresence.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.components.applicationComponent
import com.almightyalpaca.jetbrains.plugins.discord.plugin.keys.accessedAt
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.application
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.fields
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Source
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile

class RenderContext(source: Source, val project: Project?, val file: VirtualFile?, val mode: Renderer.Mode) {
    val theme: Theme? = source.getThemesOrNull()?.get(settings.theme.getValue())
    val icons: IconSet? = theme?.getIconSet(settings.applicationType.getValue().applicationName)

    val accessedAt: Long = application.accessedAt

    val match by lazy { file?.let { source.getLanguagesOrNull()?.findLanguage(file.fields) } }

    fun <T> SimpleValue<T>.getValue(): T = when (mode) {
        Renderer.Mode.NORMAL -> get()
        Renderer.Mode.PREVIEW -> getComponent()
    }

    fun createRenderer(): Renderer = renderType.createRenderer(this)
}

fun RenderContext(mode: Renderer.Mode): RenderContext {
    val projectManager = ProjectManager.getInstance()

    var selectedProject: Project? = null
    var selectedFile: VirtualFile? = null
    for (project in projectManager.openProjects) {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val selectedEditor = fileEditorManager.selectedEditor

        if (selectedEditor != null) {
            selectedProject = project
            selectedFile = selectedEditor.file
            break
        }
    }

    return RenderContext(applicationComponent.source, selectedProject, selectedFile, mode)
}
