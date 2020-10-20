package org.glavo.javah

import org.glavo.javah.Utils.classPathRoot
import java.nio.file.Path

class ClassPath(path: Path) : SearchPath {
    private val path: Path = path.toAbsolutePath()
    private val roots: List<Path> = classPathRoot(path).let { root -> if (root == null) emptyList() else SearchPath.multiReleaseRoots(root) }

    override fun search(name: ClassName): Path? {
        return SearchPath.searchFromRoots(roots, name)
    }

    override fun equals(other: Any?): Boolean = this === other || (other is ClassPath && path == other.path)

    override fun hashCode() = path.hashCode()

    override fun toString() = "ClassPath[$path]"
}
