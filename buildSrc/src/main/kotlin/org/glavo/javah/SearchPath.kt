package org.glavo.javah

import org.glavo.javah.Utils.MULTI_RELEASE_VERSIONS
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.jar.Manifest
import java.util.stream.Collectors

interface SearchPath {
    fun search(name: ClassName): Path?

    companion object {
        fun searchFrom(searchPaths: Iterable<SearchPath>, name: ClassName): Path? {
            for (searchPath in searchPaths) {
                val path = searchPath.search(name)

                if (path != null) {
                    return path
                }
            }
            return null
        }

        fun searchFromRoots(roots: Iterable<Path>, name: ClassName): Path? {
            for (root in roots) {
                if (!Files.isDirectory(root)) {
                    continue
                }

                var path = root.resolve(name.relativePath)

                if (Files.isRegularFile(path)) {
                    return path
                }

                if (Files.isSymbolicLink(path)) {
                    try {
                        path = Files.readSymbolicLink(path)
                        if (Files.isRegularFile(path)) {
                            return path
                        }
                    } catch (ignored: IOException) {
                    }
                }
            }

            return null
        }

        fun multiReleaseRoots(root: Path): List<Path> {
            if (!Files.isDirectory(root)) {
                return emptyList()
            }

            var isMultiRelease = false

            try {
                Files.newInputStream(root.resolve("META-INF").resolve("MANIFEST.MF")).use { `in` -> isMultiRelease = "true" == Manifest(`in`).mainAttributes.getValue("Multi-Release") }
            } catch (ignored: IOException) {
            } catch (ignored: NullPointerException) {
            }

            if (isMultiRelease) {
                val base = root.resolve("META-INF").resolve("versions")
                if (Files.isDirectory(base)) {
                    try {
                        val list: MutableList<Path> = Files.list(base)
                            .map { obj: Path -> obj.toAbsolutePath() }
                            .filter { path: Path? -> Files.isDirectory(path) }
                            .filter { p: Path -> MULTI_RELEASE_VERSIONS.contains(p.fileName.toString()) }
                            .sorted(Comparator.comparing { p: Path -> p.fileName.toString().toInt() }.reversed())
                            .collect(Collectors.toCollection { LinkedList() })

                        list.add(root)

                        return Collections.unmodifiableList(list)
                    } catch (ignored: IOException) {
                    }
                }
            }
            return listOf(root)
        }
    }
}
