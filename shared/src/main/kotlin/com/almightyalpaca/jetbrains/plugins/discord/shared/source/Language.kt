package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.concat

interface Language {
    val id: String
    val name: String
    val parent: Language?
    val assetId: String?
    val matchers: Map<Matcher.Target, Matcher>
    val match: LanguageMatch

    fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch?
    val assetIds: Iterable<String>

    interface Simple : Language {
        val flavors: Set<Language>

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

    interface Default : Language {
        override val id: String get() = "default"
        override val parent: Language? get() = null
        override val matchers: Map<Matcher.Target, Matcher> get() = emptyMap()
        override val assetIds: Iterable<String> get() = listOf(assetId)
        override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? = null

        override val assetId: String
    }
}
