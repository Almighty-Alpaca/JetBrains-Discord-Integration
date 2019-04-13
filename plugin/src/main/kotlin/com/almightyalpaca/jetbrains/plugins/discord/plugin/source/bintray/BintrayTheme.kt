package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractTheme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalIconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.Set
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.get
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class BintrayTheme(private val source: BintraySource, id: String, name: String, description: String, applications: Map<String, Long>) : AbstractTheme(id, name, description, applications) {
    private val sets = ConcurrentHashMap<String, BintrayIconSet>()

    override fun getIconSet(applicationCode: String): IconSet? {
        var set = sets[applicationCode]
        if (set == null) {
            val applicationId = applications[applicationCode]
            if (applicationId != null) {
                val icons = getIcons(applicationId)
                if (icons != null) {
                    set = BintrayIconSet(source, this, applicationId, icons, applicationCode)
                    sets[applicationCode] = set
                }
            }
        }
        return set
    }

    private fun getIcons(appId: Long): Set<String>? = URL("https://discordapp.com/api/oauth2/applications/$appId/assets").get { inputStream ->
        val array = ObjectMapper().readTree(inputStream) as ArrayNode

        Set(array.size()) { i ->
            (array[i] as ObjectNode)["name"].asText()
        }
    }
}
