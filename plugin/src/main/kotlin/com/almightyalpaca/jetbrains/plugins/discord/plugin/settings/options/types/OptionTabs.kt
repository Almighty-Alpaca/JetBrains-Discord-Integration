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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionCreator
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionHolderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.OptionProviderImpl
import com.intellij.ui.components.JBTabbedPane
import org.jdom.Element
import kotlin.reflect.KProperty

fun OptionCreator<in Tabs>.tabbed() = OptionProviderImpl(this, OptionTabs())

class OptionTabs : Option<Tabs>(""), Tabs.Provider {
    internal val tabs = mutableMapOf<String, Tab>()

    private val value = Tabs(this)
    override fun getValue(thisRef: OptionHolder, property: KProperty<*>) = value

    override val component by lazy {
        JBTabbedPane().apply panel@{
            tabs.asSequence()
                .sortedBy { (_, tab) -> tab.index }
                .forEach { (name, tab) -> addTab(name, tab.component) }
        }
    }

    override var isComponentEnabled: Boolean = true
        set(value) {
            field = value

            for (tab in tabs.values) {
                tab.isComponentEnabled = value
            }
        }

    override fun addChangeListener(listener: (Tabs) -> Unit) {
        component.addChangeListener { listener(value) }

        listener(value)
    }

    override val isModified: Boolean
        get() = tabs.values.any { tab -> tab.isModified }

    override val isDefault: Boolean
        get() = tabs.values.all { tab -> tab.isDefault }

    override fun apply() {
        for (tab in tabs.values)
            tab.apply()
    }

    override fun reset() {
        for (tab in tabs.values)
            tab.reset()
    }

    override fun writeXml(element: Element, key: String) {
        for (tab in tabs.values) {
            tab.writeExternal(element)
        }
    }

    override fun readXml(element: Element, key: String) {
        for (tab in tabs.values) {
            tab.readExternal(element)
        }
    }
}

class Tabs(private val option: OptionTabs) : Value() {
    var selected: Int
        get() = option.component.selectedIndex
        set(value) {
            option.component.selectedIndex = value
        }

    operator fun get(key: String): Tab = option.tabs.computeIfAbsent(key) { Tab(option.tabs.size) }

    interface Provider : Value.Provider {
        override operator fun getValue(thisRef: OptionHolder, property: KProperty<*>): Tabs
    }
}

class Tab(val index: Int) : OptionHolderImpl() {
    operator fun invoke(block: Tab.() -> Unit): Tab {
        block()
        return this
    }
}
