package com.almightyalpaca.jetbrains.plugins.discord.plugin.logging

abstract class Logger {
    abstract fun log(o: Any?)
    abstract fun log(t: Throwable, o: Any?)
    abstract operator fun Level.invoke(): Boolean

    class Impl : Logger() {
        override fun Level.invoke() = true
        override fun log(o: Any?) = System.out.println(o)
        override fun log(t: Throwable, o: Any?) {
            log(o)
            t.printStackTrace()
        }
    }

    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
