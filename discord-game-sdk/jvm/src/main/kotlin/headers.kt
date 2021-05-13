/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import gamesdk.impl.NativeDiscordObjectResult
import gamesdk.impl.types.NativeDiscordActivity
import gamesdk.impl.types.NativeDiscordPresence
import gamesdk.impl.types.NativeDiscordRelationship
import gamesdk.impl.types.NativeDiscordUser
import java.lang.reflect.*
import java.lang.reflect.Array
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.Function
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Comparator
import kotlin.Double
import kotlin.Float
import kotlin.IllegalStateException
import kotlin.Int
import kotlin.Long
import kotlin.Pair
import kotlin.Short
import kotlin.String
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

private val FIELD_COMPARATOR: java.util.Comparator<Field> =
    Comparator.comparing(Field::getName)

private val CONSTRUCTOR_COMPARATOR: Comparator<Constructor<*>> =
    Comparator.comparing { it.parameterTypes.joinToString(separator = ",", transform = Class<*>::jniType) }

private val METHOD_COMPARATOR: Comparator<Method> =
    Comparator
        .comparing(Method::getName)
        .thenComparing(Function { it.parameterTypes.joinToString(separator = ",", transform = Class<*>::jniType) })

public fun main() {
    val path = Paths.get("""C:\Users\Alpaca\git\JetBrains-Discord-Integration\discord-game-sdk\native\src\main\public\test.h""")
    Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { writer ->

        val classes = listOf(
            NativeDiscordActivity::class,
            NativeDiscordObjectResult::class,
            NativeDiscordUser::class,
            NativeDiscordPresence::class,
            NativeDiscordRelationship::class,
            Pair::class,
            // Kotlin reflection has problems with these classes
            // Int::class,
            // Long::class,
            // Boolean::class,
        )

        writer.appendKClasses(classes)
    }
}

private fun Appendable.appendKClasses(classes: Iterable<KClass<*>>): Appendable {
    return this.appendClasses(classes.map(KClass<*>::javaObjectType))
}

private fun Appendable.appendClasses(classes: Iterable<Class<*>>): Appendable {
    appendLine("#include <jni.h>")

    for (clazz in (classes.asSequence() + classes.asSequence().flatMap { it.classes.asSequence() })) {
        appendClass(clazz)
    }

    return this
}

private fun Appendable.appendClass(clazz: Class<*>): Appendable {
    if (clazz.isPrimitive)
        return this


    appendLine()
    appendLine("namespace ${clazz.canonicalName.replace(".", "::")} {")
    appendLine("    const auto NAME = \"${clazz.name.replace(".", "/")}\";")
    appendLine()
    appendLine("    inline jclass getClass(JNIEnv &env) {")
    appendLine("        return env.FindClass(NAME);")
    appendLine("    }")
    appendConstructors(clazz)
    appendLine()
    appendFields(clazz)
    appendLine()
    appendMethods(clazz)

    appendLine("}") // namespace class

    return this
}

private fun Appendable.appendMethods(clazz: Class<*>): Appendable {
    val methods = clazz
        .methods
        .filterNot(Method::isSynthetic)
        .filter { it.declaringClass == clazz }
        .sortedWith(METHOD_COMPARATOR)

    for ((groupIndex, methodGroup) in methods.groupBy(Method::getName).values.withIndex()) {
        for ((methodIndex, method) in methodGroup.withIndex()) {
            val function = method.kotlinFunction
                ?: clazz.kotlin.memberProperties.flatMap {
                    if (it is KMutableProperty<*>)
                        listOf(it.getter, it.setter)
                    else
                        listOf(it.getter)
                }.find { it.javaMethod == method } ?: throw IllegalStateException("Could not find KFunction for $method")

            if ((groupIndex != 0 || methodIndex != 0))
                appendLine()

            val methodName = when (methodGroup.size) {
                1 -> method.name.substringBefore('-')
                else -> "${method.name.substringBefore('-')}$methodIndex"
            }

            appendLine("    namespace methods::${methodName} {")
            appendLine("        const auto NAME = \"${method.name}\";")
            appendLine("        const auto SIGNATURE = \"${method.jniSignature}\";")
            appendLine()
            appendLine("        inline jmethodID getId(JNIEnv &env) {")
            appendLine("            return env.GetMethodID(getClass(env), NAME, SIGNATURE);")
            appendLine("        }")
            appendLine()
            appendLine("        inline jmethodID getId(JNIEnv &env, jclass clazz) {")
            appendLine("            return env.GetMethodID(clazz, NAME, SIGNATURE);")
            appendLine("        }")
            appendLine("    }") // namespace methods
            appendLine()

            val parameters = function.parameters.filter { !(it.kind == KParameter.Kind.INSTANCE && it.type.jvmErasure.java == clazz) }

            val parametersDoc = parameters.map { ParameterDoc(it.name ?: (function as? KProperty.Getter<*>)?.property?.name ?: "???", it.type.javaType.typeName) }.toTypedArray()

            appendMethodDoc(
                "    ",
                ParameterDoc("env", "JNIEnv&"),
                ParameterDoc("object", "jobject"),
                ParameterDoc("method", "jmethodID"),
                *parametersDoc,
                returnType = clazz.typeName
            )
            append("    inline ${method.returnType.jniType} ${methodName}(JNIEnv &env, jobject object, jmethodID method")

            for (parameter in parameters) {
                append(", ").append(parameter.type.jvmErasure.java.jniType).append(" ").append(parameter.name ?: (function as? KProperty.Getter<*>)?.property?.name ?: "???")
            }
            appendLine(") {")
            append("        return (${method.returnType.jniType}) env.${method.returnType.jniCallMethodName}(object, method")
            for (parameter in parameters) {
                append(", ").append(parameter.name)
            }
            appendLine(");")
            appendLine("    }")
            appendLine()
            appendMethodDoc(
                "    ",
                ParameterDoc("env", "JNIEnv&"),
                ParameterDoc("object", "jobject"),
                *parametersDoc,
                returnType = clazz.typeName
            )
            append("    inline ${method.returnType.jniType} ${methodName}(JNIEnv &env, jobject object")
            for (parameter in parameters) {
                append(", ").append(parameter.type.jvmErasure.java.jniType).append(" ").append(parameter.name ?: (function as? KProperty.Getter<*>)?.property?.name ?: "???")
            }
            appendLine(") {")
            append("        return (${method.returnType.jniType}) env.${method.returnType.jniCallMethodName}(object, methods::${methodName}::getId(env)")
            for (parameter in parameters) {
                append(", ").append(parameter.name)
            }
            appendLine(");")
            appendLine("    }")
        }
    }

    return this
}

private fun Appendable.appendFields(clazz: Class<*>): Appendable {
    appendLine("    namespace fields {")
    for ((i, field) in clazz.declaredFields.sortedWith(FIELD_COMPARATOR).withIndex()) {
        if (i != 0)
            appendLine()
        appendLine("        namespace ${field.name} {")
        appendLine("            const auto NAME = \"${field.name}\";")
        appendLine("            const auto SIGNATURE = \"${field.type.jniSignature}\";")
        appendLine()
        appendLine("            inline jfieldID getId(JNIEnv &env) {")
        appendLine("                return env.GetFieldID(getClass(env), NAME, SIGNATURE);")
        appendLine("            }")
        appendLine()
        appendLine("            inline jfieldID getId(JNIEnv &env, jclass clazz) {")
        appendLine("                return env.GetFieldID(clazz, NAME, SIGNATURE);")
        appendLine("            }")
        appendLine("        }") // namespace field
    }
    appendLine("    }") // namespace fields

    return this
}

private fun Appendable.appendConstructors(clazz: Class<*>): Appendable {
    val constructors = clazz
        .constructors
        .filterNot(Constructor<*>::isSynthetic)
        .filter { Modifier.isPublic(it.modifiers) }
        .filter { !Modifier.isStrict(it.modifiers) }
        .sortedWith(CONSTRUCTOR_COMPARATOR)

    for ((i, constructor) in constructors.withIndex()) {
        appendLine()
        appendLine("    namespace constructor$i {")
        appendLine("        const auto SIGNATURE = \"${constructor.jniSignature}\";")
        appendLine()
        appendLine("        inline jmethodID getId(JNIEnv &env) {")
        appendLine("            return env.GetMethodID(getClass(env), \"<init>\", SIGNATURE);")
        appendLine("        }")
        appendLine()
        appendLine("        inline jmethodID getId(JNIEnv &env, jclass clazz) {")
        appendLine("            return env.GetMethodID(clazz, \"<init>\", SIGNATURE);")
        appendLine("        }")
        appendLine()
        appendMethodDoc(
            "        ",
            ParameterDoc("env", "JNIEnv&"),
            ParameterDoc("clazz", "jclass"),
            ParameterDoc("method", "jmethodID"),
            *constructor.kotlinFunction!!.parameters.map { ParameterDoc(it.name!!, it.type.javaType.typeName) }.toTypedArray(),
            returnType = clazz.typeName
        )
        append("        inline jobject invoke(JNIEnv &env, jclass clazz, jmethodID method")
        for (parameter in constructor.kotlinFunction!!.parameters) {
            append(", ").append(parameter.type.jvmErasure.java.jniType).append(" ").append(parameter.name)
        }
        appendLine(") {")
        append("            return env.NewObject(clazz, method")
        for (parameter in constructor.kotlinFunction!!.parameters) {
            append(", ").append(parameter.name)
        }
        appendLine(");")
        appendLine("        }")
        appendLine()
        appendMethodDoc(
            "        ",
            ParameterDoc("env", "JNIEnv&"),
            *constructor.kotlinFunction!!.parameters.map { ParameterDoc(it.name!!, it.type.javaType.typeName) }.toTypedArray(),
            returnType = clazz.typeName
        )
        append("        inline jobject invoke(JNIEnv &env")
        for (parameter in constructor.kotlinFunction!!.parameters) {
            append(", ").append(parameter.type.jvmErasure.java.jniType).append(" ").append(parameter.name)
        }
        appendLine(") {")
        appendLine("            jclass clazz = getClass(env);")
        append("            return env.NewObject(clazz, getId(env, clazz)")
        for (parameter in constructor.kotlinFunction!!.parameters) {
            append(", ").append(parameter.name)
        }
        appendLine(");")
        appendLine("        }")
        appendLine("    }") // namespace constructor
    }


    return this
}

private data class ParameterDoc(val name: String, val javaType: String)

private fun Appendable.appendMethodDoc(indentation: String, vararg parameters: ParameterDoc, returnType: String): Appendable {
    append(indentation).append("/**").appendLine()
    for (parameter in parameters) {
        append(indentation).append(" * @param ").append(parameter.name).append(" ").append(parameter.javaType).appendLine()
    }
    append(indentation).append(" * @return ").append(returnType).appendLine()
    append(indentation).append(" **/").appendLine()

    return this
}

private val Class<*>.jniType: String
    get() = when (this) {
        Void::class.javaPrimitiveType -> "void"
        Boolean::class.javaPrimitiveType -> "jboolean"
        Byte::class.javaPrimitiveType -> "jbyte"
        Char::class.javaPrimitiveType -> "jchar"
        Short::class.javaPrimitiveType -> "jshort"
        Int::class.javaPrimitiveType -> "jint"
        Long::class.javaPrimitiveType -> "jlong"
        Float::class.javaPrimitiveType -> "jfloat"
        Double::class.javaPrimitiveType -> "jdouble"
        String::class.javaObjectType -> "jstring" // TODO: Special case, test if this works everywhere
        else -> "jobject"
    }

private val Class<*>.jniCallMethodName: String
    get() = when (this) {
        Void::class.javaPrimitiveType -> "CallVoidMethod"
        Boolean::class.javaPrimitiveType -> "CallBooleanMethod"
        Byte::class.javaPrimitiveType -> "CallByteMethod"
        Char::class.javaPrimitiveType -> "CallCharMethod"
        Short::class.javaPrimitiveType -> "CallShortMethod"
        Int::class.javaPrimitiveType -> "CallIntMethod"
        Long::class.javaPrimitiveType -> "CallLongMethod"
        Float::class.javaPrimitiveType -> "CallFloatMethod"
        Double::class.javaPrimitiveType -> "CallDoubleMethod"
        else -> "CallObjectMethod"
    }

private val Class<*>.jniSignature: String
    get() = when {
        this === Void.TYPE -> "V"
        else -> Array.newInstance(this, 0).toString().substring(1).substringBefore('@').replace('.', '/')
    }

private val Executable.jniSignature: String
    get() {
        val sb = StringBuilder("(")
        for (type in parameterTypes) {
            sb.append(type.jniSignature)
        }

        sb.append(')')
        val returnType = when (this) {
            is Constructor<*> -> Void.TYPE
            is Method -> returnType
            else -> throw IllegalStateException("Unknown type ${javaClass.name}")
        }

        sb.append(returnType.jniSignature)

        return sb.toString()
    }
