package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element
import javax.swing.JComponent
import kotlin.reflect.KProperty

abstract class SimpleOption<T>(description: String, val initialValue: T) : Option<T>(description), SimpleValue.Provider<T> {
    var currentValue = initialValue

    @Suppress("LeakingThis")
    private val value = SimpleValueImpl(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>): SimpleValue<T> = value

    protected open fun transformValue(value: T): T = value

    protected abstract val componentImpl: JComponent

    override var isComponentEnabled: Boolean
        get() = component.isEnabled
        set(value) {
            component.isEnabled = value
            for (component in component.components) {
                component.isEnabled = value
            }
        }

    abstract val componentValue: T

    override val isDefault
        get() = currentValue == initialValue

    override fun writeXml(element: Element, key: String) {
        JDOMExternalizerUtil.writeField(element, key, writeString())
    }

    override fun readXml(element: Element, key: String) {
        JDOMExternalizerUtil.readField(element, key)?.let { s -> readString(s) }
    }

    open fun writeString(): String = currentValue.toString()
    open fun readString(string: String): Unit = throw Exception()
}

abstract class SimpleValue<T> : Value() {
    abstract fun get(): T
    abstract fun getComponent(): T
    abstract fun set(value: T)

    interface Provider<T> : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): SimpleValue<T>
    }
}

private class SimpleValueImpl<T>(private val option: SimpleOption<T>) : SimpleValue<T>() {
    override fun get() = option.currentValue
    override fun getComponent() = option.componentValue
    override fun set(value: T) {
        option.currentValue = value
    }
}
