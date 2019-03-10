package com.almightyalpaca.jetbrains.plugins.discord.shared.themes.icons

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.DelegateSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.HTTP
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.Set
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.URL

class IconSet(val appId: Long) : DelegateSet<String>(getIcons(appId))

private fun getIcons(appId: Long): Set<String> = HTTP.get(URL("https://discordapp.com/api/oauth2/applications/$appId/assets")) { inputStream ->
    val array = ObjectMapper().readTree(inputStream) as ArrayNode

    Set(array.size()) { i ->
        (array[i] as ObjectNode)["name"].asText()
    }
}
