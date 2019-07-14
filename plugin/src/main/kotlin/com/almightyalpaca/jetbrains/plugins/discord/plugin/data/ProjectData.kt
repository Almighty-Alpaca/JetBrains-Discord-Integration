package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.isReadOnly
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.map
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.UniqueNameBuilder
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.time.OffsetDateTime

class ProjectData(
    val platformProject: Project,
    val name: String,
    val openedAt: OffsetDateTime = OffsetDateTime.now(),
    files: Map<VirtualFile, FileData> = emptyMap()
) : AccessedAt {
    val files = files.toMap()

    private val uniqueNameBuilder = UniqueNameBuilder<VirtualFile>(platformProject.basePath, File.separator, 128)

    init {
        for ((file, _) in files) {
            uniqueNameBuilder.addPath(file, file.path)
        }
    }

    fun getUniqueName(file: VirtualFile): String {
        return uniqueNameBuilder.getShortPath(file);
    }

    override val accessedAt: OffsetDateTime
        get() = files.maxBy { it.value.accessedAt }?.value?.accessedAt ?: openedAt

    fun builder() = ProjectDataBuilder(platformProject, name, openedAt, files)
}

@Suppress("NAME_SHADOWING")
class ProjectDataBuilder(var platformProject: Project, var name: String, val openedAt: OffsetDateTime = OffsetDateTime.now(), files: Map<VirtualFile, FileData> = emptyMap()) {
    private val files = mutableMapOf(*files.map { (k, v) -> k to v.builder() }.toTypedArray())

    fun add(file: VirtualFile?, builder: FileDataBuilder.() -> Unit = {}) {
        if (file?.checkValid() == true)
            files.computeIfAbsent(file) { file -> FileDataBuilder(platformProject, file.filePath, file.isReadOnly) }.builder()
    }

    private fun VirtualFile?.checkValid() = this != null && !this.fileSystem.protocol.equals("dummy", true) // && file.exists()

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
