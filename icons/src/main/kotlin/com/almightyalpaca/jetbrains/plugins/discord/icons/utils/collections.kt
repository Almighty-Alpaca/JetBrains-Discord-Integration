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

package com.almightyalpaca.jetbrains.plugins.discord.icons.utils

import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

inline fun <K, V, N> Map<K, V>.map(mapper: (K, V) -> Pair<K, N>): Map<K, N> {
    val map = mutableMapOf<K, N>()

    forEach { t -> map += mapper.invoke(t.key, t.value) }

    return map
}

inline fun <K, V> mapWith(size: Int, init: (index: Int) -> Pair<K, V>): Map<K, V> = mutableMapWith(size, init)

inline fun <K, V> mutableMapWith(size: Int, init: (index: Int) -> Pair<K, V>): MutableMap<K, V> {
    val map = LinkedHashMap<K, V>(size)
    repeat(size) { index -> map += (init(index)) }
    return map
}

inline fun <T> setWith(size: Int, init: (index: Int) -> T): Set<T> = mutableSetWith(size, init)

inline fun <T> mutableSetWith(size: Int, init: (index: Int) -> T): MutableSet<T> {
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

fun <A, B, C> Sequence<Pair<A, B>>.mapValue(transform: (B) -> C): Sequence<Pair<A, C>> = map { (a, b) -> a to transform(b) }

fun <K, V> Stream<Pair<K, V>>.toMap(): Map<K, V> =
    collect(Collectors.toMap({ (key, _) -> key }, { (_, value) -> value }))

fun <K> Stream<K>.toSet(): Set<K> =
    collect(Collectors.toSet())
