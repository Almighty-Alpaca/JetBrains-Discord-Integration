package com.almightyalpaca.jetbrains.plugins.discord.shared.source

interface IconSet : Set<String> {
    val theme: Theme
    val applicationId: Long?
    val applicationCode: String

    fun getAsset(assetId: String): Asset?
}
