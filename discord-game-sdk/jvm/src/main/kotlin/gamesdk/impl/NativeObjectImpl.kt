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

import gamesdk.api.NativeObject
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal typealias NativePointer = Long

internal sealed class Native(library: String, vararg libraries: String) {
    init {
        loadLibraries(Native::class.java.classLoader, library, *libraries)
    }

    /**
     * Loads one or more native libraries in the order specifies.
     * The directory structure must follow the pattern "$os/$architecture/$filePrefix$libraryName$fileSuffix"
     *
     * For a library named `x` the following paths are examples of valid locations:
     * - `windows/x86/x.dll`
     * - `linux/x86-64/x.so`
     * - `macos/x86-64/x.dylib`
     *
     * TODO: make private instance method once no outside class relies on it anymore
     */
    companion object {
        internal fun loadLibraries(classLoader: ClassLoader, vararg libraryNames: String) {
            try {
                val tempDir = Files.createTempDirectory("jni")

                for (libraryName in libraryNames) {
                    val path = constructPath(libraryName)

                    val tempPath = tempDir.resolve(constructName(libraryName))
                    tempPath.toFile().deleteOnExit()

                    classLoader.getResourceAsStream(path).use { inputStream ->
                        if (inputStream == null) {
                            throw IllegalStateException("Couldn't find native library at $path")
                        } else {
                            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING)
                        }
                    }

                    System.load(tempPath.toAbsolutePath().toString())
                }
            } catch (e: IOException) {
                throw IllegalStateException(e)
            }
        }

        private fun constructPath(libraryName: String) = "$os/$architecture/${constructName(libraryName)}"

        private fun constructName(libraryName: String) = "$filePrefix$libraryName$fileSuffix"

        private val filePrefix: String by lazy {
            val os = System.getProperty("os.name").toLowerCase()

            when {
                os.contains("windows") -> ""
                os.contains("mac") -> "lib"
                os.contains("linux") -> "lib"
                else -> throw IllegalStateException("unknown operating system: $os")
            }
        }

        private val fileSuffix: String by lazy {
            val os = System.getProperty("os.name").toLowerCase()

            when {
                os.contains("windows") -> ".dll"
                os.contains("mac") -> ".dylib"
                os.contains("linux") -> ".so"
                else -> throw IllegalStateException("unknown operating system: $os")
            }
        }

        private val os: String by lazy {
            val os = System.getProperty("os.name").toLowerCase()

            when {
                os.contains("windows") -> "windows"
                os.contains("mac") -> "macos" // TODO: check if path is correct
                os.contains("linux") -> "linux"
                else -> throw IllegalStateException("unknown operating system: $os")
            }
        }

        private val architecture: String by lazy {
            when (val os = System.getProperty("os.arch").toLowerCase()) {
                "x86" -> "x86"
                "amd64" -> "x86-64"
                else -> throw IllegalStateException("unknown architecture: $os")
            }
        }
    }
}

private object NativeInstance : Native("discord_game_sdk", "discord_game_sdk_kotlin")

internal typealias NativeCreator<T> = Native.() -> T

internal abstract class NativeObjectCreator {
    protected fun <T> native(creator: NativeCreator<T>): T = NativeInstance.creator()
}

internal typealias NativeMethod<T> = Native.(pointer: NativePointer) -> T

internal sealed class NativeObjectImpl : NativeObject {
    protected abstract fun <T> native(block: NativeMethod<T>): T

    internal abstract class Delegate(private val parent: NativeObjectImpl) : NativeObjectImpl() {
        final override fun <T> native(block: NativeMethod<T>): T = parent.native(block)

        final override val alive
            get() = parent.alive
    }

    internal abstract class Closeable(@Volatile private var pointer: NativePointer) : NativeObjectImpl(), AutoCloseable {
        protected abstract val destructor: NativeMethod<Unit>

        private inline fun <T> synchronized(block: () -> T) = synchronized(this, block)

        final override fun <T> native(block: NativeMethod<T>) = synchronized {
            if (pointer != 0L) {
                NativeInstance.block(pointer)
            } else {
                throw IllegalStateException("Object was already closed")
            }
        }

        final override val alive
            get() = synchronized { pointer != 0L }

        // native() instead of synchronized() because native() also checks if the object is still alive
        final override fun close() = native { pointer ->
            destructor(pointer)

            this@Closeable.pointer = 0L
        }
    }

    protected fun <T> nativeProperty(setter: Native.(NativePointer, T) -> Unit, getter: NativeMethod<T>): ReadWriteProperty<Closeable, T> = Property(setter, getter)

    private class Property<T>(private val setter: Native.(NativePointer, T) -> Unit, private val getter: NativeMethod<T>) : ReadWriteProperty<NativeObjectImpl, T> {
        override fun setValue(thisRef: NativeObjectImpl, property: KProperty<*>, value: T) {
            thisRef.native { pointer -> setter(pointer, value) }
        }

        override fun getValue(thisRef: NativeObjectImpl, property: KProperty<*>): T {
            return thisRef.native { pointer -> getter(pointer) }
        }
    }

    protected fun <T : Any> nativeLazy(initializer: NativeMethod<T>) = lazy { native(initializer) }
}
