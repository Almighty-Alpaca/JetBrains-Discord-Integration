package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

inline fun <T> tryCatch(print: Boolean = true, block: () -> T): T? = tryCatch(null, print, block)

inline fun <T> tryCatch(default: T, print: Boolean = true, block: () -> T): T = try {
    block()
} catch (e: Exception) {
    if (print) {
        e.printStackTrace()
    }
    default
}
