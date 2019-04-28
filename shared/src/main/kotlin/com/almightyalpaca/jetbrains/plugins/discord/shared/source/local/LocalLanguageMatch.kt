package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Icon
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguageMatch

class LocalLanguageMatch(private val source: LocalSource, name: String, assetIds: Iterable<String>) : AbstractLanguageMatch(name, assetIds) {
    override fun findIcon(icons: IconSet): Icon? = assetIds.find { it in icons }?.let { assetId -> LocalIcon(source, name, assetId, icons.theme, icons.applicationCode) }
}
