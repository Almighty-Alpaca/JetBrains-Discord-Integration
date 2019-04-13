package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.FieldProvider

interface LanguageMap : Collection<Language> {
    val default: Language.Default
    fun findLanguage(provider: FieldProvider): LanguageMatch
}
