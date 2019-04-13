package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractThemeMap

class BintrayThemeMap(themes: Map<String, Theme>, default: Theme) : AbstractThemeMap(themes, default)
