/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

fun CharSequence.find(char: Char, ignoreCase: Boolean = false): IntStream = IntStream.range(0, length)
    .filter { i -> get(i).equals(char, ignoreCase) }

fun CharSequence.find(sequence: CharSequence, ignoreCase: Boolean = false): IntStream = IntStream.range(0, length)
    .filter { i -> startsWith(sequence, i, ignoreCase) }

fun String.limit(limit: Int, dots: Boolean = false) = when (length <= limit) {
    true -> this
    false -> when (dots) {
        true -> "${substring(0, limit - 1)}…"
        false -> substring(0, 128)
    }
}

fun String.limitStringWidth(font: FontMetrics, limit: Int): String {
    when (font.stringWidth(this) <= limit) {
        true -> return this
        false -> {
            val pointsWidth = font.stringWidth("…")
            val limitCut = limit - pointsWidth

            for (i in length downTo 0) {
                val lineTemp = substring(0, i)
                if (font.stringWidth(lineTemp) <= limitCut)
                    return "$lineTemp…"
            }

            return "…"
        }
    }
}
