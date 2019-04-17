package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.concat

sealed class AbstractLanguage(final override val id: String, final override val name: String) : Language {
    abstract class Simple(id: String, name: String, final override val parent: Language?, final override val assetId: String?, final override val matchers: Map<Matcher.Target, Matcher>)
        : AbstractLanguage(id, name), Language.Simple {

        override val assetIds: Iterable<String> = concat(assetId, parent?.assetIds)
    }

    abstract class Default(name: String, final override val assetId: String) : AbstractLanguage("default", name), Language.Default {
        final override val assetIds: Iterable<String> = listOf(assetId)
        final override val parent: Language? = null
        final override val matchers: Map<Matcher.Target, Matcher> get() = emptyMap()
        final override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? = null
    }
}
