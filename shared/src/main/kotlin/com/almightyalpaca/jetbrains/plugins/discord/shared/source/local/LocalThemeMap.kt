package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractThemeMap

class LocalThemeMap(themes: Map<String, Theme>, default: Theme) : AbstractThemeMap(themes, default)
