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

internal sealed class NativeObject(private val pointer: () -> Pointer) {
    constructor(delegate: NativeObject) : this(delegate.pointer)

    internal abstract fun <T> native(block: Native.(Pointer) -> T): T

    protected fun <T> native(lock: CloseableNativeObject, block: Native.(Pointer) -> T) = synchronized(lock) {
        if (pointer() != 0L) {
            NativeInstance.block(pointer())
        } else {
            throw IllegalStateException("Object was already closed")
        }
    }

    protected fun <T> nativeProperty(setter: Native.(Pointer, T) -> Unit, getter: Native.(Pointer) -> T): ReadWriteProperty<CloseableNativeObject, T> = Property(setter, getter)

    private class Property<T>(private val setter: Native.(Pointer, T) -> Unit, private val getter: Native.(Pointer) -> T) : ReadWriteProperty<CloseableNativeObject, T> {
        override fun setValue(thisRef: CloseableNativeObject, property: KProperty<*>, value: T) {
            thisRef.native { pointer -> setter(pointer, value) }
        }

        override fun getValue(thisRef: CloseableNativeObject, property: KProperty<*>): T {
            return thisRef.native { pointer -> getter(pointer) }
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

internal abstract class DelegateNativeObject internal constructor(delegate: NativeObject) : NativeObject(delegate) {
    private val closable: CloseableNativeObject = when (delegate) {
        is CloseableNativeObject -> delegate
        is DelegateNativeObject -> delegate.closable
    }

    final override fun <T> native(block: Native.(Pointer) -> T) = synchronized(closable) {
        super.native(closable, block)
    }
}

internal abstract class CloseableNativeObject internal constructor(private var pointer: Pointer) : NativeObject({ pointer }), AutoCloseable {
    @Synchronized
    final override fun <T> native(block: Native.(Pointer) -> T): T = native(this, block)

    protected abstract val destructor: Native.(Pointer) -> Unit

    @Synchronized
    override fun close() = native {
        destructor(pointer)

        pointer = 0
    }
}
