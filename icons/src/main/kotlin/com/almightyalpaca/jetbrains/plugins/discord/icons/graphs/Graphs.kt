package com.almightyalpaca.jetbrains.plugins.discord.icons.graphs

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.FileSourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getLocalIcons
import com.almightyalpaca.jetbrains.plugins.shared.source.toLanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.source.toThemeMap
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val provider = FileSourceProvider(Paths.get("../"))

    val languages = provider.languages.toLanguageMap()
    val themes = provider.themes.toThemeMap()

    val graphs = Paths.get("build/graphs/")
    Files.createDirectories(graphs)

    for (theme in themes.keys) {
        val icons = getLocalIcons(theme)

        val exporter = DotGraphExporter(languages, icons)

        val path = graphs.resolve("$theme.dot")

        Files.newBufferedWriter(path).use { writer ->
            exporter.writeTo(writer)
        }
    }
}
