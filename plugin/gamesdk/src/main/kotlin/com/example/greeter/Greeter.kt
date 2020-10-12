
package com.example.greeter

class Greeter {
	companion object {
		init {
			NativeLoader.loadLibrary(Greeter::class.java.classLoader, "gamesdk")
		}
	}

	external fun sayHello(name: String?): String?
}
