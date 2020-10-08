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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.Data
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.RenderContext
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr.TemplateLexer
import com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr.TemplateParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

/**
 * Some utils
 */
object Utils {
    fun getVarValue(varName: String, context: CustomTemplateContext): String? =
        when (varName) {
            "ApplicationVersion" -> context.applicationData?.applicationVersion
            "ProjectName" -> context.projectData?.projectName
            "ProjectDescription" -> context.projectData?.projectDescription
            "VcsBranch" -> context.projectData?.vcsBranch
            "FileName" -> context.fileData?.fileName
            "FileNameUnique" -> context.fileData?.fileNameUnique
            "FilePath" -> context.fileData?.filePath
            "CaretLine" -> context.fileData?.caretLine?.toString()
            "LineCount" -> context.fileData?.lineCount?.toString()
            "ModuleName" -> context.fileData?.moduleName
            "PathInModule" -> context.fileData?.pathInModule
            "Language" -> context.language
            "FileSize" -> sizeAsString(context.fileData?.fileSize)
            "IsTextEditor" -> context.fileData?.editorIsTextEditor.toString()
            "FileIsWritable" -> context.fileData?.fileIsWriteable.toString()
            "DebuggerActive" -> context.projectData?.debuggerActive.toString()
            else -> null
        }

    private fun sizeAsString(size: Int?): String? {
        if (size == null) return null
        if (size < 2 shl 10) return "$size bytes" // 0 .. 2 KiB
        if (size < 2 shl 20) return "${twoDecimals(size.toDouble() / (1 shl 10))} KiB" // 2 KiB .. 2 MiB
        if (size < 1 shl 30) return "${twoDecimals(size.toDouble() / (1 shl 20))} MiB" // 2 MiB .. 1 GiB
        return "${twoDecimals(size.toDouble() / (1 shl 30))} GiB" // 1 GiB ..
    }

    private fun twoDecimals(num: Double) = (Math.floor(num * 100.0) / 100.0)

    private fun varNullCheck(context: CustomTemplateContext, varName: String): Boolean = getVarValue(varName, context) != null

    fun evalVisitor(context: CustomTemplateContext, tree: TemplateParser.Text_evalContext): String {
        var ret = ""
        for (child in tree.children ?: listOf()) {
            ret += when (child) {
                is TemplateParser.VarContext -> {
                    getVarValue(child.NAME()?.text ?: "", context) ?: ""
                }
                is TemplateParser.FunContext -> {
                    val name = child.NAME()?.symbol?.text ?: ""
                    val req = when (name) {
                        "RegexEscape", "NotNull" -> 1
                        "Matches" -> 2
                        "ReplaceFirst", "ReplaceAll" -> 3
                        else -> 0
                    }
                    if (child.text_eval().size < req) {
                        child.text
                    } else {
                        val arr = child.text_eval()
                        when (name) {
                            "RegexEscape" -> {
                                Regex.escape(evalVisitor(context, arr[0]))
                            }
                            "NotNull" -> {
                                if (varNullCheck(context, evalVisitor(context, arr[0]))) {
                                    "true"
                                } else {
                                    "false"
                                }
                            }
                            "Matches" -> {
                                if (evalVisitor(context, arr[0]).matches(Regex(evalVisitor(context, arr[1])))) {
                                    "true"
                                } else {
                                    "false"
                                }
                            }
                            "ReplaceFirst" -> {
                                evalVisitor(context, arr[0]).replaceFirst(Regex(evalVisitor(context, arr[1])), evalVisitor(context, arr[2]))
                            }
                            "ReplaceAll" -> {
                                evalVisitor(context, arr[0]).replace(Regex(evalVisitor(context, arr[1])), evalVisitor(context, arr[2]))
                            }
                            else -> ""
                        }
                    }
                }
                is TemplateParser.If_ruleContext -> {
                    val args = child.text_eval()


                    // while writing the if, args.size might be 0, and it
                    // could throw an index out of bounds exception.
                    // This if is meant to prevent that
                    val conditionValue =
                        if (args.size > 0)
                            when (evalVisitor(context, args[0])) {
                                "null", "false", "" -> false
                                else -> true
                            }
                        else false

                    if (conditionValue) {
                        // same as above, meant to prevent index out of bounds.
                        if (args.size > 1) evalVisitor(context, args[1]) else ""
                    } else {
                        if (args.size >= 3) {
                            evalVisitor(context, args[2])
                        } else {
                            ""
                        }
                    }
                }
                is TemplateParser.Raw_text_ruleContext -> {
                    val txt = child.text
                    txt.substring(2, txt.length - 2) // take out the first and last 2 characters(the '#"' at the beginning
                    // and '"#' at the end)
                }
                else -> child.text // NAME/TEXT/parentheses from the grammar
            }
        }
        return ret
    }
}

/**
 * Represents a template
 * When executed, it should return a string with all the patterns replaced by values
 */
class CustomTemplate(val template: String) {
    private val rootNode: TemplateParser.Text_evalContext

    init {
        val lexer = TemplateLexer(CharStreams.fromString(template))
        val tokens = CommonTokenStream(lexer)
        val parser = TemplateParser(tokens)
        parser.buildParseTree = true
        rootNode = parser.template().text_eval()
    }

    fun execute(context: CustomTemplateContext): String? {
        return Utils.evalVisitor(context, rootNode)
    }
}

data class CustomTemplateContext(val language: String?, val data: TemplateData) {
    val applicationData = data as? TemplateData.Application
    val projectData = data as? TemplateData.Project
    val fileData = data as? TemplateData.File

    companion object {
        fun from(context: RenderContext): CustomTemplateContext = CustomTemplateContext(context.language?.name, context.data.asTemplateData())
    }
}

private fun Data.asTemplateData(): TemplateData {
    when (this) {
        is Data.File -> {
            return TemplateData.File(
                this.applicationVersion,
                this.projectName,
                this.projectDescription,
                this.vcsBranch,
                this.debuggerActive,
                this.fileName,
                this.fileNameUnique,
                this.filePath,
                this.fileIsWriteable,
                this.editorIsTextEditor,
                this.caretLine,
                this.lineCount,
                this.moduleName,
                this.pathInModule,
                this.fileSize
            )
        }
        is Data.Project -> {
            return TemplateData.Project(
                this.applicationVersion, this.projectName, this.projectDescription, this.vcsBranch, this.debuggerActive
            )
        }
        is Data.Application -> {
            return TemplateData.Application(
                this.applicationVersion
            )
        }
        else -> {
            throw IllegalArgumentException()
        }
    }
}

fun RenderContext.asCustomTemplateContext(): CustomTemplateContext {
    return CustomTemplateContext.from(this)
}

sealed class TemplateData {
    open class Application(
        val applicationVersion: String
    ) : TemplateData()

    open class Project(
        applicationVersion: String,
        val projectName: String,
        val projectDescription: String,
        val vcsBranch: String?,
        val debuggerActive: Boolean
    ) : Application(applicationVersion)

    open class File(
        applicationVersion: String,
        projectName: String,
        projectDescription: String,
        vcsBranch: String?,
        debuggerActive: Boolean,
        val fileName: String,
        val fileNameUnique: String,
        val filePath: String,
        val fileIsWriteable: Boolean,
        val editorIsTextEditor: Boolean,

        val caretLine: Int,
        val lineCount: Int,
        val moduleName: String?,
        val pathInModule: String?,
        val fileSize: Int
    ) : Project(
        applicationVersion,
        projectName,
        projectDescription,
        vcsBranch,
        debuggerActive
    )
}
