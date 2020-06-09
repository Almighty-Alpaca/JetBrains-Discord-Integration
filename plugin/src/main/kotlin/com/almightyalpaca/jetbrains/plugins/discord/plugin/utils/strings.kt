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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

import java.awt.FontMetrics
import java.util.stream.IntStream

fun CharSequence.find(char: Char, ignoreCase: Boolean = false): IntStream =
    IntStream.range(0, length)
        .filter { i -> get(i).equals(char, ignoreCase) }

fun String?.limit(range: IntRange, dots: Boolean = true) = when {
    this == null || isInvisible() -> null
    else -> when (length > range.last) {
        true -> when (dots) {
            true -> "${substring(0, range.last - 1)}…"
            false -> substring(0, 128)
        }
        false -> when (length < range.first) {
            true -> this + ('\u200b' * (range.first - length))
            false -> this
        }
    }
}

fun String.limitWidth(font: FontMetrics, limit: Int): String =
    when (font.stringWidth(this) <= limit) {
        true -> this
        false -> {
            val pointsWidth = font.stringWidth("…")
            val limitCut = limit - pointsWidth

            var result = "…"

            for (i in length downTo 0) {
                val lineTemp = substring(0, i)
                if (font.stringWidth(lineTemp) <= limitCut) {
                    result = "$lineTemp…"
                    break
                }
            }

            result
        }
    }

operator fun Char.times(n: Int): String = StringBuilder().apply {
    for (i in 0..n + 1) {
        append(this@times)
    }
}.toString()

fun limitingLength(initialValue: String?, range: IntRange, dots: Boolean) =
    modifying(initialValue) { it.limit(range, dots) }

fun verifyingLength(initialValue: String?, range: IntRange) =
    verifying(initialValue) { it == null || it.length in range }

fun CharSequence.isInvisible(): Boolean {
    return indices.all { this[it].isWhitespace() || this[it] == '\u200B' }
}
