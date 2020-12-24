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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.analytics

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.dataService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.source.sourceService
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.DisposableCoroutineScope
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AnalyticsFileEditorManagerListener : FileEditorManagerListener, DisposableCoroutineScope {
    override val parentJob: Job = SupervisorJob()

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val editor = source.getSelectedEditor(file)
        val project = source.project

        launch {
            val data = dataService.getData(Renderer.Mode.NORMAL, project, editor)
            val context = RenderContext(sourceService.source, data, Renderer.Mode.NORMAL)

            with(context) {
                if (mode == Renderer.Mode.NORMAL) {
                    val iconWanted = language?.assetIds?.first()
                    val iconUsed = icons?.let { language?.findIcon(it) }?.asset?.id

                    if (language != null && theme != null && iconUsed != null && iconWanted != null) {
                        val applicationName = settings.applicationType.getStoredValue().applicationName
                        analyticsService.reportIcon(
                            language = language.id,
                            theme = theme.id,
                            applicationName = applicationName,
                            iconWanted = iconWanted,
                            iconUsed = iconUsed
                        )
                    }

                    if (fileData?.fileEditor != null && language != null) {
                        val extension = fileData.fileExtensions.maxBy(String::length)
                        if (extension != null) {
                            analyticsService.reportFile(
                                editor = fileData.fileEditor,
                                type = fileData.fileType.name,
                                extension = extension,
                                language = language.id
                            )
                        }
                    }
                }
            }
        }
    }
}
