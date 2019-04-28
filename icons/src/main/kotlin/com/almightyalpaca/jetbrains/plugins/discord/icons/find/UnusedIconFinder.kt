package com.almightyalpaca.jetbrains.plugins.discord.icons.find

import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getLocalIcons
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

fun main() = runBlocking {
    val source = LocalSource(Paths.get("../"), retry = false)
    val languages = source.getLanguages()
    val themes = source.getThemes()

    val assets = languages.stream()
        .map { language -> language.assetId }
        .filter(Objects::nonNull)
        .toSet()

    var violation = false

    for (theme in themes.keys) {
        val icons = getLocalIcons(theme)

        for (icon in icons) {
            if (icon !in assets) {
                violation = true
                println("$icon in $theme is unused")
            }
        }
    }

    if (violation)
        exitProcess(-1)
}
