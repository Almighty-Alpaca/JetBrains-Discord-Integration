/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.services.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.services.UniqueFilePathBuilderService
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.io.UniqueNameBuilder
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFilePathWrapper
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.ConcurrencyUtil
import com.intellij.util.containers.ContainerUtil
import gnu.trove.THashSet
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * This class was first copied from [UniqueVFilePathBuilderImpl][com.intellij.openapi.fileEditor.impl.UniqueVFilePathBuilderImpl] and then converted to Kotlin.
 */
class UniqueFilePathBuilderServiceImpl : UniqueFilePathBuilderService {
    override fun getUniqueVirtualFilePath(project: Project, file: VirtualFile, scope: GlobalSearchScope): String {
        return getUniqueVirtualFilePath(project, file, false, scope)
    }

    override fun getUniqueVirtualFilePath(project: Project, vFile: VirtualFile): String {
        return getUniqueVirtualFilePath(project, vFile, GlobalSearchScope.projectScope(project))
    }

    override fun getUniqueVirtualFilePathWithinOpenedFileEditors(project: Project, vFile: VirtualFile): String {
        return getUniqueVirtualFilePath(project, vFile, true, GlobalSearchScope.projectScope(project))
    }

    companion object {
        private val ourShortNameBuilderCacheKey = Key.create<CachedValue<Map<GlobalSearchScope, Map<String, UniqueNameBuilder<VirtualFile>>>>>("project's.short.file.name.builder")
        private val ourShortNameOpenedBuilderCacheKey = Key.create<CachedValue<Map<GlobalSearchScope, Map<String, UniqueNameBuilder<VirtualFile>>>>>("project's.short.file.name.opened.builder")
        private val ourEmptyBuilder = UniqueNameBuilder<VirtualFile>(null, null, -1)

        private fun getUniqueVirtualFilePath(project: Project, file: VirtualFile, skipNonOpenedFiles: Boolean, scope: GlobalSearchScope): String {
            val key = if (skipNonOpenedFiles) ourShortNameOpenedBuilderCacheKey else ourShortNameBuilderCacheKey
            var data = project.getUserData(key)
            if (data == null) {
                val field = FileEditorManagerImpl::class.java.getDeclaredField("OPEN_FILE_SET_MODIFICATION_COUNT")
                field.isAccessible = true
                val modificationTracker = field.get(null) as ModificationTracker

                data = CachedValuesManager.getManager(project).createCachedValue(
                    {
                        CachedValueProvider.Result<Map<GlobalSearchScope, Map<String, UniqueNameBuilder<VirtualFile>>>>(
                            ConcurrentHashMap(2),
                            PsiModificationTracker.MODIFICATION_COUNT,
                            //ProjectRootModificationTracker.getInstance(project),
                            //VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS,
                            modificationTracker
                        )
                    }, false
                )

                project.putUserData(key, data)
            }

            val scope2ValueMap = data.value as ConcurrentMap<GlobalSearchScope, Map<String, UniqueNameBuilder<VirtualFile>>>
            var valueMap: MutableMap<String, UniqueNameBuilder<VirtualFile>>? = scope2ValueMap[scope] as MutableMap<String, UniqueNameBuilder<VirtualFile>>?
            if (valueMap == null) {
                valueMap = ConcurrencyUtil.cacheOrGet(scope2ValueMap, scope, ContainerUtil.createConcurrentSoftValueMap()) as MutableMap<String, UniqueNameBuilder<VirtualFile>>
            }

            val fileName = file.name
            var uniqueNameBuilderForShortName: UniqueNameBuilder<VirtualFile>? = valueMap[fileName]

            if (uniqueNameBuilderForShortName == null) {
                val builder = filesWithTheSameName(fileName, project, skipNonOpenedFiles, scope)
                valueMap[fileName] = builder ?: ourEmptyBuilder
                uniqueNameBuilderForShortName = builder
            } else if (uniqueNameBuilderForShortName === ourEmptyBuilder) {
                uniqueNameBuilderForShortName = null
            }

            if (uniqueNameBuilderForShortName != null && uniqueNameBuilderForShortName.contains(file)) {
                return uniqueNameBuilderForShortName.getShortPath(file)
            }

            return if (file is VirtualFilePathWrapper) file.presentableName else file.name
        }

        private fun filesWithTheSameName(fileName: String, project: Project, skipNonOpenedFiles: Boolean, scope: GlobalSearchScope): UniqueNameBuilder<VirtualFile>? {
            var filesWithSameName = if (skipNonOpenedFiles) emptySet() else FilenameIndex.getVirtualFilesByName(project, fileName, scope)
            val setOfFilesWithTheSameName = THashSet(filesWithSameName)
            // add open files out of project scope
            for (openFile in FileEditorManager.getInstance(project).openFiles) {
                if (openFile.name == fileName) {
                    setOfFilesWithTheSameName.add(openFile)
                }
            }
            if (!skipNonOpenedFiles) {
                for (recentlyEditedFile in EditorHistoryManager.getInstance(project).fileList) {
                    if (recentlyEditedFile.name == fileName) {
                        setOfFilesWithTheSameName.add(recentlyEditedFile)
                    }
                }
            }

            filesWithSameName = setOfFilesWithTheSameName

            if (filesWithSameName.size > 1) {
                var path = project.basePath
                path = if (path == null) "" else FileUtil.toSystemIndependentName(path)
                val builder = UniqueNameBuilder<VirtualFile>(path, File.separator, 120)
                for (virtualFile in filesWithSameName) {
                    val presentablePath = if (virtualFile is VirtualFilePathWrapper)
                        (virtualFile as VirtualFilePathWrapper).presentablePath
                    else
                        virtualFile.path
                    builder.addPath(virtualFile, presentablePath)
                }
                return builder
            }

            return null
        }
    }
}
