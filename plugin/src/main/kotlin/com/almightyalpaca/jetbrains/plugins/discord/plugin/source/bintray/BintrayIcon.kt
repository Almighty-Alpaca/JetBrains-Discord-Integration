package com.almightyalpaca.jetbrains.plugins.discord.plugin.source.bintray

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract.AbstractIcon

class BintrayIcon(name: String, asset: Asset) : AbstractIcon(name, asset) {
    constructor(name: String, assetId: String, iconSet: BintrayIconSet) : this(name, BintrayAsset(assetId, iconSet))
}
