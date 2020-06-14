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

object Patterns {
    /**
     * Thrown on invalid input
     */
    class ValidityCheckFailed(reason: String) : RuntimeException(reason)

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
                else -> null
            }

        fun varNullCheck(context: CustomTemplateContext, varName: String): Boolean = getVarValue(varName, context) != null

        fun evalVisitor(context: CustomTemplateContext, tree: TemplateParser.Text_evalContext): String {
            var ret = ""
            for (child in tree.children ?: listOf()) {
                ret += when (child) {
                    is TemplateParser.VarContext -> {
                        getVarValue(child.TEXT().text, context) ?: ""
                    }
                    is TemplateParser.FunContext -> {
                        val name = child.TEXT().symbol.text
                        val req = when (name) {
                            "RegexEscape" -> 1
                            "FileIsWritable", "IsTextEditor" -> 2
                            "NotNull" -> 3
                            "Matches" -> 4
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
                                "FileIsWritable" -> {
                                    evalVisitor(
                                        context, if (context.fileData?.fileIsWriteable == true) {
                                            arr[0]
                                        } else {
                                            arr[1]
                                        }
                                    )
                                }
                                "IsTextEditor" -> {
                                    evalVisitor(
                                        context, if (context.fileData?.editorIsTextEditor == true) {
                                            arr[0]
                                        } else {
                                            arr[1]
                                        }
                                    )
                                }
                                "NotNull" -> {
                                    evalVisitor(
                                        context, if (varNullCheck(context, evalVisitor(context, arr[0]))) {
                                            arr[1]
                                        } else {
                                            arr[2]
                                        }
                                    )
                                }
                                "Matches" -> {
                                    evalVisitor(
                                        context, if (evalVisitor(context, arr[0]).matches(Regex(evalVisitor(context, arr[1])))) {
                                            arr[2]
                                        } else {
                                            arr[3]
                                        }
                                    )
                                }
                                else -> ""
                            }
                        }
                    }
                    else -> child.text // TEXT from the grammar: [^\${}]+
                }
            }
            return ret
        }
    }
}

/**
 * Represents a template
 * When executed, it should return a string with all the patterns replaced by values
 */
class CustomTemplate(private val template: String?, private val context: CustomTemplateContext) {
    @Throws(Patterns.ValidityCheckFailed::class)
    fun execute(): String? {
        if (template == null) return null

        val lexer = TemplateLexer(CharStreams.fromString(template))
        val tokens = CommonTokenStream(lexer)
        val parser = TemplateParser(tokens)
        parser.buildParseTree = true
        return Patterns.Utils.evalVisitor(context, parser.template().text_eval())
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
                this.fileName,
                this.fileNameUnique,
                this.filePath,
                this.fileIsWriteable,
                this.editorIsTextEditor,
                this.caretLine,
                this.lineCount,
                this.moduleName,
                this.pathInModule
            )
        }
        is Data.Project -> {
            return TemplateData.Project(
                this.applicationVersion, this.projectName, this.projectDescription, this.vcsBranch
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
        val vcsBranch: String?
    ) : Application(applicationVersion)

    open class File(
        applicationVersion: String,
        projectName: String,
        projectDescription: String,
        vcsBranch: String?,
        val fileName: String,
        val fileNameUnique: String,
        val filePath: String,
        val fileIsWriteable: Boolean,
        val editorIsTextEditor: Boolean,

        val caretLine: Int,
        val lineCount: Int,
        val moduleName: String?,
        val pathInModule: String?
    ) : Project(
        applicationVersion,
        projectName,
        projectDescription,
        vcsBranch
    )
}
