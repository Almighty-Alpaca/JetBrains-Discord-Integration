package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> throwing(initializer: () -> Throwable): ReadWriteProperty<Any?, T> = ThrowingDelegate(initializer)

private class ThrowingDelegate<T>(private val initializer: () -> Throwable) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = throw  initializer()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = throw  initializer()
}
