/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch

abstract class AbstractLanguageMap(languages: Collection<Language>) : LanguageMap, Collection<Language> by languages {
    override val default: Language.Default = find { l -> l.id == "default" } as Language.Default

    override fun findLanguage(provider: Matcher.Target.Provider): LanguageMatch {
        for (target in Matcher.Target.values()) {
            val fields = provider.getField(target)
            for (language in this) {
                val match = language.findMatch(target, fields)
                if (match != null) {
                    return match
                }
            }
        }

        return default.match
    }
}
