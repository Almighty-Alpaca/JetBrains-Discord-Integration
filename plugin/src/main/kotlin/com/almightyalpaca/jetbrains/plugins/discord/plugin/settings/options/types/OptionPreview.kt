package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types

import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer.Renderer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.preview.JPreview
import com.intellij.util.ui.JBUI
import org.jdom.Element
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import kotlin.reflect.KProperty

fun OptionCreator<in Preview>.preview() = OptionProviderImpl(this, OptionPreview())

class OptionPreview : Option<Preview>(""), OptionCreator<Tabs> {
    lateinit var tabsKey: String
    lateinit var tabsOption: Option<out Tabs>

    val value = Preview(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value


    override fun set(key: String, option: Option<out Tabs>) {
        if (this@OptionPreview::tabsOption.isInitialized)
            throw Exception("tabs have already been set")

        tabsKey = key
        tabsOption = option
    }

    override val component by lazy {
        JPanel().apply panel@{
            layout = BoxLayout(this@panel, BoxLayout.X_AXIS)

            val previewImpl = JPreview()

            val preview = JPanel().apply innerPanel@{
                layout = BoxLayout(this@innerPanel, BoxLayout.Y_AXIS)

                add(previewImpl)
                add(Box.Filler(Dimension(0, 0), Dimension(10, Integer.MAX_VALUE), Dimension(10, Integer.MAX_VALUE)))

                border = JBUI.Borders.empty(10, 25)
            }
            add(preview)

            val other = JPanel().apply innerPanel@{
                layout = BoxLayout(this@innerPanel, BoxLayout.Y_AXIS)

                add(tabsOption.component)
                add(Box.Filler(Dimension(0, 0), Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE), Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)))
            }
            add(other)

            tabsOption.addChangeListener { tabs ->
                previewImpl.type = when (val selected = tabs.selected) {
                    0 -> Renderer.Type.APPLICATION
                    1 -> Renderer.Type.PROJECT
                    2 -> Renderer.Type.FILE
                    else -> {
                        Logger.Level.TRACE { "Unknown tab with id=$selected selected" }

                        Renderer.Type.APPLICATION
                    }
                }
            }
        }
    }

    override fun addChangeListener(listener: (Preview) -> Unit) = throw Exception("Cannot listen to preview changes")

    override var isComponentEnabled
        get() = tabsOption.isComponentEnabled
        set(value) {
            tabsOption.isComponentEnabled = value
        }
    override val isModified get() = tabsOption.isModified
    override val isDefault get() = tabsOption.isDefault
    override fun apply() = tabsOption.apply()
    override fun reset() = tabsOption.reset()
    override fun writeXml(element: Element, key: String) = tabsOption.writeXml(element, tabsKey)
    override fun readXml(element: Element, key: String) = tabsOption.readXml(element, tabsKey)

    companion object : Logging()
}

class Preview(private val option: OptionPreview) : Value(), OptionCreator<Tabs> by option {
    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Group
    }
}
