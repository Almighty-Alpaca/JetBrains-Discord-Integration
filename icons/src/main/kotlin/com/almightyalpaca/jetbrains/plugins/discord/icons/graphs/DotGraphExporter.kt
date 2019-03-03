package com.almightyalpaca.jetbrains.plugins.discord.icons.graphs

import com.almightyalpaca.jetbrains.plugins.shared.languages.Language
import com.almightyalpaca.jetbrains.plugins.shared.languages.LanguageMap
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
        if (this is Language.Simple && this.flavors.isNotEmpty())
            writeVertexGroup(writer, edges, intend)
        else
            writeVertexBasic(writer, intend)

        parent?.let { parent -> edges += (this to parent) }
    }

    private fun Language.writeVertexBasic(writer: PrintWriter, intend: String = INDENT) {
        writer.write(intend)
        writer.write("${id.escapeDot()} [ label=\"{${name.escapeDot()}|")

        when (val asset = assets.firstOrNull()) {
            null -> writer.write("")
            in icons -> writer.write(asset)
            else -> writer.write("$asset*")
        }

        writer.write("}\"")

        if (matchers.isNotEmpty() && assets.none { asset -> asset in icons })
            writer.write(", color=red")

        writer.write(" ];")
        writer.println()
    }

    private fun Language.Simple.writeVertexGroup(writer: PrintWriter, edges: MutableCollection<Pair<Language, Language>>, intend: String = INDENT) {
        writer.println("${intend}subgraph cluster_$id {")
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
        writer.println("$intend${first.id.escapeDot()} -> ${second.id.escapeDot()};")
    }

    private fun String.escapeDot() = StringEscapeUtils.escapeHtml4(this)

    companion object {
        private const val INDENT = "  "
        private const val DOUBLE_INDENT = INDENT + INDENT
    }
}
