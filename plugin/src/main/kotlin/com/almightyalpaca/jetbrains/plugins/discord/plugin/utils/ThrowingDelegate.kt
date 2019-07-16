/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> throwing(initializer: () -> Throwable): ReadWriteProperty<Any?, T> = ThrowingDelegate(initializer)

private class ThrowingDelegate<T>(private val initializer: () -> Throwable) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = throw  initializer()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = throw  initializer()
}
