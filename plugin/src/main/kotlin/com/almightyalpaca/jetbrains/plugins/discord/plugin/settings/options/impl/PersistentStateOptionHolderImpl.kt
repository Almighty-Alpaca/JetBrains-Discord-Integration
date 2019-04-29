package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl

import com.intellij.openapi.components.PersistentStateComponent
import org.jdom.Element

abstract class PersistentStateOptionHolderImpl : PersistentStateComponent<Element>, OptionHolderImpl() {
    override fun getState(): Element? {
        val state = Element("dummy")

        writeExternal(state)

        return state
    }

    override fun loadState(state: Element): Unit = readExternal(state)
}
