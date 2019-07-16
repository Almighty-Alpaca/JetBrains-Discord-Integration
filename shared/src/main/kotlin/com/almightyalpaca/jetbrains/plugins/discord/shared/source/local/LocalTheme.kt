/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractTheme
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.setWith
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.get
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class LocalTheme(private val source: LocalSource, id: String, name: String, description: String, applications: Map<String, Long>) : AbstractTheme(id, name, description, applications) {
    private val sets = ConcurrentHashMap<String, LocalIconSet>()

    override fun getIconSet(applicationCode: String): IconSet? {
        var set = sets[applicationCode]
        if (set == null) {
            val applicationId = applications[applicationCode]
            if (applicationId != null) {
                val icons = getIcons(applicationId)
                if (icons != null) {
                    set = LocalIconSet(source, this, applicationId, icons, applicationCode)
                    sets[applicationCode] = set
                }
            }
        }
        return set
    }

    private fun getIcons(appId: Long): Set<String>? = URL("https://discordapp.com/api/oauth2/applications/$appId/assets").get { inputStream ->
        val array = ObjectMapper().readTree(inputStream) as ArrayNode

        setWith(array.size()) { i ->
            (array[i] as ObjectNode)["name"].asText()
        }
    }
}
