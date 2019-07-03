package com.almightyalpaca.jetbrains.plugins.discord.plugin.logging

abstract class Logger {
    abstract fun log(level: Level, o: Any?)
    abstract fun log(level: Level, t: Throwable, o: Any?)

    class Impl : Logger() {
        override fun log(level: Level, o: Any?) = println(o)
        override fun log(level: Level, t: Throwable, o: Any?) {
            log(level, o)
            t.printStackTrace()
        }
    }

    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
