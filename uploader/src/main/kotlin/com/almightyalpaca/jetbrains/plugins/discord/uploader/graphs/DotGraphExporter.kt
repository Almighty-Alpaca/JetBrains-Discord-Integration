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

package com.almightyalpaca.jetbrains.plugins.discord.uploader.graphs

import com.almightyalpaca.jetbrains.plugins.discord.icons.source.Language
import com.almightyalpaca.jetbrains.plugins.discord.icons.source.LanguageMap
import org.apache.commons.text.StringEscapeUtils
import java.io.PrintWriter
import java.io.Writer

class DotGraphExporter(private val languages: LanguageMap, private val icons: Set<String>) {
    fun writeTo(writer: Writer) = writeTo(PrintWriter(writer))

    private fun writeTo(writer: PrintWriter) {
        val edges = mutableSetOf<Pair<Language, Language>>()

        writeHead(writer)

        languages.default.writeVertex(writer, edges)

        for (language in languages) {
            if (!language.id.contains('/'))
                language.writeVertex(writer, edges)
        }

        for (edge in edges)
            edge.writeEdge(writer)

        writeTail(writer)
    }

    private fun writeHead(writer: PrintWriter) {
        writer.println("digraph G {")
        writer.println("${INDENT}splines=\"compound\";")
        writer.println("${INDENT}rankdir=BT;")
        writer.println()
        writer.println("${INDENT}node [")
        writer.println("${DOUBLE_INDENT}fontname = \" Bitstream Vera Sans \"")
        writer.println("${DOUBLE_INDENT}shape = \"record\"")
        writer.println("$INDENT]")
    }

    private fun writeTail(writer: PrintWriter) {
        writer.println("}")
    }

    private fun Language.writeVertex(writer: PrintWriter, edges: MutableCollection<Pair<Language, Language>>, intend: String = INDENT) {
        val flavors = languages.filter { lang -> lang.id.startsWith("$id/") }
        if (this is Language.Simple && flavors.isNotEmpty())
            writeVertexGroup(writer, edges, flavors, intend)
        else
            writeVertexBasic(writer, intend)

        parent?.let { parent -> edges += (this to parent) }
    }

    private fun Language.writeVertexBasic(writer: PrintWriter, intend: String = INDENT) {
        writer.write(intend)
        writer.write(""""${id.escapeDot()}" [ label="{${name.escapeDot()}|""")

        when (val assetId = assetIds.firstOrNull()) {
            null -> writer.write("")
            in icons -> writer.write(assetId)
            else -> writer.write("$assetId*")
        }

        writer.write("}\"")

        if (matchers.isNotEmpty() && assetIds.none { assetId -> assetId in icons })
            writer.write(", color=red")

        writer.write(" ];")
        writer.println()
    }

    private fun Language.Simple.writeVertexGroup(
        writer: PrintWriter,
        edges: MutableCollection<Pair<Language, Language>>,
        flavors: List<Language>,
        intend: String = INDENT
    ) {
        writer.println("""${intend}subgraph "cluster_${id.escapeDot()}" {""")
        writer.println("$intend${INDENT}edge [")
        writer.println("$intend${DOUBLE_INDENT}style = dashed")
        writer.println("$intend$INDENT]")

        writeVertexBasic(writer, intend + INDENT)

        for (f in flavors) {
            f.writeVertex(writer, edges, intend + INDENT)
            (f to this).writeEdge(writer, intend + INDENT)
        }

        writer.println("  }")
    }

    private fun Pair<Language, Language>.writeEdge(writer: PrintWriter, intend: String = INDENT) {
        writer.println("""$intend"${first.id.escapeDot()}" -> "${second.id.escapeDot()}";""")
    }

    private fun String.escapeDot() = StringEscapeUtils.escapeHtml4(this)

    companion object {
        private const val INDENT = "  "
        private const val DOUBLE_INDENT = INDENT + INDENT
    }
}
