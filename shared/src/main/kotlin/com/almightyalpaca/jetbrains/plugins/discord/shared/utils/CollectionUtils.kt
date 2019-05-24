package com.almightyalpaca.jetbrains.plugins.discord.shared.utils

import java.util.stream.Stream
import java.util.stream.StreamSupport

inline fun <T, K, V> Iterable<T>.toMap(mapper: (T) -> Pair<K, V>) = iterator().toMap(mapper)

inline fun <T, K, V> Iterator<T>.toMap(mapper: (T) -> Pair<K, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()

    forEach { t -> map += mapper(t) }

    return map
}

inline fun <K, V, N> Map<K, V>.map(mapper: (K, V) -> Pair<K, N>): Map<K, N> {
    val map = mutableMapOf<K, N>()

    forEach { t -> map += mapper.invoke(t.key, t.value) }

    return map
}

inline fun <K, V> Map(size: Int, init: (index: Int) -> Pair<K, V>): Map<K, V> = MutableMap(size, init)

inline fun <K, V> MutableMap(size: Int, init: (index: Int) -> Pair<K, V>): MutableMap<K, V> {
    val map = LinkedHashMap<K, V>(size)
    repeat(size) { index -> map += (init(index)) }
    return map
}

inline fun <T> Set(size: Int, init: (index: Int) -> T): Set<T> = MutableSet(size, init)

inline fun <T> MutableSet(size: Int, init: (index: Int) -> T): MutableSet<T> {
    val set = LinkedHashSet<T>(size)

    repeat(size) { index -> set.add(init(index)) }

    return set
}

fun <K, V> Map<K, V>.stream() = entries.stream()

fun <T> concat(vararg collections: Iterable<T>?): List<T> {
    val result = ArrayList<T>()

    for (collection in collections) {
        if (collection != null) {
            result.addAll(collection)
        }
    }

    return result
}

fun <R> Iterable<R>.stream(): Stream<R> = StreamSupport.stream(this.spliterator(), false)
