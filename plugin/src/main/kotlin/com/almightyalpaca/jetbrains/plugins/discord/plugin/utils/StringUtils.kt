package com.almightyalpaca.jetbrains.plugins.discord.plugin.utils

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
