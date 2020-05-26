package com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions.git

import com.almightyalpaca.jetbrains.plugins.discord.plugin.extensions.VcsInfoExtension
import com.intellij.dvcs.DvcsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepositoryManager

class GitVcsInfoExtension : VcsInfoExtension {
    override fun getCurrentVcsBranch(project: Project, file: VirtualFile?): String? {
        val manager = GitRepositoryManager.getInstance(project)
        return DvcsUtil.guessRepositoryForFile(project, manager, file, null)?.currentBranchName
    }
}
