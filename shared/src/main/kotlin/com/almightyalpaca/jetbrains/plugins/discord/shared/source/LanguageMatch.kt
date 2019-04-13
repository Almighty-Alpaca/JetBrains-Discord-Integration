package com.almightyalpaca.jetbrains.plugins.discord.shared.source

interface LanguageMatch {
    val name: String
    val assetIds: Iterable<String>

    fun findIcon(icons: IconSet): Icon?
}
