package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractThemeSourceMap

class BintrayThemeSourceMap(private val source: BintraySource, map: Map<String, ThemeSource>) : AbstractThemeSourceMap(map) {
    override fun createThemeMap(themes: Map<String, Theme>, default: Theme) = BintrayThemeMap(themes, default)

    override fun createTheme(id: String, name: String, description: String, applications: Map<String, Long>) = BintrayTheme(source, id, name, description, applications)
}
