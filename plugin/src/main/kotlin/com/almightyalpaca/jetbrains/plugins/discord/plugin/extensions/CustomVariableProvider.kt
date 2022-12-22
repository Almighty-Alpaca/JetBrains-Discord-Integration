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
package com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.CustomVariableData
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

interface CustomVariableProvider {
    fun forApplication(variableData: CustomVariableData) {}
    fun forProject(variableData: CustomVariableData, project: Project) {}
    fun forFile(variableData: CustomVariableData, editor: FileEditor, file: VirtualFile) {}

    companion object {
        private val EXTENSION_POINT_NAME = ExtensionPointName.create<CustomVariableProvider>("com.almightyalpaca.intellij.plugins.discord.customVariableProvider")

        fun forApplication(variableData: CustomVariableData) {
            EXTENSION_POINT_NAME.extensions.forEach { it.forApplication(variableData) }
        }

        fun forProject(variableData: CustomVariableData, project: Project) {
            EXTENSION_POINT_NAME.extensions.forEach { it.forProject(variableData, project) }
        }

        fun forFile(variableData: CustomVariableData, editor: FileEditor, file: VirtualFile) {
            EXTENSION_POINT_NAME.extensions.forEach { it.forFile(variableData, editor, file) }
        }
    }
}
