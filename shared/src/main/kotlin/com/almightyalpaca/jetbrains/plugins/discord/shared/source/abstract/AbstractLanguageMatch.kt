package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch

abstract class AbstractLanguageMatch(override val name: String, override val assetIds: Iterable<String>) : LanguageMatch
