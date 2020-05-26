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
