package com.almightyalpaca.jetbrains.plugins.discord.app.data

import com.almightyalpaca.jetbrains.plugins.discord.app.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.app.utils.isReadOnly
import com.almightyalpaca.jetbrains.plugins.shared.utils.map
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.time.OffsetDateTime

class ProjectData(val path: Path, val name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    val files = files.toMap()

    val accessedAt: OffsetDateTime
        get() = files.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ProjectDataBuilder(path, name, openedAt, files)
}

class ProjectDataBuilder(var path: Path, var name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    private val files = mutableMapOf(*files.map { (k, v) -> k to v.builder() }.toTypedArray())

    fun add(file: VirtualFile?, builder: FileDataBuilder.() -> Unit = {}) {
        file?.let { files.computeIfAbsent(file) { file -> FileDataBuilder(file.filePath, file.isReadOnly) }.builder() }
    }

    fun update(file: VirtualFile?, builder: FileDataBuilder.() -> Unit) {
        file?.let { files[file]?.builder() }
    }

    infix fun remove(file: VirtualFile?) {
        file?.let { files.remove(file) }
    }

    operator fun contains(file: VirtualFile?) = file != null && file in files

    fun build() = ProjectData(path, name, openedAt, files.map { file, data -> file to data.build(path) })
}
