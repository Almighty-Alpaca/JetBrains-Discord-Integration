package org.glavo.javah

import org.glavo.javah.Utils.SIMPLE_NAME_PATTERN
import org.glavo.javah.Utils.mangleName
import java.util.*

class Constant private constructor(val name: String, val value: Any) {
    val mangledName: String = mangleName(name)

    override fun equals(other: Any?) = this === other || (other is Constant && name == other.name && value == other.value)

    override fun hashCode() = Objects.hash(name, value)

    override fun toString() = String.format("Constant[name=%s, value=%s]", name, value)

    fun valueToString(): String {
        return when (value) {
            is Double -> value.toString()
            is Float -> value.toString() + "f"
            is Long -> value.toString() + "i64"
            is Char -> value.toInt().toString() + "L"
            else -> value.toString() + "L"
        }
    }

    companion object {
        private val TYPES = listOf<Class<*>>(Byte::class.java, Short::class.java, Int::class.java, Long::class.java, Char::class.java, Float::class.java, Double::class.java)

        fun of(name: String, value: Any): Constant {
            require(TYPES.contains(value.javaClass))
            require(SIMPLE_NAME_PATTERN.matcher(name).matches()) { String.format("\"%s\" is not a qualified constant name", name) }

            return Constant(name, value)
        }
    }
}
