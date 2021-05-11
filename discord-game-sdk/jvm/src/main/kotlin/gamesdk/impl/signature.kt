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

package gamesdk.impl

import gamesdk.impl.events.NativeRelationshipUpdateEvent
import java.lang.reflect.Array
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import kotlin.reflect.KClass

public fun main() {
//    val method: KFunction<*> = ::NativeDiscordRelationship
    val clazz: KClass<*> = NativeRelationshipUpdateEvent::class

    val className = clazz.java.name
//    val methodName = (method.javaMethod ?: method.javaConstructor ?: throw IllegalStateException("$method is neither a method nor a constructor")).name

//    val className = Events::class.java.canonicalName
//    val methodName = "getCurrentUserUpdates"

    for (executable in Class.forName(className).let { it.methods.toList() + it.constructors }) {
//        if (executable.name == methodName) {
        if (executable.declaringClass.name != "java.lang.Object") {
            val name = when (executable) {
                is Constructor<*> -> "<init>"
                is Method -> executable.name
                else -> throw IllegalStateException("Unknown type ${executable.javaClass.name}")
            }

            println("${className.replace('.', '/')} $name ${getSignature(executable)}")
        }
    }
}

private fun getSignature(e: Executable): String {
    val sb = StringBuilder("(")
    for (type in e.parameterTypes) {
        sb.append(Array.newInstance(type, 0).toString().substring(1).substringBefore('@').replace('.', '/'))
    }

    sb.append(')')
    val returnType = when (e) {
        is Constructor<*> -> Void.TYPE
        is Method -> e.returnType
        else -> throw IllegalStateException("Unknown type ${e.javaClass.name}")
    }

    if (returnType === Void.TYPE) {
        sb.append("V")
    } else {
        sb.append(Array.newInstance(returnType, 0).toString().substring(1).substringBefore('@').replace('.', '/'))
    }

    return sb.toString()
}
