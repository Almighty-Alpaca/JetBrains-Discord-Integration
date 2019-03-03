package com.almightyalpaca.jetbrains.plugins.shared.source

import com.fasterxml.jackson.databind.JsonNode

typealias LanguageSourceSet = Map<String, SourceProvider.Source>
typealias ThemeSourceSet = Map<String, SourceProvider.Source>

interface SourceProvider {
    val languages: Map<String, Source>
    val themes: Map<String, Source>

    class Source(val id: String, val node: JsonNode)
}
