package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.concat

sealed class AbstractLanguage(override val id: String, override val name: String, override val assetId: String?) : Language {
    abstract override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch?

    abstract class Simple(id: String, name: String, override val parent: Language?, assetId: String?, override val matchers: Map<Matcher.Target, Matcher>, override val flavors: Set<Language>)
        : AbstractLanguage(id, name, assetId), Language.Simple {

        override val assetIds: Iterable<String>
            get() = concat(assetId, parent?.assetIds)

        override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? {
            val matcher = matchers[target]
            if (matcher != null)
                if (fields.any { f -> matcher.matches(f) })
                    return match

            for (flavor in flavors) {
                val flavorMatch = flavor.findMatch(target, fields)
                if (flavorMatch != null)
                    return flavorMatch
            }

            return null
        }
    }

    abstract class Default(name: String, override val assetId: String) : AbstractLanguage("default", name, assetId), Language.Default {
        override val id: String = "default"
        override val assetIds = listOf(assetId)

        override fun findMatch(target: Matcher.Target, fields: Collection<String>) = null
    }
}
