
package com.example.greeter

import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files

object NativeLoader {
	fun loadLibrary(classLoader: ClassLoader, libName: String) {
		try {
			System.loadLibrary(libName)
		} catch (ex: UnsatisfiedLinkError) {
			val url = classLoader.getResource(libFilename(libName))
			try {
				val file = Files.createTempFile("jni", libFilename(nameOnly(libName))).toFile()
				file.deleteOnExit()
				file.delete()
				url.openStream().use { `in` -> Files.copy(`in`, file.toPath()) }
				System.load(file.canonicalPath)
			} catch (e: IOException) {
				throw UncheckedIOException(e)
			}
		}
	}

	private fun libFilename(libName: String): String {
		val osName = System.getProperty("os.name").toLowerCase()
		if (osName.indexOf("win") >= 0) {
			return "$libName.dll"
		} else if (osName.indexOf("mac") >= 0) {
			return decorateLibraryName(libName, ".dylib")
		}
		return decorateLibraryName(libName, ".so")
	}

	private fun nameOnly(libName: String): String {
		val pos = libName.lastIndexOf('/')
		return if (pos >= 0) {
			libName.substring(pos + 1)
		} else libName
	}

	private fun decorateLibraryName(libraryName: String, suffix: String): String {
		if (libraryName.endsWith(suffix)) {
			return libraryName
		}
		val pos = libraryName.lastIndexOf('/')
		return if (pos >= 0) {
			libraryName.substring(0, pos + 1) + "lib" + libraryName.substring(pos + 1) + suffix
		} else {
			"lib$libraryName$suffix"
		}
	}
}
