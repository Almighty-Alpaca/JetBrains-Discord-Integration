package com.almightyalpaca.jetbrains.plugins.discord.icons.validator

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.FileSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getLocalIcons
import com.almightyalpaca.jetbrains.plugins.shared.languages.LanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.source.toLanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.source.toThemeMap
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main() {
    val provider = FileSourceProvider(Paths.get("../"))
    val languages = provider.languages.toLanguageMap()
    val themes = provider.themes.toThemeMap()

    var violation = false

    for (theme in themes.keys) {
        val icons = getLocalIcons(theme)

        violation = validate(languages, theme, icons)
    }

    if (violation)
        exitProcess(-1)
}

private fun validate(languages: LanguageMap, theme: String, icons: Set<String>): Boolean {
    var violation = false

    for (language in languages) {
        if (language.isMatching && language.assets.none { asset -> asset in icons }) {
            violation = true

            println("No icon for ${language.name} found in $theme! Hierarchy: ${language.assets.joinToString(separator = " > ")}")
        }
    }

    return violation
}
