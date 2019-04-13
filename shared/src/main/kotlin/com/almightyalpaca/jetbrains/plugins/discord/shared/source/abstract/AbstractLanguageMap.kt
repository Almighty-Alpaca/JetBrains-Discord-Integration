package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.FieldProvider
import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch

abstract class AbstractLanguageMap(languages: Collection<Language>) : LanguageMap, Collection<Language> by languages {
    override val default: Language.Default = find { l -> l.id == "default" } as Language.Default

    override fun findLanguage(provider: FieldProvider): LanguageMatch {
        for (target in Matcher.Target.values()) {
            val fields = provider.getField(target)
            for (language in this) {
                val match = language.findMatch(target, fields)
                if (match != null) {
                    return match
                }
            }
        }

        return default.match
    }
}