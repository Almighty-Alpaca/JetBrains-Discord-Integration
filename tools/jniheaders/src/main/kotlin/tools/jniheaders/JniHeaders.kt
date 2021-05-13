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

package tools.jniheaders

import com.google.common.reflect.ClassPath
import java.lang.reflect.*
import java.nio.file.*
import java.util.*
import java.util.function.Function
import kotlin.Array
import kotlin.Boolean
import kotlin.BooleanArray
import kotlin.Byte
import kotlin.ByteArray
import kotlin.Char
import kotlin.CharArray
import kotlin.Double
import kotlin.DoubleArray
import kotlin.Float
import kotlin.FloatArray
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.LongArray
import kotlin.Short
import kotlin.ShortArray
import kotlin.String
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.*
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError
import java.lang.reflect.Array as ReflectArray

private val FIELD_COMPARATOR: Comparator<Field> =
    Comparator.comparing(Field::getName)

private val CONSTRUCTOR_COMPARATOR: Comparator<Constructor<*>> =
    Comparator.comparing { constructor -> constructor.parameters.joinToString(separator = ",", transform = { clazz -> clazz.javaClass.jniType }) }

private val METHOD_COMPARATOR: Comparator<Method> =
    Comparator
        .comparing(Method::getName)
        .thenComparing(Function { it.parameterTypes.joinToString(separator = ",", transform = Class<*>::jniType) })

private object ClassReference

@Suppress("UnstableApiUsage")
@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {
    val path = Paths.get(args[0])

    val fileSystem = FileSystems.getDefault()

    val classNames = args
        .asSequence()
        .drop(1)
        .toList()

    val patterns = classNames
        .map("glob:"::plus)
        .map(fileSystem::getPathMatcher)
        .toList()

    val patternClasses = ClassPath
        .from(ClassLoader.getSystemClassLoader())
        .allClasses
        .filter { c -> patterns.any { p -> p.matches(Paths.get(c.name.replace('$', '.'))) } }
        .map(ClassPath.ClassInfo::load)
        .map(Class<*>::kotlin)
        .toList()

    val directClasses = classNames
        .mapNotNull {
            try {
                Class.forName(it).kotlin
            } catch (ignored: Exception) {
                null
            }
        }
        .toList()

    val classes = (patternClasses + directClasses).distinct()

    val files = writeClasses(path, classes)

    Files.createDirectories(path)

    Files.newBufferedWriter(path.resolve("jniclasses.h"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { writer ->
        files.forEach { file ->
            writer.appendLine("""#include "$file"""")
        }
    }
}

private fun writeClasses(path: Path, classes: Iterable<KClass<*>>): Sequence<String> = classes
    .asSequence()
    .filter { clazz -> clazz.simpleName?.endsWith("Kt") == false }
    .map { clazz -> clazz to "${clazz.qualifiedName!!.replace(".", "/")}.h" }
    .onEach { (clazz, subpath) ->
        val file = path.resolve(subpath)

        Files.createDirectories(file.parent)

        Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { writer ->
            with(writer) {
                val guard = clazz.qualifiedName!!.replace('.', '_').toUpperCase()

                appendHeader(guard)

                sequenceOf(clazz).forEach(this::appendClass)

                appendFooter(guard)
            }
        }
    }
    .map(Pair<*, String>::second)

private fun Appendable.appendHeader(guard: String): Appendable {
    appendLine("#ifndef $guard")
    appendLine("#define $guard")
    appendLine()
    appendLine("#include <jni.h>")
    appendLine()

    return this
}

private fun Appendable.appendFooter(guard: String): Appendable {
    appendLine()
    appendLine("#endif // $guard")

    return this
}

private fun Appendable.appendClass(clazz: KClass<*>): Appendable {
    val jClazz = clazz.java

    if (jClazz.isPrimitive || jClazz.isSynthetic || jClazz.isAnonymousClass)
        return this

    appendLine()
    appendLine("namespace ${jClazz.canonicalName.replace(".", "::")} {")
    appendLine("    const auto NAME = \"${jClazz.name.replace(".", "/")}\";")
    appendLine()
    appendLine("    inline jclass getClass(JNIEnv &env) {")
    appendLine("        return env.FindClass(NAME);")
    appendLine("    }")
    appendConstructors(clazz)
    appendLine()
    appendFields(jClazz)
    appendLine()
    appendMethods(jClazz)

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

            val function = try {
                method.kotlinFunction
                    ?: clazz.kotlin.memberProperties.flatMap {
                        if (it is KMutableProperty<*>)
                            listOf(it.getter, it.setter)
                        else
                            listOf(it.getter)
                    }.find { it.javaMethod == method } ?: throw IllegalStateException("Could not find KFunction for $method")
            } catch (e: KotlinReflectionInternalError) {
                appendMethodJava(method, clazz, groupIndex, methodIndex, methodGroup)

                continue
            }

            appendMethodKotlin(function, method, clazz, groupIndex, methodIndex, methodGroup)
        }
    }

    return this
}

private fun Appendable.appendMethodKotlin(
    function: KFunction<Any?>,
    method: Method,
    clazz: Class<*>,
    groupIndex: Int,
    methodIndex: Int,
    methodGroup: List<Method>
) {
    val parameters = function.parameters.filter { !(it.kind == KParameter.Kind.INSTANCE && it.type.jvmErasure.java == clazz) }

    if (parameters.any { it.type.classifier == null })
        return

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

private fun Appendable.appendMethodJava(
    method: Method,
    clazz: Class<*>,
    groupIndex: Int,
    methodIndex: Int,
    methodGroup: List<Method>,
) {
    val parameters = method.parameters.filter { it.type != clazz }

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

    val parametersDoc = parameters.map { ParameterDoc(it.name ?: "???", it.type.typeName) }.toTypedArray()

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
        append(", ").append(parameter.type.jniType).append(" ").append(parameter.name ?: "???")
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
        append(", ").append(parameter.type.jniType).append(" ").append(parameter.name ?: "???")
    }
    appendLine(") {")
    append("        return (${method.returnType.jniType}) env.${method.returnType.jniCallMethodName}(object, methods::${methodName}::getId(env)")
    for (parameter in parameters) {
        append(", ").append(parameter.name)
    }
    appendLine(");")
    appendLine("    }")
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

private fun Appendable.appendConstructors(clazz: KClass<*>): Appendable {

    val jClazz = clazz.java

    val constructors = clazz
        .java
        .constructors
        // .filterNot(Constructor<*>::isSynthetic)
        .filter { Modifier.isPublic(it.modifiers)}
        // .filter { !Modifier.isStrict(it.modifiers) }
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

        val parameters = constructor
            .parameters
            .map {
                ParameterDoc(
                    it.name!!,
                    it.type.typeName
                )
            }
            .toTypedArray()

        appendMethodDoc(
            "        ",
            ParameterDoc("env", "JNIEnv&"),
            ParameterDoc("clazz", "jclass"),
            ParameterDoc("method", "jmethodID"),
            *
            parameters,
            returnType = jClazz.typeName
        )
        append("        inline ").append(constructor.declaringClass.jniType).append(" invoke(JNIEnv &env, jclass clazz, jmethodID method")
        for (parameter in constructor.parameters) {
            append(", ").append(parameter.type.jniType).append(" ").append(parameter.name)
        }
        appendLine(") {")
        append("            return (").append(constructor.declaringClass.jniType).append(") env.NewObject(clazz, method")
        for (parameter in constructor.parameters) {
            append(", ").append(parameter.name)
        }
        appendLine(");")
        appendLine("        }")
        appendLine()
        appendMethodDoc(
            "        ",
            ParameterDoc("env", "JNIEnv&"),
            *parameters,
            returnType = jClazz.typeName
        )
        append("        inline ").append(constructor.declaringClass.jniType).append(" invoke(JNIEnv &env")
        for (parameter in constructor.parameters) {
            append(", ").append(parameter.type.jniType).append(" ").append(parameter.name)
        }
        appendLine(") {")
        appendLine("            jclass clazz = getClass(env);")
        append("            return (").append(constructor.declaringClass.jniType).append(") env.NewObject(clazz, getId(env, clazz)")
        for (parameter in constructor.parameters) {
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

        BooleanArray::class.javaObjectType -> "jbooleanArray"
        ByteArray::class.javaObjectType -> "jbyteArray"
        CharArray::class.javaObjectType -> "jcharArray"
        ShortArray::class.javaObjectType -> "jshortArray"
        IntArray::class.javaObjectType -> "jintArray"
        LongArray::class.javaObjectType -> "jlongArray"
        FloatArray::class.javaObjectType -> "jfloatArray"
        DoubleArray::class.javaObjectType -> "jdoubleArray"

        // TODO: Special cases, test if this works everywhere
        String::class.javaObjectType -> "jstring"
        Class::class.javaObjectType -> "jclass"
        Array::class.javaObjectType -> "jobject"

        else -> when {
            Throwable::class.javaObjectType.isAssignableFrom(this) -> "jthrowable"
            else -> "jobject"
        }
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
        else -> ReflectArray.newInstance(this, 0).toString().substring(1).substringBefore('@').replace('.', '/')
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
