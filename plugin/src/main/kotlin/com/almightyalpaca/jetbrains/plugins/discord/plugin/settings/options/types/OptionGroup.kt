package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.intellij.ui.IdeBorderFactory
import org.jdom.Element
import java.awt.Insets
import javax.swing.BoxLayout
import javax.swing.JPanel
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

fun OptionCreator<in OptionHolder>.group(description: String) = OptionProviderImpl(this, OptionGroup(description))

class OptionGroup(description: String) : Option<Group>(description), OptionHolder, Group.Provider {
    private val value = Group(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override fun addChangeListener(listener: (Group) -> Unit) = throw Exception("Cannot listen to group changes")

    override val component by lazy {
        JPanel().apply panel@{
            layout = BoxLayout(this@panel, BoxLayout.Y_AXIS)

            for (option in options.values) {
                add(option.component)
            }

            val fontHeight = getFontMetrics(font).height

            if (description.isNotEmpty())
                border = IdeBorderFactory.createTitledBorder(description, false, Insets((fontHeight * 1.5).roundToInt(), 0, fontHeight, 0))
        }
    }

    override var isComponentEnabled: Boolean = true
        set(value) {
            field = value

            for (option in options.values) {
                option.isComponentEnabled = value
            }
        }

    override val options: MutableMap<String, Option<*>> = LinkedHashMap()

    override val isModified: Boolean
        get() = options.values.any(Option<*>::isModified)
    override val isDefault: Boolean
        get() = options.values.all(Option<*>::isDefault)

    override fun apply() = super.apply()
    override fun reset() = super.reset()

    override fun writeXml(element: Element, key: String) {
        for ((childKey, option) in options) {
            if (!option.isDefault) {
                option.writeXml(element, childKey)
            }
        }
    }

    override fun readXml(element: Element, key: String) {
        for ((childKey, option) in options) {
            option.readXml(element, childKey)
        }
    }
}

class Group(private val option: OptionGroup) : Value(), OptionHolder by option {
    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Group
    }
}
