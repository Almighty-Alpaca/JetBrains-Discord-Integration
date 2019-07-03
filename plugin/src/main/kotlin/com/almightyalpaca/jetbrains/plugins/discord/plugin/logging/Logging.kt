package com.almightyalpaca.jetbrains.plugins.discord.plugin.logging

abstract class Logging {
    val logger by lazy { Logger.Impl() }

    protected inline fun log(factory: () -> Any?) {
        logger.log(Logger.Level.DEBUG, factory.invoke().toString())
    }

    protected inline fun log(t: Throwable, factory: () -> Any?) {
        logger.log(Logger.Level.DEBUG, t, factory.invoke().toString())
    }
}
