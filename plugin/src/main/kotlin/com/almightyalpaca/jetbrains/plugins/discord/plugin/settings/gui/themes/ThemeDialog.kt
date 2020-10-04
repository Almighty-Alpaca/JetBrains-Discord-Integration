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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.gui.themes

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.ThemeMap
import com.intellij.openapi.ui.DialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.awt.Component
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.plaf.basic.BasicComboBoxRenderer
import kotlin.coroutines.CoroutineContext

class ThemeDialog(private val themes: ThemeMap, private val initialValue: String?) : DialogWrapper(null, true, IdeModalityType.IDE), CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob

    private lateinit var field: JComboBox<Theme>

    val value
        get() = (field.selectedItem as Theme).id

    /*
     * TODO
     * Optional: Override the getPreferredFocusedComponent() method and return the component that should be focused when the dialog is first displayed.
     * Optional: Override the getDimensionServiceKey() method to return the identifier which will be used for persisting the dialog dimensions.
     * Optional: Override the getHelpId() method to return the context help topic associated with the dialog.
     */

    init {
        init()

        title = "Themes"
    }

    override fun createCenterPanel(): JComponent? = JPanel().apply panel@{
        // val tabs = JTabbedPane().apply {
        //     for (themeChooser in themes.values) {
        //         val tab = JPanel().apply tab@{
        //
        //         }
        //         addTab(themeChooser.name, tab)
        //     }
        // }
        //
        // add(tabs)

        val renderer = object : BasicComboBoxRenderer() {
            override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

                val theme = value as Theme
                text = "<html><b>${theme.name}</b><br>${theme.description}</html>"

                return this
            }
        }
        field = JComboBox(themes.values.toTypedArray()).apply box@{
            this@box.renderer = renderer
            selectedItem = themes[initialValue]
        }

        add(field)
    }
}
