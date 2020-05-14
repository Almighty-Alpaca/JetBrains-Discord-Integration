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

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractTheme
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.setWith
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class BintrayTheme(id: String, name: String, description: String, applications: Map<String, Long>) : AbstractTheme(id, name, description, applications) {
    private val sets = ConcurrentHashMap<String, BintrayIconSet>()

    override fun getIconSet(applicationName: String): IconSet? {
        var set = sets[applicationName]
        if (set == null) {
            val applicationId = applications[applicationName]
            if (applicationId != null) {
                val icons = getIcons(applicationId)
                if (icons != null) {
                    set = BintrayIconSet(this, applicationId, icons, applicationName)
                    sets[applicationName] = set
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
