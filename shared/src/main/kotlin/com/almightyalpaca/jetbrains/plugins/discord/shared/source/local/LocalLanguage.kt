package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMatch
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguage

object LocalLanguage {
    class Simple(source: LocalSource, id: String, name: String, parent: Language?, assetId: String?, matchers: Map<Matcher.Target, Matcher>) : AbstractLanguage.Simple(id, name, parent, assetId, matchers) {
        override val match: LanguageMatch = LocalLanguageMatch(source, name, assetIds)
    }

    class Default(source: LocalSource, name: String, assetId: String) : AbstractLanguage.Default(name, assetId) {
        override val match: LanguageMatch = LocalLanguageMatch(source, name, assetIds)
    }
}
