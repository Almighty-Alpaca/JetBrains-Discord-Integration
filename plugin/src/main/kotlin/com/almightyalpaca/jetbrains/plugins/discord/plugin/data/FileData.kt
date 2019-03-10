package com.almightyalpaca.jetbrains.plugins.discord.plugin.data

import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.find
import com.almightyalpaca.jetbrains.plugins.shared.languages.FieldProvider
import com.almightyalpaca.jetbrains.plugins.shared.languages.matchers.Matcher
import com.almightyalpaca.jetbrains.plugins.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.shared.utils.toSet
import org.apache.commons.io.FilenameUtils
import java.nio.file.Path
import java.time.OffsetDateTime

class FileData(projectPath: Path, val path: Path, var readOnly: Boolean, val openedAt: OffsetDateTime = OffsetDateTime.now(), val accessedAt: OffsetDateTime = openedAt) {
    val relativePath: Path by lazy { projectPath.relativize(path) }
    val relativePathSane: String by lazy { FilenameUtils.separatorsToUnix(relativePath.toString()) }
    val name: String by lazy { path.name }
    val baseNames: Collection<String> by lazy { name.find('.').mapToObj { i -> name.substring(0, i) }.toSet() }
    val extensions: Collection<String> by lazy { name.find('.').mapToObj { i -> name.substring(i) }.toSet() }

    fun builder() = FileDataBuilder(path, readOnly, openedAt, accessedAt)
}

class FileDataBuilder(var path: Path, var readOnly: Boolean, val openedAt: OffsetDateTime = OffsetDateTime.now(), var accessedAt: OffsetDateTime = openedAt) {
    fun build(projectPath: Path) = FileData(projectPath, path, readOnly, openedAt, accessedAt)
}

val FileData.fieldProvider: FieldProvider
    get() = { target ->
        when (target) {
            Matcher.Target.EXTENSION -> extensions
            Matcher.Target.NAME -> listOf(name)
            Matcher.Target.BASENAME -> baseNames
            Matcher.Target.PATH -> listOf(relativePathSane)
            Matcher.Target.CONTENT -> listOf() // TODO: first line/magic bytes
        }
    }
