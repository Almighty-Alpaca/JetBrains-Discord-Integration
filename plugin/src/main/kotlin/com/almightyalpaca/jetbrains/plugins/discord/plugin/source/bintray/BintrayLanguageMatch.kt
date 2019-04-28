package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Icon
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractLanguageMatch

class BintrayLanguageMatch(name: String, assetIds: Iterable<String>) : AbstractLanguageMatch(name, assetIds) {
    override fun findIcon(icons: IconSet): Icon? = when (icons) {
        is BintrayIconSet -> assetIds
            .find { it in icons }
            ?.let { assetId -> BintrayIcon(name, assetId, icons) }
        else -> throw RuntimeException("Can only get icons from BintrayIconSets")
    }
}
