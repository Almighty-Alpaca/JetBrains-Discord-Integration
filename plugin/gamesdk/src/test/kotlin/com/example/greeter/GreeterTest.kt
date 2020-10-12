package com.example.greeter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GreeterTest {
    @Test
    fun testGreeter() {
        val greeter = Greeter()
        val greeting = greeter.sayHello("World")
        assertEquals(greeting, "Bonjour, World! I'm using Discord GameSDK version 2!")
    }

    @Test
    fun testNullGreeter() {
        val greeter = Greeter()
        val greeting = greeter.sayHello(null)
        assertEquals(greeting, "name cannot be null")
    }
}
