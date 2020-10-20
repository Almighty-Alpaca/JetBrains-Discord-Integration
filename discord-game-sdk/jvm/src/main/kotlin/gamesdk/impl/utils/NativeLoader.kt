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

package gamesdk.impl.utils

import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object NativeLoader {
    fun loadLibraries(classLoader: ClassLoader, vararg libraryNames: String) {
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
            os.contains("mac") -> "macos" // TODO: check if pat his correct
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
