package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
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

fun createErrorMessage(message: String): JComponent = JPanel().apply warning@{
    this@warning.layout = BoxLayout(this@warning, BoxLayout.X_AXIS)

    background = Color(255, 25, 25, 150)

    add(JBLabel(AllIcons.General.ErrorDialog))
    add(Box.createHorizontalStrut(10))
    add(JBLabel("<html>$message</html>"))
    add(Box.createHorizontalGlue())

    border = JBUI.Borders.merge(JBUI.Borders.empty(10), JBUI.Borders.customLine(Color(255, 25, 25, 200)),true)
}
