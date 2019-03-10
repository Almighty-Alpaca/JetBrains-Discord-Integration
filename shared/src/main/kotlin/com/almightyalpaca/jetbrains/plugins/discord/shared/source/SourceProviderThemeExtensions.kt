package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.ThemeMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.stream
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.fasterxml.jackson.databind.JsonNode

fun ThemeSourceSet.toThemeMap(): ThemeMap {
    val themes = stream()
        .filter { (key, _) -> key != "default" }
        .map { (key, value) -> key to value.asTheme() }
        .toMap()

    val defaultTheme = themes.getValue(this.getValue("default").node.textValue())

    return ThemeMap(themes.values.toSet(), defaultTheme)
}

fun SourceProvider.Source.asTheme(): Theme {
    val name: String = node["name"]?.textValue()!!
    val description: String = node["description"]?.textValue()!!
    val applications: Map<String, Long> = node["applications"].asApplications()

    return Theme(id, name, description, applications)
}

private fun JsonNode.asApplications(): Map<String, Long> = when {
    isNull -> emptyMap()
    isObject -> this.fields().toMap { (key, value) -> key to value.longValue() }
    else -> throw RuntimeException()
}
