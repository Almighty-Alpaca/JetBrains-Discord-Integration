package com.almightyalpaca.jetbrains.plugins.discord.plugin.logging


abstract class Logging {
    val logger by lazy { Logger.Impl() }

    protected inline fun log(level: Logger.Level, factory: () -> Any?) {
        if (with(logger) { level() })
            logger.log(factory.invoke().toString())
    }

    protected inline fun log(level: Logger.Level, t: Throwable, factory: () -> Any?) {
        if (with(logger) { level() })
            logger.log(t, factory.invoke().toString())
    }

    protected inline operator fun Logger.Level.invoke(factory: () -> Any?) = log(this, factory)
    protected inline operator fun Logger.Level.invoke(t: Throwable, factory: () -> Any?) = log(this, t, factory)
}
