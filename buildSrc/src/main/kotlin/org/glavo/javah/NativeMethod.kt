package org.glavo.javah

import org.glavo.javah.Utils.METHOD_NAME_PATTERN
import org.glavo.javah.Utils.METHOD_TYPE_PATTERN
import org.glavo.javah.Utils.mangleName
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.util.*

class NativeMethod private constructor(private val access: Int, val name: String, val type: Type, arguments: String) {
    val mangledName = mangleName(name)
    val longMangledName = mangledName + "__" + mangleName(arguments)
    val isStatic: Boolean
        get() = access and Opcodes.ACC_STATIC != 0

    override fun equals(other: Any?): Boolean {
        return this === other || (other is NativeMethod && name == other.name && type == other.type)
    }

    override fun hashCode(): Int {
        return Objects.hash(name, type)
    }

    override fun toString(): String {
        return String.format("NativeMethod[name=%s, type=%s}", name, type)
    }

    companion object {
        fun of(name: String, descriptor: String?): NativeMethod {
            return of(0, name, descriptor)
        }

        fun of(name: String, type: Type): NativeMethod {
            return of(0, name, type)
        }

        fun of(access: Int, name: String, descriptor: String?): NativeMethod {
            Objects.requireNonNull(name)
            Objects.requireNonNull(descriptor)
            return of(access, name, Type.getType(descriptor))
        }

        fun of(access: Int, name: String, type: Type): NativeMethod {
            Objects.requireNonNull(name)
            Objects.requireNonNull(type)
            require(METHOD_NAME_PATTERN.matcher(name).matches()) { String.format("\"%s\" is not a qualified method name", name) }
            val m = METHOD_TYPE_PATTERN.matcher(type.toString())
            require(m.matches()) { String.format("\"%s\" is not a method type", type) }
            return NativeMethod(access, name, type, m.group("args"))
        }
    }
}
