package com.almightyalpaca.jetbrains.plugins.discord.shared.themes

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.DelegateMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap

class ThemeMap(themes: Set<Theme>, defaultTheme: Theme) : DelegateMap<String, Theme>(generateBackingMap(themes, defaultTheme)) {
    override fun get(key: String): Theme = map.getValue(key)

    companion object {
        val EMPTY = ThemeMap(emptySet(), Theme.EMPTY)
    }
}

fun generateBackingMap(themes: Set<Theme>, defaultTheme: Theme): Map<String, Theme> = themes.toMap { t -> t.id to t }.withDefault { defaultTheme }
