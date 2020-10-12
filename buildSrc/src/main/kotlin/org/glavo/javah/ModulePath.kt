package org.glavo.javah

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class ModulePath(path: Path) : SearchPath {
    private val path: Path = path.toAbsolutePath()

    private var roots = if (Files.notExists(path) || !Files.isDirectory(path)) {
        emptyList()
    } else {
        try {
            Files.list(path)
                .asSequence()
                .map { obj -> obj.toAbsolutePath() }
                .filter { path -> Files.isRegularFile(path) }
                .filter { p ->
                    val n = p.fileName.toString().toLowerCase()
                    n.endsWith(".jar") || n.endsWith(".zip") || n.endsWith(".jmod")
                }
                .map(Utils::classPathRoot)
                .filterNotNull()
                .flatMap { p -> SearchPath.multiReleaseRoots(p).asSequence() }
                .toList()
        } catch (e: IOException) {
            emptyList()
        }
    }

    override fun search(name: ClassName): Path? = SearchPath.searchFromRoots(roots, name)

    override fun toString(): String = "ModulePath[$path]"
}
