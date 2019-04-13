package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractIconSet

class LocalIconSet(private val source: LocalSource, theme: Theme, applicationId: Long?, icons: Set<String>, applicationCode: String) : AbstractIconSet(theme, applicationId, icons, applicationCode) {
    override fun getAsset(assetId: String): Asset? = when (assetId in this) {
        true -> LocalAsset(source, assetId, theme, applicationCode)
        false -> null
    }
}
