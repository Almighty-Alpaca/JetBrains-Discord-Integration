package com.almightyalpaca.jetbrains.plugins.discord.shared.utils

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

inline fun <K, V> Map(size: Int, init: (index: Int) -> Pair<K, V>): Map<K, V> =
    MutableMap(size, init)

inline fun <K, V> MutableMap(size: Int, init: (index: Int) -> Pair<K, V>): MutableMap<K, V> {
    val map = LinkedHashMap<K, V>(size)
    repeat(size) { index -> map += (init(index)) }
    return map
}

inline fun <T> Set(size: Int, init: (index: Int) -> T): Set<T> =
    MutableSet(size, init)

inline fun <T> MutableSet(size: Int, init: (index: Int) -> T): MutableSet<T> {
    val set = LinkedHashSet<T>(size)
    repeat(size) { index -> set.add(init(index)) }
    return set
}

fun <K, V> Map<K, V>.stream() = entries.stream()

fun <K, V> lazyMap(factory: (K) -> V?): LazyMutableMap<K, V> =
    LazyMutableMapImpl(mutableMapOf(), factory)

fun <K, V> lazyMap(map: MutableMap<K, V>, factory: (K) -> V?): LazyMutableMap<K, V> =
    LazyMutableMapImpl(map, factory)

fun <K, V> MutableMap<K, V>.lazy(factory: (K) -> V?): LazyMutableMap<K, V> =
    LazyMutableMapImpl(this, factory)

interface LazyMutableMap<K, V> : MutableMap<K, V>

private class LazyMutableMapImpl<K, V>(val map: MutableMap<K, V>, val factory: (K) -> V?) : LazyMutableMap<K, V>,
    MutableMap<K, V> by map {
    override fun get(key: K) = map.compute(key) { _, value -> value ?: factory.invoke(key) }
}

abstract class DelegateMap<K, V>(protected val map: Map<K, V>) : Map<K, V> by map

abstract class DelegateCollection<T>(protected val collection: Collection<T>) : Collection<T> by collection

abstract class DelegateSet<T>(protected val set: Set<T>) : Set<T> by set

fun <T> concat(element: T?, collection: Iterable<T>?): List<T> {
    val result = ArrayList<T>()
    element?.let { result.add(element) }
    collection?.let { result.addAll(collection) }
    return result
}
