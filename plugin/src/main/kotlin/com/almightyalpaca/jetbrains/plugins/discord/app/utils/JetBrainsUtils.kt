package com.almightyalpaca.jetbrains.plugins.discord.app.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.nio.file.Paths

object Application {
    inline val instance: Application
        get() = ApplicationManager.getApplication()
}

inline val Project.filePath: Path
    get() = Paths.get(this.basePath)

inline val VirtualFile.filePath: Path
    get() = Paths.get(this.path)

inline val VirtualFile.isReadOnly
    get() = !isWritable
