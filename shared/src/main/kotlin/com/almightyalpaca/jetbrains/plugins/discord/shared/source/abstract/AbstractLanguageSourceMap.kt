package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSourceMap

abstract class AbstractLanguageSourceMap(protected val map: Map<String, LanguageSource>) : LanguageSourceMap, Map<String, LanguageSource> by map
