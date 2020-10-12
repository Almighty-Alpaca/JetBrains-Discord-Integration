package org.glavo.javah

import java.io.PrintWriter
import java.nio.file.Path
import java.util.*

class JavahTask {
    private val searchPaths: MutableList<SearchPath> = LinkedList()

    var outputDir: Path? = null

    private var errorHandle: PrintWriter = PrintWriter(System.err, true)

    private val classes: MutableList<ClassName> = LinkedList()

    fun run() {
        val outputDir = requireNotNull(outputDir, { "outputDir must not be null" })

        val generator = JNIGenerator(outputDir, searchPaths, errorHandle)

        for (cls in classes) {
            try {
                generator.generate(cls)
            } catch (ex: Exception) {
                ex.printStackTrace(errorHandle)
            }
        }
    }

    fun addClass(name: ClassName) {
        classes.add(name)
    }

    fun addClass(name: String) {
        classes.add(ClassName.of(name))
    }

    fun addClasses(i: Iterable<String>) {
        i.forEach { classes.add(ClassName.of(it)) }
    }

    fun addRuntimeSearchPath() {
        searchPaths.add(RuntimeSearchPath)
    }

    fun addSearchPath(searchPath: SearchPath) {
        searchPaths.add(searchPath)
    }

    fun addClassPath(classPath: Path) {
        searchPaths.add(ClassPath(classPath))
    }

    fun addModulePath(modulePath: Path) {
        searchPaths.add(ModulePath(modulePath))
    }
}
