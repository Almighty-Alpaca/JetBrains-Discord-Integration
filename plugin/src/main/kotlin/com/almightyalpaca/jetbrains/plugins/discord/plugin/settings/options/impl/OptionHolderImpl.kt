@file:Suppress("DEPRECATION")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option
import com.intellij.openapi.util.JDOMExternalizable
import org.jdom.Element
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

@Suppress("DEPRECATION")
open class OptionHolderImpl(outer: Boolean = true) : OptionHolder, JDOMExternalizable {
    override val options = LinkedHashMap<String, Option<*>>()

    override val component by lazy {
        JPanel().apply panel@{
            layout = BoxLayout(this@panel, BoxLayout.Y_AXIS)

            for (option in options.values) {
                add(option.component)
            }

            if (outer)
                add(Box.Filler(Dimension(0, 0), Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE), Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)))
        }
    }

    override fun readExternal(element: Element) {
        for ((key, option) in options) {
            option.readXml(element, key)
        }
    }

    override fun writeExternal(element: Element) {
        for ((key, option) in options) {
            if (!option.isDefault) {
                option.writeXml(element, key)
            }
        }
    }
}
