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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions.git

import com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions.VcsInfoExtension
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepositoryManager

class GitVcsInfoExtension : VcsInfoExtension {
    override fun getCurrentVcsBranch(project: Project, file: VirtualFile?): String? = runReadAction action@{
        val manager = GitRepositoryManager.getInstance(project)
        return@action DvcsUtil.guessRepositoryForFile(project, manager, file, null)?.currentBranchName
    }
}
