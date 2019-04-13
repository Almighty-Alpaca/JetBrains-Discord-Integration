package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeMap

abstract class AbstractThemeMap(map: Map<String, Theme>, override val default: Theme) : ThemeMap, Map<String, Theme> by map