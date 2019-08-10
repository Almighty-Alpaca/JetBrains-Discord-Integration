/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

interface FailingLazy<out T> : Lazy<T>

fun <T> failingLazy(default: T, initializer: () -> T): FailingLazy<T> = FailingLazyImpl(default, initializer)

private object UNINITIALIZED

private class FailingLazyImpl<out T>(private val default: T, initializer: () -> T) : FailingLazy<T> {
    private var initializer: (() -> T)? = initializer
    private var _value: Any? = UNINITIALIZED

    override val value: T
        get() {
            if (_value === UNINITIALIZED) {
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

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
