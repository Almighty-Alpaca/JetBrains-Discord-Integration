package com.almightyalpaca.jetbrains.plugins.shared.languages

import com.almightyalpaca.jetbrains.plugins.shared.languages.matchers.Matcher
import com.almightyalpaca.jetbrains.plugins.shared.themes.icons.IconSet
import com.almightyalpaca.jetbrains.plugins.shared.utils.DelegateCollection
import com.almightyalpaca.jetbrains.plugins.shared.utils.concat

sealed class Language(val id: String, val name: String, val parent: Language?, val asset: String?, val matchers: Map<Matcher.Target, Matcher>) {
    abstract fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch?
    abstract val assets: Iterable<String>
    inline val isMatching: Boolean
        get() = matchers.isNotEmpty()

    class Simple(id: String, name: String, parent: Language?, asset: String?, matchers: Map<Matcher.Target, Matcher>, val flavors: Set<Language>) :
        Language(id, name, parent, asset, matchers) {
        override val assets: Iterable<String> by lazy { concat(asset, parent?.assets) }
        override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? {
            val matcher = matchers[target]
            if (matcher != null)
                if (fields.any { f -> matcher.matches(f) })
                    return LanguageMatch(name, assets)

            for (flavor in flavors) {
                val match = flavor.findMatch(target, fields)
                if (match != null)
                    return match
            }

            return null
        }
    }

    class Default(name: String, asset: String) : Language("default", name, null, asset, emptyMap()) {
        override val assets: Iterable<String> = listOf(asset)
        val match = LanguageMatch(name, assets)
        override fun findMatch(target: Matcher.Target, fields: Collection<String>): LanguageMatch? = null
    }
}

typealias FieldProvider = (Matcher.Target) -> Collection<String>

class LanguageMap(languages: Map<String, Language>) : DelegateCollection<Language>(languages.values) {
    val default: Language.Default = languages["default"] as Language.Default

    fun findLanguage(provider: FieldProvider): LanguageMatch {
        for (target in Matcher.Target.values()) {
            val fields = provider(target)
            for (language in this) {
                val match = language.findMatch(target, fields)
                if (match != null) {
                    return match
                }
            }
        }

        return default.match
    }

    companion object {
        val EMPTY = LanguageMap(mapOf("default" to Language.Default("default", "default")))
    }
}

data class LanguageMatch(val name: String, val assets: Iterable<String>) {
    fun findIcon(icons: IconSet) = Icon(icons.appId, name, assets.find { asset -> asset in icons } ?: throw RuntimeException())
}

data class Icon(val appId: Long, val name: String, val asset: String)
