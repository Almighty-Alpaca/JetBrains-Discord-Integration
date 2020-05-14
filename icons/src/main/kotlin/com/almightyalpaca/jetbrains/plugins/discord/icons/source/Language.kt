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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source

import com.almightyalpaca.jetbrains.plugins.discord.icons.matcher.Matcher

interface Language {
    val id: String
    val name: String
    val parent: Language?
    val matchers: Map<Matcher.Target, Matcher>
    val match: LanguageMatch
    val assetIds: Iterable<String>

    fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? {
        val matcher = matchers[target]
        if (matcher != null)
            if (fields.any { f -> matcher.matches(f) })
                return match

        return null
    }

    interface Simple : Language

    interface Default : Language {
        val assetId: String
    }
}
