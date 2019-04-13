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
        true -> "${substring(0, limit - 3)}..."
        false -> substring(0, 128)
    }
}

fun String.limitStringWidth(font: FontMetrics, limit: Int): String {
    when (font.stringWidth(this) <= limit) {
        true -> return this
        false -> {
            val pointsWidth = font.stringWidth("...")
            val limitCut = limit - pointsWidth

            for (i in length downTo 0) {
                val lineTemp = substring(0, i)
                if (font.stringWidth(lineTemp) <= limitCut)
                    return "$lineTemp..."
            }

            return "..."
        }
    }
}
