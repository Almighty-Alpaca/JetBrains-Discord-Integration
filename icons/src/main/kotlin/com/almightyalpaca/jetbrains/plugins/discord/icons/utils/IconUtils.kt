package com.almightyalpaca.jetbrains.plugins.discord.icons.utils

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.name
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toSet
import java.nio.file.Files
import java.nio.file.Paths

fun getLocalIcons(theme: String): Set<String> {
    val path = Paths.get("themes/$theme")

    return Files.list(path)
            .filter { p -> Files.isRegularFile(p) }
            .map { p -> p.name }
            .filter { p -> p.endsWith(".png") }
            .map { p -> p.substring(0, p.length - 4) }
            .toSet()
}
