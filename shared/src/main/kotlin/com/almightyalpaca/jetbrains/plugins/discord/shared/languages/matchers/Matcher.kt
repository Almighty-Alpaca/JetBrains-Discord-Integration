package com.almightyalpaca.jetbrains.plugins.discord.shared.languages.matchers

sealed class Matcher {
    abstract fun matches(field: String): Boolean

    abstract class Text(private val strings: Set<String>, private val matcher: String.(String) -> Boolean) : Matcher() {
        override fun matches(field: String) = strings.any { s -> field.matcher(s) }
    }

    class StartsWith(texts: Set<String>) : Text(texts, { s -> startsWith(s, true) })

    class EndsWith(texts: Set<String>) : Text(texts, { s -> endsWith(s, true) })

    class Contains(texts: Set<String>) : Text(texts, { s -> contains(s, true) })

    class Equals(texts: Set<String>) : Text(texts, { s -> equals(s, true) })

    class RegEx(expressions: Set<String>) : Matcher() {
        private val expressions: Collection<Regex> = expressions.map { e -> e.toRegex(RegexOption.IGNORE_CASE) }
        override fun matches(field: String) = expressions.any { e -> e.containsMatchIn(field) }
    }

    abstract class Combining(
        private val matchers: Set<Matcher>,
        private val matcher: Set<Matcher>.(String) -> Boolean
    ) : Matcher() {
        override fun matches(field: String) = matchers.matcher(field)
    }

    class All(matchers: Set<Matcher>) : Combining(matchers, { s -> all { m -> m.matches(s) } })

    class Any(matchers: Set<Matcher>) : Combining(matchers, { s -> any { m -> m.matches(s) } })

    enum class Target(val id: String) {
        EXTENSION("extension"),
        NAME("name"),
        BASENAME("basename"),
        PATH("path"),
        CONTENT("content");
    }
}
