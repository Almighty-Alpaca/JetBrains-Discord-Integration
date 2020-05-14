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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.icons.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.LanguageMatch
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.concat

sealed class AbstractLanguage(final override val id: String, final override val name: String) : Language {
    override fun toString(): String {
        return "AbstractLanguage(id='$id', name='$name')"
    }

    abstract class Simple(id: String, name: String, final override val parent: Language?, assetIds: List<String>?, final override val matchers: Map<Matcher.Target, Matcher>) :
        AbstractLanguage(id, name), Language.Simple {

        final override val assetIds: Iterable<String> = concat(assetIds, parent?.assetIds)
    }

    abstract class Default(name: String, final override val assetId: String) : AbstractLanguage("default", name), Language.Default {
        final override val assetIds: Iterable<String> = listOf(assetId)
        final override val parent: Language? = null
        final override val matchers: Map<Matcher.Target, Matcher> get() = emptyMap()
        final override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? = null
    }
}
