package org.glavo.javah

import org.glavo.javah.Utils.FULL_NAME_PATTERN
import org.glavo.javah.Utils.mangleName
import java.util.*

class ClassName private constructor(private val moduleName: String?, val className: String) {
    val simpleName: String = className.substring(className.lastIndexOf('.') + 1)
    val mangledName: String = mangleName(className)
    val relativePath = className.replace('.', '/') + ".class"

    override fun equals(other: Any?): Boolean = this === other || (other is ClassName && moduleName == other.moduleName && className == other.className)

    override fun hashCode() = Objects.hash(moduleName, className)

    override fun toString(): String {
        return if (moduleName == null) {
            className
        } else {
            "$moduleName/$className"
        }
    }

    companion object {
        fun of(moduleName: String?, className: String): ClassName {
            require(moduleName == null || FULL_NAME_PATTERN.matches(moduleName)) { "Illegal module name: $moduleName" }
            require(FULL_NAME_PATTERN.matches(className)) { "Illegal class name: $moduleName" }

            return ClassName(moduleName, className)
        }

        fun of(fullName: String): ClassName {
            val idx = fullName.indexOf('/')

            return if (idx == -1) {
                of(null, fullName)
            } else of(fullName.substring(0, idx), fullName.substring(idx + 1))
        }
    }
}
