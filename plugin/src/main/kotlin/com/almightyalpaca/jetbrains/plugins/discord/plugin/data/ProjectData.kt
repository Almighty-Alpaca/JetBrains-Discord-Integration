package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.isReadOnly
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.map
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime

class ProjectData(val path: Path, val name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    val files = files.toMap()

    val accessedAt: OffsetDateTime
        get() = files.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ProjectDataBuilder(path, name, openedAt, files)
}

@Suppress("NAME_SHADOWING")
class ProjectDataBuilder(var path: Path, var name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    private val files = mutableMapOf(*files.map { (k, v) -> k to v.builder() }.toTypedArray())

    fun add(file: VirtualFile?, builder: FileDataBuilder.() -> Unit = {}) {
        if (file != null && isValid(file))
            files.computeIfAbsent(file) { file -> FileDataBuilder(file.filePath, file.isReadOnly) }.builder()
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

    fun build() = ProjectData(path, name, openedAt, files.map { file, data -> file to data.build(path) })
}
