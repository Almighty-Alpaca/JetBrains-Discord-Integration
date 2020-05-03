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
