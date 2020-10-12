package org.glavo.javah

import org.glavo.javah.Utils.classPathRoot
import java.nio.file.Path

class ClassPath(path: Path) : SearchPath {
    private val path: Path = path.toAbsolutePath()
    private val roots: List<Path>

    override fun search(name: ClassName): Path? {
        return SearchPath.searchFromRoots(roots, name)
    }

    override fun equals(o: Any?): Boolean {
        return if (this === o) true
        else if (o == null || javaClass != o.javaClass) false
        else {
            val classPath = o as ClassPath
            path == classPath.path
        }
    }

    override fun hashCode() = path.hashCode()

    override fun toString() = "ClassPath[$path]"

    init {
        val root = classPathRoot(path)
        roots = if (root == null) emptyList() else SearchPath.multiReleaseRoots(root)
    }
}
