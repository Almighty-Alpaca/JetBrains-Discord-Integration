package org.glavo.javah

import java.io.IOException
import java.net.URI
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

object RuntimeSearchPath : SearchPath {
    override fun search(name: ClassName): Path? {
        var uri: URI? = null

        try {
            val cls = Class.forName(name.className)

            uri = cls.getResource(name.simpleName + ".class").toURI()

            return Paths.get(uri!!)
        } catch (ex: FileSystemNotFoundException) {
            if (uri == null) {
                return null
            }

            try {
                return FileSystems.newFileSystem(uri, emptyMap<String, Any>()).getPath("/", name.relativePath)
            } catch (ignored: IOException) {
            } catch (ignored: NullPointerException) {
            }
        } catch (ignored: Exception) {
        }

        return null
    }
}
