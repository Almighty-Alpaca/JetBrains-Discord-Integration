package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

interface FailingLazy<out T> : Lazy<T>

fun <T> failingLazy(default: T, initializer: () -> T): FailingLazy<T> = FailingLazyImpl(default, initializer)

private object UNINITIALIZED_VALUE

private class FailingLazyImpl<out T>(private val default: T, initializer: () -> T) : FailingLazy<T> {
    private var initializer: (() -> T)? = initializer
    private var _value: Any? = UNINITIALIZED_VALUE

    override val value: T
        get() {
            if (_value === UNINITIALIZED_VALUE) {
                try {
                    _value = initializer!!()
                    initializer = null
                } catch (e: Exception) {
                    e.printStackTrace()
                    return default
                }
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
