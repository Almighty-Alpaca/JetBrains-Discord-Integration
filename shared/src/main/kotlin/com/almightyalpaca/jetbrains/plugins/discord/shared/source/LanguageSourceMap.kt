package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.Set
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import java.util.*

interface LanguageSourceMap : Map<String, LanguageSource> {
    fun createLanguageMap(languages: Map<String, Language>): LanguageMap
    fun createDefaultLanguage(name: String, assetId: String): Language.Default
    fun createSimpleLanguage(fileId: String, name: String, parent: Language?, assetId: String?, matchers: Map<Matcher.Target, Matcher>): Language.Simple

    fun toLanguageMap(): LanguageMap {
        val languages = mutableMapOf<String, Language>()

        for (source in this.values) {
            createLanguage(languages, source.id, source.node)
        }

        return createLanguageMap(languages)
    }

    private fun getLanguage(languages: MutableMap<String, Language>, id: String): Language = if (id.contains('/')) {
        createLanguage(languages, id.substringBefore('/'))
        languages[id]!!
    } else {
        createLanguage(languages, id)
    }

    private fun createLanguage(
            languages: MutableMap<String, Language>,
            id: String,
            source: JsonNode = this.getValue(id).node,
            baseId: String? = null,
            baseName: String? = null,
            baseAssetId: String? = null,
            baseParent: Language? = null
    ): Language = languages[id] ?: when (id) {
        "default" -> createDefaultLanguage(languages, source)
        else -> createSimpleLanguage(languages, id, source, baseId, baseName, baseAssetId, baseParent)
    }

    private fun createDefaultLanguage(languages: MutableMap<String, Language>, source: JsonNode): Language {
        val name: String = source["name"]?.textValue()!!
        val assetId: String = source["asset"]?.textValue()!!

        val language = createDefaultLanguage(name, assetId)

        languages["default"] = language

        return language
    }

    private fun createSimpleLanguage(
            languages: MutableMap<String, Language>,
            id: String,
            source: JsonNode,
            baseId: String? = null,
            baseName: String? = null,
            baseAssetId: String? = null,
            baseParent: Language? = null
    ): Language {
        @Suppress("NAME_SHADOWING")
        val id: String = (baseId?.plus("/") ?: "") + (source["id"]?.textValue() ?: id)
        val name: String = source["name"]?.textValue() ?: baseName!!
        val assetId: String? = source["asset"]?.textValue() ?: baseAssetId

        val parentId: String? = source["parent"]?.textValue()
        val parent = parentId?.let { languages[parentId] ?: getLanguage(languages, parentId) } ?: baseParent

        val matchers: Map<Matcher.Target, Matcher> = source["match"]?.let { match ->
            EnumMap<Matcher.Target, Matcher>(Matcher.Target::class.java).apply {
                for (target in Matcher.Target.values()) {
                    val targetMatchers = match[target.id]
                    if (targetMatchers != null) {
                        this[target] = targetMatchers.asAny()
                    }
                }
            }
        } ?: emptyMap()


        val language = createSimpleLanguage(id, name, parent, assetId, matchers)
        languages[id] = language

        (source["flavors"] as ArrayNode?)?.run {
            when {
                isObject -> createLanguage(languages, "1", this, id, name, assetId, parent)
                isArray -> repeat(size()) { i ->
                    val language = createLanguage(languages, "$i", this[i], id, name, assetId, parent)
                    language.id to language
                }
                else -> throw RuntimeException()
            }
        }

        return language
    }

    private fun JsonNode.asMatchers(): Set<Matcher> = when {
        isNull -> emptySet()
        isObject -> setOf(asMatcher())
        isArray -> Set(size()) { i -> this[i].asMatcher() }
        else -> throw RuntimeException("invalid type")
    }

    private fun JsonNode.asMatcher(): Matcher {
        this["startsWith"]?.run { return asStartsWith() }
        this["endsWith"]?.run { return asEndsWith() }
        this["contains"]?.run { return asContains() }
        this["equals"]?.run { return asEquals() }
        this["regex"]?.run { return asRegEx() }

        this["any"]?.run { return asAny() }
        this["all"]?.run { return asAll() }

        throw RuntimeException("Unknown matcher type")
    }

    private fun JsonNode.asStartsWith() = Matcher.StartsWith(asStrings())

    private fun JsonNode.asEndsWith() = Matcher.EndsWith(asStrings())

    private fun JsonNode.asContains() = Matcher.Contains(asStrings())

    private fun JsonNode.asEquals() = Matcher.Equals(asStrings())

    private fun JsonNode.asRegEx() = Matcher.RegEx(asStrings())

    private fun JsonNode.asStrings(): Set<String> = when {
        isNull -> emptySet()
        isTextual -> setOf(asText())
        isArray -> Set(size()) { i -> this[i].asText() }
        else -> throw RuntimeException("invalid type")
    }

    private fun JsonNode.asAny() = Matcher.Any(asMatchers())

    private fun JsonNode.asAll() = Matcher.All(asMatchers())
}
