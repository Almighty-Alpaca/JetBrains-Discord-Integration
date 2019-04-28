package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.stream
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.fasterxml.jackson.databind.JsonNode

interface ThemeSourceMap : Map<String, ThemeSource> {
    fun createThemeMap(themes: Map<String, Theme>, default: Theme): ThemeMap
    fun createTheme(id: String, name: String, description: String, applications: Map<String, Long>): Theme

    fun toThemeMap(): ThemeMap {
        val themes = stream()
            .filter { (key, _) -> key != "default" }
            .map { (key, value) -> key to value.asTheme() }
            .toMap()

        val defaultTheme = themes.getValue(this.getValue("default").node.textValue())

        return createThemeMap(themes, defaultTheme)
    }

    fun ThemeSource.asTheme(): Theme {
        val name: String = node["name"]?.textValue()!!
        val description: String = node["description"]?.textValue()!!
        val applications: Map<String, Long> = node["applications"].asApplications()

        return createTheme(id, name, description, applications)
    }

    private fun JsonNode.asApplications(): Map<String, Long> = when {
        isNull -> emptyMap()
        isObject -> this.fields().toMap { (key, value) -> key to value.longValue() }
        else -> throw RuntimeException()
    }
}
