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

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.nio.file.Paths

inline val application: Application
    get() = ApplicationManager.getApplication()

inline fun <reified T : BaseComponent> Project.getComponent(): T = this.getComponent(T::class.java)

inline fun <reified T : Any> Project.getService(): T = ServiceManager.getService(this, T::class.java)

inline fun <reified T : BaseComponent> lazyComponent(): Lazy<T> = lazy { application.getComponent(T::class.java) }

inline fun <reified T : Any> lazyService(): Lazy<T> = lazy { ServiceManager.getService(T::class.java) }

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline val Project.filePath: Path
    get() = Paths.get(this.basePath)

inline val VirtualFile.filePath: Path
    get() = Paths.get(this.path)
