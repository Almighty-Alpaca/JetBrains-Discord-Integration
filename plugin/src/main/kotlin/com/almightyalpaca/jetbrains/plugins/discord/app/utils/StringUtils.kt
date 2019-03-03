package com.almightyalpaca.jetbrains.plugins.discord.app.utils

import java.util.stream.IntStream

fun CharSequence.find(char: Char, ignoreCase: Boolean = false): IntStream = IntStream.range(0, length)
    .filter { i -> get(i).equals(char, ignoreCase) }

fun CharSequence.find(sequence: CharSequence, ignoreCase: Boolean = false): IntStream = IntStream.range(0, length)
    .filter { i -> startsWith(sequence, i, ignoreCase) }
