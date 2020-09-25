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

package com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractTheme
import org.apache.commons.io.FilenameUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class ClasspathTheme(private val source: ClasspathSource, id: String, name: String, description: String, applications: Map<String, Long>) :
    AbstractTheme(id, name, description, applications) {
    private val sets = ConcurrentHashMap<String, ClasspathIconSet>()

    override fun getIconSet(applicationName: String): IconSet? {
        var set = sets[applicationName]
        if (set == null) {
            val applicationId = applications[applicationName]
            if (applicationId != null) {
                val resources = source.listResources("${source.pathThemes}/$id", ".png")
                val icons = resources
                    .map(FilenameUtils::getBaseName)
                    .toSet() + "application"
                set = ClasspathIconSet(source, this, applicationId, icons, applicationName)
                sets[applicationName] = set
            }
        }
        return set
    }
}
