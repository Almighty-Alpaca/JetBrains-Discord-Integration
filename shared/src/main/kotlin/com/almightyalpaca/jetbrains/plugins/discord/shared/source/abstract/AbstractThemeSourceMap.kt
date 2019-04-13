package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSourceMap

abstract class AbstractThemeSourceMap(protected val map: Map<String, ThemeSource>) : ThemeSourceMap, Map<String, ThemeSource> by map
