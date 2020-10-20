/*
 * Copyright 2017-2020 Aljoscha Grebe
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

package gamesdk.impl.utils

import gamesdk.impl.Pointer
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal sealed class Native

private object NativeInstance : Native()

internal sealed class NativeObject {
    abstract val alive: Boolean

    internal abstract fun <T> native(block: Native.() -> T): T

    protected fun <T> nativeProperty(setter: Native.(Pointer, T) -> Unit, getter: Native.(Pointer) -> T): ReadWriteProperty<CloseableNativeObject, T> = Property(setter, getter)

    private class Property<T>(private val setter: Native.(Pointer, T) -> Unit, private val getter: Native.(Pointer) -> T) : ReadWriteProperty<CloseableNativeObject, T> {
        override fun setValue(thisRef: CloseableNativeObject, property: KProperty<*>, value: T) {
            thisRef.native { setter(thisRef.pointer, value) }
        }

        override fun getValue(thisRef: CloseableNativeObject, property: KProperty<*>): T {
            return thisRef.native { getter(thisRef.pointer) }
        }
    }

    protected fun <T> nativeLazy(tCreator: () -> T): ReadOnlyProperty<NativeObject, T> = Lazy(tCreator)

    private class Lazy<T>(tCreator: () -> T) : ReadOnlyProperty<NativeObject, T> {
        private val t by lazy(tCreator)

        override fun getValue(thisRef: NativeObject, property: KProperty<*>): T {
            return thisRef.native { t }
        }
    }
}

internal abstract class DelegateNativeObject internal constructor(private val delegate: NativeObject) : NativeObject() {
    final override val alive
        get() = delegate.alive

    final override fun <T> native(block: Native.() -> T) = synchronized(delegate) {
        if (alive) {
            NativeInstance.block()
        } else {
            throw IllegalStateException()
        }
    }
}

internal abstract class CloseableNativeObject internal constructor(internal val pointer: Pointer) : NativeObject(), AutoCloseable {
    final override var alive = true
        private set

    final override fun <T> native(block: Native.() -> T) = synchronized(this) {
        if (alive) {
            NativeInstance.block()
        } else {
            throw IllegalStateException()
        }
    }

    override fun close() = native {
        alive = false

        nativeDestroy(pointer)
    }
}

private external fun Native.nativeDestroy(core: Pointer)
