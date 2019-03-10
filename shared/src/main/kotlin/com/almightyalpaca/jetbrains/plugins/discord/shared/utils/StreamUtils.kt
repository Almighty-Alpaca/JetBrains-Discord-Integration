package com.almightyalpaca.jetbrains.plugins.discord.shared.utils

import java.util.stream.Collectors
import java.util.stream.Stream

fun <K, V> Stream<Pair<K, V>>.toMap(): Map<K, V> =
    collect(Collectors.toMap({ (key, _) -> key }, { (_, value) -> value }))

fun <K> Stream<K>.toSet(): Set<K> =
    collect(Collectors.toSet())
