package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

inline val application: Application
    get() = ApplicationManager.getApplication()

fun <T : BaseComponent> getComponent(interfaceClass: KClass<T>): T = application.getComponent(interfaceClass.java)

fun <T : Any> getService(interfaceClass: KClass<T>): T = ServiceManager.getService(interfaceClass.java)

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline val Project.filePath: Path
    get() = Paths.get(this.basePath)

inline val VirtualFile.filePath: Path
    get() = Paths.get(this.path)

inline val VirtualFile.isReadOnly
    get() = !isWritable
