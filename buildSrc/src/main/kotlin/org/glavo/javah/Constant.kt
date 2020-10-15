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
            is Boolean -> "$value"
            is Byte -> "${value.toInt()}L"
            is Char -> "${value.toInt()}L"
            is Double -> "$value"
            is Float -> "${value}f"
            is Int -> "${value.toInt()}L"
            is Long -> "${value}i64"
            is Short -> "${value.toInt()}L"
            is String -> "\"${value}\""
            else -> throw IllegalStateException("Unknown type: ${value.javaClass}")
        }
    }

    companion object {
        private val TYPES: List<Class<*>> = listOf(
            Boolean::class.javaObjectType,
            Byte::class.javaObjectType,
            Char::class.javaObjectType,
            Double::class.javaObjectType,
            Float::class.javaObjectType,
            Int::class.javaObjectType,
            Long::class.javaObjectType,
            Short::class.javaObjectType,
            String::class.javaObjectType // Because we're better than Java
        )

        fun isValid(name: String, value: Any) = TYPES.contains(value.javaClass) && SIMPLE_NAME_PATTERN.matcher(name).matches()

        fun of(name: String, value: Any): Constant {
            require(TYPES.contains(value.javaClass)) { "Invalid type ${value.javaClass}" }
            require(SIMPLE_NAME_PATTERN.matcher(name).matches()) { """"$name" is not a qualified constant name""" }

            return Constant(name, value)
        }
    }
}
