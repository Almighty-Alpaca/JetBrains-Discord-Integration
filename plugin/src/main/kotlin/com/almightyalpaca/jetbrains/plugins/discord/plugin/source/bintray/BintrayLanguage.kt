package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguage

object BintrayLanguage {
    class Simple(id: String, name: String, parent: Language?, assetIds: List<String>?, matchers: Map<Matcher.Target, Matcher>) : AbstractLanguage.Simple(id, name, parent, assetIds, matchers) {
        override val match: LanguageMatch = BintrayLanguageMatch(name, this.assetIds)
    }

    class Default(name: String, assetId: String) : AbstractLanguage.Default(name, assetId) {
        override val match: LanguageMatch = BintrayLanguageMatch(name, assetIds)
    }
}
