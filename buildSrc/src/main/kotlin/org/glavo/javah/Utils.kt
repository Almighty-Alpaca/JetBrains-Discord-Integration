package org.glavo.javah

import org.jetbrains.kotlin.konan.file.use
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.PrintWriter
import java.io.Writer
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

object Utils {
    const val MAX_SUPPORTED_VERSION = 13

    val MULTI_RELEASE_VERSIONS = (9..MAX_SUPPORTED_VERSION).asSequence().map(Int::toString).toList()

    val SIMPLE_NAME_PATTERN: Pattern = Pattern.compile("[^.;\\[/]+")
    val FULL_NAME_PATTERN: Pattern = Pattern.compile("[^.;\\[/]+(\\.[^.;\\[/]+)*")
    val METHOD_NAME_PATTERN: Pattern = Pattern.compile("(<init>)|(<cinit>)|([^.;\\[/<>]+)")
    val METHOD_TYPE_PATTERN: Pattern = Pattern.compile("\\((?<args>(\\[*([BCDFIJSZ]|L[^.;\\[/]+(/[^.;\\\\\\[/]+)*;))*)\\)(?<ret>\\[*([BCDFIJSZV]|L[^.;\\[/]+(/[^.;\\[/]+)*;))")

    val NOOP_WRITER = PrintWriter(object : Writer() {
        override fun write(cbuf: CharArray, off: Int, len: Int) {
        }

        override fun flush() {
        }

        override fun close() {
        }
    })

    fun mangleName(name: String): String {
        val builder = StringBuilder(name.length * 2)
        val len = name.length
        for (i in 0 until len) {
            val ch = name[i]
            if (ch == '.') {
                builder.append('_')
            } else if (ch == '_') {
                builder.append("_1")
            } else if (ch == ';') {
                builder.append("_2")
            } else if (ch == '[') {
                builder.append("_3")
            } else if (ch in '0'..'9' || ch in 'a'..'z' || ch in 'A'..'Z') {
                builder.append(ch)
            } else {
                builder.append(String.format("_0%04x", ch.toInt()))
            }
        }
        return builder.toString()
    }

    fun escape(unicode: String): String {
        Objects.requireNonNull(unicode)
        val len = unicode.length
        val builder = StringBuilder(len)
        for (i in 0 until len) {
            val ch = unicode[i]
            if (ch in ' '..'~') {
                builder.append(ch)
            } else {
                builder.append(String.format("\\u%04x", ch.toInt()))
            }
        }
        return builder.toString()
    }

    fun classPathRoot(p: Path): Path? {
        val path = p.toAbsolutePath()

        if (Files.notExists(path)) {
            return null
        } else if (Files.isDirectory(path)) {
            return path
        }

        FileSystems.newFileSystem(path, null as ClassLoader?).use { fs ->
            val name = path.fileName.toString().toLowerCase()

            if (name.endsWith(".jar") || name.endsWith(".zip")) {
                return fs.getPath("/")
            } else if (name.endsWith(".jmod")) {
                return fs.getPath("/", "classes")
            }

            fs.close()
        }

        return null
    }

    fun superClassOf(reader: ClassReader): ClassName? {
        Objects.requireNonNull(reader)
        class V : ClassVisitor(Opcodes.ASM7) {
            var superName: ClassName? = null
            override fun visit(version: Int, access: Int, name: String, signature: String, superName: String, interfaces: Array<String>) {
                this.superName = ClassName.of(superName.replace('/', '.'))
            }
        }

        val v = V()
        reader.accept(v, ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG)
        return v.superName
    }
}
