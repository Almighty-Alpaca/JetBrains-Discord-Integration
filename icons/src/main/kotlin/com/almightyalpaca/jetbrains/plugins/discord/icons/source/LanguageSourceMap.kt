/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.almightyalpaca.jetbrains.plugins.discord.icons.source

import com.almightyalpaca.jetbrains.plugins.discord.icons.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.setWith
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import java.util.*

interface LanguageSourceMap : Map<String, LanguageSource> {
    fun createLanguageMap(languages: Map<String, Language>): LanguageMap
    fun createDefaultLanguage(name: String, assetId: String): Language.Default
    fun createSimpleLanguage(fileId: String, name: String, parent: Language?, assetIds: List<String>?, matchers: Map<Matcher.Target, Matcher>): Language.Simple

    fun toLanguageMap(): LanguageMap {
        val languages = mutableMapOf<String, Language>()

        for (source in this.values) {
            createLanguage(languages, source.id, source.node)
        }

        return createLanguageMap(languages)
    }

    private fun getLanguage(languages: MutableMap<String, Language>, id: String): Language = if (id.contains('/')) {
        createLanguage(languages, id.substringBefore('/'))
        languages[id] ?: throw Exception("Missing language")
    } else {
        createLanguage(languages, id)
    }

    private fun createLanguage(
        languages: MutableMap<String, Language>,
        id: String,
        source: JsonNode = this.getValue(id).node,
        baseId: String? = null,
        baseName: String? = null,
        baseAssetIds: List<String>? = null,
        baseParent: Language? = null
    ): Language = languages[id] ?: try {
        when (id) {
            "default" -> createDefaultLanguage(languages, source)
            else -> createSimpleLanguage(languages, id, source, baseId, baseName, baseAssetIds, baseParent)
        }
    } catch (e: Exception) {
        throw Exception("Error while parsing ${(baseId?.plus("/") ?: "") + (source["id"]?.textValue() ?: id)}", e)
    }

    private fun createDefaultLanguage(languages: MutableMap<String, Language>, source: JsonNode): Language {
        val name: String = source["name"]?.textValue() ?: throw Exception("Missing name")
        val assetId: String = source["asset"]?.textValue() ?: throw Exception("Missing asset")

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
        baseAssetIds: List<String>? = null,
        baseParent: Language? = null
    ): Language {
        @Suppress("NAME_SHADOWING")
        val id: String = (baseId?.plus("/") ?: "") + (source["id"]?.textValue() ?: id)
        val name: String = source["name"]?.textValue() ?: baseName ?: throw Exception("Missing name")
        val assetIds: MutableList<String> = mutableListOf()
        source["asset"]?.let { node -> assetIds.addAll(node.asOrderedStrings()) }
        baseAssetIds?.let { baseAssets -> assetIds.addAll(baseAssets) }

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

        val language = createSimpleLanguage(id, name, parent, assetIds, matchers)
        languages[id] = language

        (source["flavors"] as ArrayNode?)?.run {
            when {
                isObject -> createLanguage(languages, "1", this, id, name, assetIds, parent)
                isArray -> repeat(size()) { i ->
                    val flavor = createLanguage(languages, "$i", this[i], id, name, assetIds, parent)
                    flavor.id to flavor
                }
                else -> throw RuntimeException()
            }
        }

        return language
    }

    private fun JsonNode.asOrderedStrings(): List<String> = when {
        isNull -> emptyList()
        isTextual -> listOf(asText())
        isArray -> List(size()) { i -> this[i].asText() }
        else -> throw RuntimeException("invalid type")
    }

    private fun JsonNode.asMatchers(): Set<Matcher> = when {
        isNull -> emptySet()
        isObject -> setOf(asMatcher())
        isArray -> setWith(size()) { i -> this[i].asMatcher() }
        else -> throw RuntimeException("invalid type")
    }

    private fun JsonNode.asMatcher(): Matcher {
        this["startsWith"]?.run { return asStartsWithMatcher() }
        this["endsWith"]?.run { return asEndsWithMatcher() }
        this["contains"]?.run { return asContainsMatcher() }
        this["equals"]?.run { return asEqualsMatcher() }
        this["regex"]?.run { return asRegExMatcher() }

        this["any"]?.run { return asAny() }
        this["all"]?.run { return asAll() }

        throw RuntimeException("Unknown matcher type")
    }

    private fun JsonNode.asStartsWithMatcher() = Matcher.Text.StartsWith(asStrings())

    private fun JsonNode.asEndsWithMatcher() = Matcher.Text.EndsWith(asStrings())

    private fun JsonNode.asContainsMatcher() = Matcher.Text.Contains(asStrings())

    private fun JsonNode.asEqualsMatcher() = Matcher.Text.Equals(asStrings())

    private fun JsonNode.asRegExMatcher() = Matcher.Text.RegEx(asStrings())

    private fun JsonNode.asStrings(): Set<String> = when {
        isNull -> emptySet()
        isTextual -> setOf(asText())
        isArray -> setWith(size()) { i -> this[i].asText() }
        else -> throw RuntimeException("invalid type")
    }

    private fun JsonNode.asAny() = Matcher.Combining.Any(asMatchers())

    private fun JsonNode.asAll() = Matcher.Combining.All(asMatchers())
}
