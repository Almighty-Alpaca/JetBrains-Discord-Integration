package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.util.ui.JBUI
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

fun boxLayoutPanel(vararg components: Component, horizontal: Boolean = true) = JPanel().apply panel@{
    val axis = when (horizontal) {
        true -> BoxLayout.X_AXIS
        false -> BoxLayout.Y_AXIS
    }

    layout = BoxLayout(this@panel, axis)

    for (component in components)
        add(component)

    when (horizontal) {
        true -> add(Box.createHorizontalGlue())
        false -> add(Box.createVerticalGlue())
    }

    border = JBUI.Borders.empty(5, 0)
}
