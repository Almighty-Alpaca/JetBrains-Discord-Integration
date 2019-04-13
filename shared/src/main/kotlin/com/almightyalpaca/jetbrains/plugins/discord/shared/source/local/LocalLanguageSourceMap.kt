package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguageSourceMap

class LocalLanguageSourceMap(private val source: LocalSource, map: Map<String, LanguageSource>) : AbstractLanguageSourceMap(map) {
    override fun createLanguageMap(languages: Map<String, Language>): LanguageMap = LocalLanguageMap(languages.values)

    override fun createDefaultLanguage(name: String, assetId: String): Language.Default = LocalLanguage.Default(source, name, assetId)

    override fun createSimpleLanguage(id: String, name: String, parent: Language?, assetId: String?, matchers: Map<Matcher.Target, Matcher>, flavors: Set<Language>): Language.Simple = LocalLanguage.Simple(source, id, name, parent, assetId, matchers, flavors)
}