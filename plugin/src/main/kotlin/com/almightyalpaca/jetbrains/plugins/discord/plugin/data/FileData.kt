package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.filePath
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.find
import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.FieldProvider
import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.apache.commons.io.FilenameUtils
import java.nio.file.Path
import java.time.OffsetDateTime

class FileData(
    val virtualFile: VirtualFile,
    val project: Project,
    val path: Path,
    val readOnly: Boolean,
    val openedAt: OffsetDateTime = OffsetDateTime.now(),
    override val accessedAt: OffsetDateTime = openedAt
) : FieldProvider,
    AccessedAt {
    /** Path relative to the project directory */
    val relativePath: Path by lazy { project.filePath.relativize(path) }
    /** Path relative to the project directory in Unix style (aka using forward slashes) */
    val relativePathSane: String by lazy { FilenameUtils.separatorsToUnix(relativePath.toString()) }
    val name: String by lazy { path.name }
    val baseNames: Collection<String> by lazy { name.find('.').mapToObj { i -> name.substring(0, i) }.toSet() }
    val extensions: Collection<String> by lazy { name.find('.').mapToObj { i -> name.substring(i) }.toSet() }

    override fun getField(target: Matcher.Target) = when (target) {
        Matcher.Target.EXTENSION -> extensions
        Matcher.Target.NAME -> listOf(name)
        Matcher.Target.BASENAME -> baseNames
        Matcher.Target.PATH -> listOf(relativePathSane)
    }

    fun builder() = FileDataBuilder(project, path, readOnly, openedAt, accessedAt)
}

class FileDataBuilder(val project: Project, var path: Path, var readOnly: Boolean, val openedAt: OffsetDateTime = OffsetDateTime.now(), var accessedAt: OffsetDateTime = openedAt) {
    fun build(file: VirtualFile) = FileData(file, project, path, readOnly, openedAt, accessedAt)
}
