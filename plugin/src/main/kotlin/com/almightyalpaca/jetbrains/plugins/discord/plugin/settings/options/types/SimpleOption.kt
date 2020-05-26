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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element
import javax.swing.JComponent
import kotlin.reflect.KProperty

abstract class SimpleOption<T>(text: String, val description: String? = null, protected val initialValue: T) :
    Option<T>(text), SimpleValue.Provider<T> {
    var currentValue = initialValue

    @Suppress("LeakingThis")
    protected open val value: SimpleValue<T> = SimpleValueImpl(this)

    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    protected open fun transformValue(value: T): T = value

    abstract override val component: JComponent

    protected abstract val componentImpl: JComponent

    override var isComponentEnabled: Boolean
        get() = component.isEnabled
        set(value) {
            component.isEnabled = value
            for (component in component.components) {
                component.isEnabled = value
            }
        }

    abstract var componentValue: T

    override val isDefault
        get() = currentValue == initialValue

    override fun writeXml(element: Element, key: String) {
        JDOMExternalizerUtil.writeField(element, key, writeString())
    }

    override fun readXml(element: Element, key: String) {
        JDOMExternalizerUtil.readField(element, key)?.let { s -> readString(s) }
    }

    open fun writeString(): String = currentValue.toString()
    abstract fun readString(string: String)
}

abstract class SimpleValue<T> : Value() {
    abstract fun getStoredValue(): T
    abstract fun getPreviewValue(): T

    fun getValue(mode: Renderer.Mode) = when (mode) {
        Renderer.Mode.PREVIEW -> getPreviewValue()
        Renderer.Mode.NORMAL -> getStoredValue()
    }

    abstract fun setStoredValue(value: T)
    abstract fun setPreviewValue(value: T)

    fun setValue(mode: Renderer.Mode, value: T) = when (mode) {
        Renderer.Mode.NORMAL -> {
            setStoredValue(value)
            setPreviewValue(value)
        }
        Renderer.Mode.PREVIEW -> {
            setPreviewValue(value)
        }
    }

    fun updateStoredValue(block: (T) -> T) = setStoredValue(block(getStoredValue()))
    fun updatePreviewValue(block: (T) -> T) = setPreviewValue(block(getPreviewValue()))
    fun updateValue(mode: Renderer.Mode, block: (T) -> T) = setValue(mode, block(getValue(mode)))

    abstract val text: String
    abstract val description: String?

    interface Provider<T> : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): SimpleValue<T>
    }
}

open class SimpleValueImpl<T>(protected open val option: SimpleOption<T>) : SimpleValue<T>() {
    override fun getStoredValue() = option.currentValue
    override fun getPreviewValue() = option.componentValue
    override fun setStoredValue(value: T) {
        option.currentValue = value
    }

    override fun setPreviewValue(value: T) {
        option.componentValue = value
    }

    override val text: String
        get() = option.text

    override val description: String?
        get() = option.description
}
