package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguageSourceMap

class BintrayLanguageSourceMap(map: Map<String, LanguageSource>) : AbstractLanguageSourceMap(map) {
    override fun createLanguageMap(languages: Map<String, Language>): LanguageMap = BintrayLanguageMap(languages.values)

    override fun createDefaultLanguage(name: String, assetId: String): Language.Default = BintrayLanguage.Default(name, assetId)

    override fun createSimpleLanguage(fileId: String, name: String, parent: Language?, assetId: String?, matchers: Map<Matcher.Target, Matcher>): Language.Simple =
        BintrayLanguage.Simple(fileId, name, parent, assetId, matchers)
}
