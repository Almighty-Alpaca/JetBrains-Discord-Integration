package com.almightyalpaca.jetbrains.plugins.discord.app.source

import com.almightyalpaca.jetbrains.plugins.shared.source.LanguageSourceSet
import com.almightyalpaca.jetbrains.plugins.shared.source.SourceProvider
import com.almightyalpaca.jetbrains.plugins.shared.source.ThemeSourceSet
import com.almightyalpaca.jetbrains.plugins.shared.utils.toMap
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.intellij.openapi.application.PathManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.OkUrlFactory
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.kohsuke.github.extras.OkHttp3Connector
import java.nio.file.Paths

class GitHubSourceProvider(repositoryName: String) : SourceProvider {
    override val languages: LanguageSourceSet
    override val themes: ThemeSourceSet

    init {
        val appConfigHash = Paths.get(PathManager.getConfigPath()).toAbsolutePath().toString().hashCode()
        val cacheDir = Paths.get(FileUtils.getTempDirectoryPath(), "JetBrains-Discord-Integration/$appConfigHash/")
        val cacheSize = 1024L * 1024L * 16L  // 16MiB

        val cache = Cache(cacheDir.toFile(), cacheSize)
        val github = GitHub.connectAnonymously()
        github.connector = OkHttp3Connector(OkUrlFactory(OkHttpClient.Builder().cache(cache).build()))

        val repository = github.getRepository(repositoryName)

        languages = retrieveLanguages(repository)
        themes = retrieveThemes(repository)
    }
}

private fun retrieveLanguages(repository: GHRepository): LanguageSourceSet {
    val mapper = ObjectMapper(YAMLFactory())

    return repository.getDirectoryContent("icons/languages")
        .stream()
        .filter { c -> FilenameUtils.isExtension(c.name.toLowerCase(), "yaml") }
        .map { c ->
            val stream = repository.readBlob(c.sha)
            val node: JsonNode = mapper.readTree(stream)
            SourceProvider.Source(FilenameUtils.getBaseName(c.name).toLowerCase(), node)
        }
        .map { p -> p.id to p }
        .toMap()
}

private fun retrieveThemes(repository: GHRepository): ThemeSourceSet {
    val mapper = ObjectMapper(YAMLFactory())

    return repository.getDirectoryContent("icons/themes")
        .stream()
        .filter { c -> FilenameUtils.isExtension(c.name.toLowerCase(), "yaml") }
        .map { c ->
            val stream = repository.readBlob(c.sha)
            val node: JsonNode = mapper.readTree(stream)
            SourceProvider.Source(FilenameUtils.getBaseName(c.name).toLowerCase(), node)
        }
        .map { p -> p.id to p }
        .toMap()
}
