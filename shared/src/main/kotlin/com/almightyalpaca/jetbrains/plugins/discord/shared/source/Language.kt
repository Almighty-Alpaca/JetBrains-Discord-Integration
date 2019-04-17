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
    val assetIds: Iterable<String>

    fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? {
        val matcher = matchers[target]
        if (matcher != null)
            if (fields.any { f -> matcher.matches(f) })
                return match

        return null
    }

    interface Simple : Language {    }

    interface Default : Language {
        override val assetId: String
    }
}
