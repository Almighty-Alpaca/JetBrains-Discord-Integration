package com.almightyalpaca.jetbrains.plugins.discord.plugin.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageSourceSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.SourceProvider
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeSourceSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.baseName
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.extension
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.toMap
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.nio.file.Files
import java.nio.file.Path

class FileSourceProvider(location: Path) : SourceProvider {
    override val languages: LanguageSourceSet by lazy { calculateLanguages(location) }
    override val themes: ThemeSourceSet by lazy { calculateThemes(location) }
}

private fun calculateLanguages(directory: Path): LanguageSourceSet {
    val mapper = ObjectMapper(YAMLFactory())

    return Files.list(directory.resolve("icons/languages"))
        .filter { p -> p.extension.toLowerCase() == "yaml" }
        .map { p ->
            val node: JsonNode = mapper.readTree(Files.newInputStream(p))
            SourceProvider.Source(p.baseName.toLowerCase(), node)
        }
        .map { p -> p.id to p }
        .toMap()
}

private fun calculateThemes(directory: Path): ThemeSourceSet {
    val mapper = ObjectMapper(YAMLFactory())

    return Files.list(directory.resolve("icons/themes"))
        .filter { p -> p.extension.toLowerCase() == "yaml" }
        .map { p ->
            val node: JsonNode = mapper.readTree(Files.newInputStream(p))
            SourceProvider.Source(p.baseName.toLowerCase(), node)
        }
        .map { p -> p.id to p }
        .toMap()
}
