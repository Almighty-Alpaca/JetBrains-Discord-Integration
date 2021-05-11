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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.i18n

import com.intellij.AbstractBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*
import java.util.function.Supplier

interface Message : Supplier<String>

object DiscordBundle {
    @NonNls
    private const val PATH = "com.almightyalpaca.jetbrains.plugins.discord.plugin.i18n.DiscordBundle"

    private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(PATH) }

    @Nls
    operator fun get(@PropertyKey(resourceBundle = PATH) key: String, vararg params: Any): Message = object : Message {
        override fun get(): String = AbstractBundle.messageOrDefault(bundle, key, key, *params) // This is going to be way prettier once moving to Kotlin 1.4
    }
}
