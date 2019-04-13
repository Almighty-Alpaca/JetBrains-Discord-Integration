package com.almightyalpaca.jetbrains.plugins.discord.shared.source.abstract

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Asset
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme

abstract class AbstractAsset(override val id: String, override val theme: Theme) : Asset
