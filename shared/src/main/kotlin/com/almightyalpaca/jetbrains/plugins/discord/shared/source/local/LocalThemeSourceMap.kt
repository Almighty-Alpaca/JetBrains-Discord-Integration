package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractThemeSourceMap

class LocalThemeSourceMap(private val source: LocalSource, map: Map<String, ThemeSource>) : AbstractThemeSourceMap(map) {
    override fun createThemeMap(themes: Map<String, Theme>, default: Theme) = LocalThemeMap(themes, default)

    override fun createTheme(id: String, name: String, description: String, applications: Map<String, Long>) = LocalTheme(source, id, name, description, applications)
}
