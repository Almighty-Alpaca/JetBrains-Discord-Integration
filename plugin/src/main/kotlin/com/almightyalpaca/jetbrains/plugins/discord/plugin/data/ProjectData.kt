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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.maxNullable
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.map
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.time.OffsetDateTime

class ProjectData(
    val platformProject: Project,
    val name: String,
    val openedAt: OffsetDateTime = OffsetDateTime.now(),
    files: Map<VirtualFile, FileData> = emptyMap()
) : AccessedAt {
    val files = files.toMap()

    override val accessedAt: OffsetDateTime
        get() = files.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder(applicationBuilder: ApplicationDataBuilder) = ProjectDataBuilder(applicationBuilder, platformProject, name, openedAt, files)
}

@Suppress("NAME_SHADOWING")
class ProjectDataBuilder(
    val applicationBuilder: ApplicationDataBuilder,
    var platformProject: Project,
    var name: String,
    openedAt: OffsetDateTime = OffsetDateTime.now(),
    files: Map<VirtualFile, FileData> = emptyMap()
) {
    private val files = mutableMapOf(*files.map { (k, v) -> k to v.builder(this) }.toTypedArray())

    var openedAt = openedAt
        set(value) {
            field = value
            files.values.forEach { f ->
                if (f.openedAt.isBefore(value)) {
                    f.openedAt = value
                }
            }
        }

    val accessedAt
        get() = maxNullable(files.values.asSequence()
            .map { f -> f.accessedAt }
            .max(), openedAt)

    fun add(file: VirtualFile?, builder: FileDataBuilder.() -> Unit = {}) {
        if (file?.checkValid() == true)
            files.computeIfAbsent(file) { file -> FileDataBuilder(this, platformProject) }.builder()
    }

    private fun VirtualFile?.checkValid() = this != null && !this.fileSystem.protocol.equals("dummy", true)

    fun update(file: VirtualFile?, builder: FileDataBuilder.() -> Unit) {
        if (file?.checkValid() == true)
            files[file]?.builder()
    }

    infix fun remove(file: VirtualFile?) {
        file.let { files.remove(file) }
    }

    operator fun contains(file: VirtualFile?) = file != null && file in files

    fun build() = ProjectData(platformProject, name, openedAt, files.map { file, data -> file to data.build(file) })
}
