package com.almightyalpaca.jetbrains.plugins.shared.source

import com.almightyalpaca.jetbrains.plugins.shared.languages.Language
import com.almightyalpaca.jetbrains.plugins.shared.languages.LanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.languages.matchers.Matcher
import com.almightyalpaca.jetbrains.plugins.shared.utils.Set
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import java.util.*

fun LanguageSourceSet.toLanguageMap(): LanguageMap {
    val languages = mutableMapOf<String, Language>()

    for (source in this.values) {
        createLanguage(languages, source.id, source.node)
    }

    return LanguageMap(languages)
}

private fun LanguageSourceSet.createLanguage(
    languages: MutableMap<String, Language>,
    id: String,
    source: JsonNode = this.getValue(id).node,
    baseName: String? = null,
    baseAsset: String? = null,
    baseParent: Language? = null
): Language =
    languages[id] ?: when (id) {
        "default" -> createDefaultLanguage(languages, source)
        else -> createSimpleLanguage(languages, id, source, baseName, baseAsset, baseParent)
    }

private fun LanguageSourceSet.createDefaultLanguage(languages: MutableMap<String, Language>, source: JsonNode): Language {
    val name: String = source["name"]?.textValue()!!
    val asset: String = source["asset"]?.textValue()!!

    val language = Language.Default(name, asset)

    languages["default"] = language

    return language
}

private fun LanguageSourceSet.createSimpleLanguage(
    languages: MutableMap<String, Language>,
    id: String,
    source: JsonNode,
    baseName: String? = null,
    baseAsset: String? = null,
    baseParent: Language? = null
): Language {
    val name: String = source["name"]?.textValue() ?: baseName!!
    val asset: String? = source["asset"]?.textValue() ?: baseAsset

    val parentId: String? = source["parent"]?.textValue()
    val parent = parentId?.let { languages[parentId] ?: createLanguage(languages, parentId) } ?: baseParent

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

    val flavors: Set<Language> = (source["flavors"] as ArrayNode?)?.run {
        when {
            isNull -> emptySet()
            isObject -> setOf(createLanguage(languages, "$id\$1", this, name, asset, parent))
            isArray -> Set(size()) { i -> createLanguage(languages, "$id$i", this[i], name, asset, parent) }
            else -> throw RuntimeException()
        }
    } ?: emptySet()

    val language = Language.Simple(id, name, parent, asset, matchers, flavors)

    languages[id] = language

    return language
}

private fun JsonNode.asMatchers(): Set<Matcher> = when {
    isNull -> emptySet()
    isObject -> setOf(asMatcher())
    isArray -> Set(size()) { i -> this[i].asMatcher() }
    else -> throw RuntimeException()
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
    else -> throw RuntimeException()
}

private fun JsonNode.asAny() = Matcher.Any(asMatchers())

private fun JsonNode.asAll() = Matcher.All(asMatchers())
