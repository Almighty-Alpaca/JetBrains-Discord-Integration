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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.services

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.lazyService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope

/**
 * This class was first copied from [UniqueVFilePathBuilder][com.intellij.openapi.fileEditor.impl.UniqueVFilePathBuilderImpl] and then converted to Kotlin.
 */
interface UniqueFilePathBuilderService {
    fun getUniqueVirtualFilePath(project: Project, file: VirtualFile, scope: GlobalSearchScope): String

    fun getUniqueVirtualFilePath(project: Project, vFile: VirtualFile): String

    fun getUniqueVirtualFilePathWithinOpenedFileEditors(project: Project, vFile: VirtualFile): String
}

val uniqueFilePathBuilderService: UniqueFilePathBuilderService by lazyService()
