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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.impl.EditorTabPresentationUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import org.apache.commons.io.FilenameUtils

val VirtualFile.fields: Matcher.Target.Provider
    get() = object : Matcher.Target.Provider {
        override fun getField(target: Matcher.Target) = when (target) {
            Matcher.Target.EXTENSION -> name.find('.').mapToObj { i -> name.substring(i) }.toSet()
            Matcher.Target.NAME -> listOf(name)
            Matcher.Target.BASENAME -> name.find('.').mapToObj { i -> name.substring(0, i) }.toSet()
            Matcher.Target.PATH -> listOf(FilenameUtils.separatorsToUnix(path))
        }
    }

fun VirtualFile.getUniqueName(project: Project) = ApplicationManager.getApplication().runReadAction(Computable {
    when (Disposer.isDisposed(project)) {
        true -> null
        false -> EditorTabPresentationUtil.getUniqueEditorTabTitle(project, this, null)
    }
})
