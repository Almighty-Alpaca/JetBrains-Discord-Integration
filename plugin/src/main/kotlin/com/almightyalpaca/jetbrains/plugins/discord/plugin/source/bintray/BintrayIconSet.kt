package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractIconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.get
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.retryAsync
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.net.URL
import kotlin.coroutines.CoroutineContext

class BintrayIconSet(private val source: BintraySource, theme: Theme, applicationId: Long?, icons: Set<String>, applicationCode: String) : AbstractIconSet(theme, applicationId, icons, applicationCode), CoroutineScope {
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