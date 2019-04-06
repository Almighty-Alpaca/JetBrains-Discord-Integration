package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.isReadOnly
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.map
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.time.OffsetDateTime

class ProjectData(val platformProject: Project, val name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) : AccessedAt {
    val files = files.toMap()

    override val accessedAt: OffsetDateTime
        get() = files.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ProjectDataBuilder(platformProject, name, openedAt, files)
}

@Suppress("NAME_SHADOWING")
class ProjectDataBuilder(var platformProject: Project, var name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    private val files = mutableMapOf(*files.map { (k, v) -> k to v.builder() }.toTypedArray())

    fun add(file: VirtualFile?, builder: FileDataBuilder.() -> Unit = {}) {
        if (file != null && isValid(file))
            files.computeIfAbsent(file) { file -> FileDataBuilder(platformProject, file.filePath, file.isReadOnly) }.builder()
    }

    fun update(file: VirtualFile?, builder: FileDataBuilder.() -> Unit) {
        if (file != null && isValid(file))
            files[file]?.builder()
    }

    infix fun remove(file: VirtualFile?) {
        file?.let { files.remove(file) }
    }

    private fun isValid(file: VirtualFile) = Files.isRegularFile(file.filePath)

    operator fun contains(file: VirtualFile?) = file != null && file in files

    fun build() = ProjectData(platformProject, name, openedAt, files.map { file, data -> file to data.build() })
}
