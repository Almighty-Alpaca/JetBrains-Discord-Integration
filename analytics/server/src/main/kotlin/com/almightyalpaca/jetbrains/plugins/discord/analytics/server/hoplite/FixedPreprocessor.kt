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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.hoplite

import com.sksamuel.hoplite.ArrayNode
import com.sksamuel.hoplite.MapNode
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.StringNode
import com.sksamuel.hoplite.preprocessor.Preprocessor

// TODO: remove once https://github.com/sksamuel/hoplite/issues/138 has been fixed

abstract class FixedStringNodePreprocessor : Preprocessor {
    override fun process(node: Node): Node = when (node) {
        is StringNode -> map(node)
        is ArrayNode -> node.copy(elements = node.elements.map { process(it) })
        is MapNode -> node.copy(map = node.map.mapValues { process(it.value) })
        else -> node
    }

    protected abstract fun map(node: StringNode): Node
}

object FixedSystemPropertyPreprocessor : FixedStringNodePreprocessor() {
    // Redundant escaping required for Android support.
    private val regex = Regex("""\$\{(.*?)\}""")

    override fun map(node: StringNode): Node {
        val value = regex.replace(node.value) {
            val key = it.groupValues[1]
            System.getProperty(key, it.value)
        }
        return node.copy(value = value)
    }
}
