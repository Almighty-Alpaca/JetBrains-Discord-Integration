package com.almightyalpaca.jetbrains.plugins.discord.shared.source.local

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractIcon

class LocalIcon(name: String, asset: Asset) : AbstractIcon(name, asset) {
    constructor(source: LocalSource, name: String, assetId: String, theme: Theme, applicationCode: String) : this(name, LocalAsset(source, assetId, theme, applicationCode))
}
