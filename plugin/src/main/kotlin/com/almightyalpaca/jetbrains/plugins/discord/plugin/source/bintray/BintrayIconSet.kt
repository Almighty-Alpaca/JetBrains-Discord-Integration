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

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.abstract.AbstractIconSet
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.retryAsync
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.net.URL
import kotlin.coroutines.CoroutineContext

class BintrayIconSet(theme: Theme, applicationId: Long?, icons: Set<String>, applicationName: String) :
    AbstractIconSet(theme, applicationId, icons, applicationName), CoroutineScope {
    private val parentJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    override fun getAsset(assetId: String): Asset? = when (assetId in this) {
        true -> BintrayAsset(assetId, this)
        false -> null
    }

    internal val iconIdJob = retryAsync { readIconIds() }

    private fun readIconIds(): Map<String, Long>? = when (applicationId) {
        null -> null
        else -> URL("https://discordapp.com/api/oauth2/applications/$applicationId/assets").get { inputStream ->
            ObjectMapper().readTree(inputStream)
                .map { node -> node as ObjectNode }
                .map { node -> node["name"].asText() to node["id"].asLong() }
                .toMap()
        }
    }
}
