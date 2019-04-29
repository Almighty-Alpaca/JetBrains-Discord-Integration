@file:Suppress("DEPRECATION")

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.Option
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.GridBagConstraints
import org.jdom.Element
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Box
import javax.swing.JPanel

@Suppress("DEPRECATION")
open class OptionHolderImpl : OptionHolder {
    override val options = LinkedHashMap<String, Option<*>>()

    override val component by lazy {
        JPanel().apply panel@{
            layout = GridBagLayout()

            var y = 0
            for (option in options.values) {
                add(option.component, GridBagConstraints {
                    gridx = 0
                    gridy = y++
                    gridwidth = 1
                    gridheight = 1
                    anchor = GridBagConstraints.NORTHWEST
                    fill = GridBagConstraints.HORIZONTAL
                    weightx = 1.0
                })
            }

            add(Box.createVerticalGlue(), GridBagConstraints {
                gridx = 1
                gridy = y++
                gridwidth = 1
                gridheight = 1
                anchor = GridBagConstraints.NORTHWEST
                fill = GridBagConstraints.BOTH
                weightx = 1.0
                weighty = 1.0
            })
        }
    }

    fun readExternal(element: Element) {
        for ((key, option) in options) {
            option.readXml(element, key)
        }
    }

    fun writeExternal(element: Element) {
        for ((key, option) in options) {
            if (!option.isDefault) {
                option.writeXml(element, key)
            }
        }
    }
}
