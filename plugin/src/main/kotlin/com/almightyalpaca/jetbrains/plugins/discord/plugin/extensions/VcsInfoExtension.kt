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

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlin.streams.asSequence

interface VcsInfoExtension {
    fun getCurrentVcsBranch(project: Project, file: VirtualFile?): String?

    companion object {
        private val EXTENSION_POINT_NAME = ExtensionPointName.create<VcsInfoExtension>("com.almightyalpaca.intellij.plugins.discord.vcsInfo")

        fun getCurrentVcsBranch(project: Project, file: VirtualFile?): String? =
            EXTENSION_POINT_NAME
                .extensions()
                .asSequence()
                .map { it.getCurrentVcsBranch(project, file) }
                .filterNotNull()
                .firstOrNull()
    }
}
