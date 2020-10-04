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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.validator

import com.almightyalpaca.jetbrains.plugins.discord.icons.matcher.Matcher
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.classpath.ClasspathSource
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() = runBlocking {
    val source = ClasspathSource("discord", retry = false)
    val languages = source.getLanguages()

    var violation = false

    languages
        .asSequence()
        .flatMap { language ->
            Matcher.Target.values()
                .asSequence()
                .flatMap { target ->
                    language.matchers[target]
                        .unwrap()
                        .map { matcher -> matcher to language }
                        .map { target to it }
                }

        }
        .map { Request(it.first, it.second.first.first, it.second.first.second, it.second.second) }
        .groupBy { it.target to it.string }
        .asSequence()
        .map { Result(it.key.first, it.key.second, it.value.map(Request::matcher).first(), it.value.map(Request::language)) }
        .filter { it.language.size >= 2 }
        .map { "Conflict (${it.string}) found between ${it.language.joinToString { language -> language.id }} for '${it.matcher.toTypeString()}'" }
        .forEach {
            violation = true
            println(it)
        }

    if (violation)
        exitProcess(-1)
}

private fun Matcher.toTypeString() = when (this) {
    is Matcher.Text.StartsWith -> "StartWith"
    is Matcher.Text.EndsWith -> "EndsWith"
    is Matcher.Text.Contains -> "Contains"
    is Matcher.Text.Equals -> "Equals"
    is Matcher.Text.RegEx -> "RegEx"
    is Matcher.Combining.All -> "All"
    is Matcher.Combining.Any -> "Any"
}

private fun Matcher?.unwrap(): Sequence<Pair<String, Matcher>> = when (this) {
    is Matcher.Text.StartsWith -> sequenceOf(this).flatMap { it.strings.asSequence() }.map { it to this }
    is Matcher.Text.EndsWith -> sequenceOf(this).flatMap { it.strings.asSequence() }.map { it to this }
    is Matcher.Text.Contains -> sequenceOf(this).flatMap { it.strings.asSequence() }.map { it to this }
    is Matcher.Text.Equals -> sequenceOf(this).flatMap { it.strings.asSequence() }.map { it to this }
    is Matcher.Text.RegEx -> sequenceOf(this).flatMap { it.expressions.asSequence().map { expression -> expression.toString() } }.map { it to this }
    is Matcher.Combining.All -> emptySequence()
    is Matcher.Combining.Any -> this.matchers.asSequence().flatMap { it.unwrap() }
    null -> emptySequence()
}

data class Request(val target: Matcher.Target, val string: String, val matcher: Matcher, val language: Language)

data class Result(val target: Matcher.Target, val string: String, val matcher: Matcher, val language: List<Language>)
