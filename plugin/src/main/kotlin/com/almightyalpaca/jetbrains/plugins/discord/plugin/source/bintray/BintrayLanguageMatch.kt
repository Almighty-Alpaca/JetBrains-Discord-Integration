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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Icon
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractLanguageMatch

class BintrayLanguageMatch(name: String, assetIds: Iterable<String>) : AbstractLanguageMatch(name, assetIds) {
    override fun findIcon(icons: IconSet): Icon? = when (icons) {
        is BintrayIconSet -> assetIds
            .find { it in icons }
            ?.let { assetId -> BintrayIcon(name, assetId, icons) }
        else -> throw RuntimeException("Can only get icons from BintrayIconSets")
    }
}
