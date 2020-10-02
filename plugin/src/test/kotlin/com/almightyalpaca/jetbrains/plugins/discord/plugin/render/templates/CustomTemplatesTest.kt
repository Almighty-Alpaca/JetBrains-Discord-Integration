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

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {
    companion object {
        /// defaults for tests
        /// tests should override the values they use
        fun createApplicationData(
            applicationVersion: String = "2020.1.2"
        ): TemplateData.Application {
            return TemplateData.Application(
                applicationVersion
            )
        }

        fun createProjectData(
            applicationVersion: String = "2020.1.2",
            projectName: String = "Dummy project",
            projectDescription: String = "A dummy project description",
            vcsBranch: String? = "master",
            debbuggerActive: Boolean = false
        ): TemplateData.Project {
            return TemplateData.Project(
                applicationVersion,
                projectName,
                projectDescription,
                vcsBranch,
                debbuggerActive
            )
        }

        fun createFileData(
            applicationVersion: String = "2020.1.2",
            projectName: String = "Dummy project",
            projectDescription: String = "A dummy project description",
            vcsBranch: String? = "master",
            debuggerActive: Boolean = false,
            fileName: String = "Main.java",
            fileNameUnique: String = "Main.java",
            filePath: String = "src/Main.java",
            fileIsWriteable: Boolean = true,
            editorIsTextEditor: Boolean = true,

            /// chosen at random
            caretLine: Int = 172,
            lineCount: Int = 526,
            moduleName: String? = "dummy-module",
            pathInModule: String? = "/src/Main.java",
            fileSize: Int = 0
        ): TemplateData.File {
            return TemplateData.File(
                applicationVersion,
                projectName,
                projectDescription,
                vcsBranch,
                debuggerActive,
                fileName,
                fileNameUnique,
                filePath,
                fileIsWriteable,
                editorIsTextEditor,
                caretLine,
                lineCount,
                moduleName,
                pathInModule,
                fileSize
            )
        }
    }

    /**
     * No templates whatsoever
     */
    @Test
    fun normalText() {
        assertEquals(
            "abc", CustomTemplate(
                "abc"
            ).execute(
                CustomTemplateContext(
                    null,
                    createApplicationData()
                )
            ),
            "Normal text test #1 failed"
        )
    }

    /**
     * Just variable replacements
     */
    @Test
    fun simpleVariableReplacement() {
        assertEquals(
            "Main.java, unique: a/Main.java", CustomTemplate(
                "\${FileName}, unique: \${FileNameUnique}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileName = "Main.java", fileNameUnique = "a/Main.java")
                )
            ),
            "\${var} Variable replacement test #1 failed"
        )
        assertEquals(
            // see the $NotNull{VcsBranch} test in the notNullTest below; just know this is what's supposed to happen when vcsBranch is null
            "JetBrains-Discord-Integration, on branch: ", CustomTemplate(
                "\${ProjectName}, on branch: \${VcsBranch}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = null)
                )
            ),
            "\${var} Variable replacement test #2 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration, on branch: master", CustomTemplate(
                "\${ProjectName}, on branch: \${VcsBranch}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = "master")
                )
            ),
            "\${var} Variable replacement test #3 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration - Discord rich presence integration for all JetBrains IDEs", CustomTemplate(
                "\${ProjectName} - \${ProjectDescription}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", projectDescription = "Discord rich presence integration for all JetBrains IDEs")
                )
            ),
            "\${var} Variable replacement test #4 failed"
        )
        assertEquals(
            "2020.1.2 - JetBrains-Discord-Integration", CustomTemplate(
                "\${ApplicationVersion} - \${ProjectName}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", applicationVersion = "2020.1.2")
                )
            ),
            "\${var} Variable replacement test #5 failed"
        )
        assertEquals(
            "src/Main.java, language: Java - 158/1237", CustomTemplate(
                "\${FilePath}, language: \${Language} - \${CaretLine}/\${LineCount}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", caretLine = 158, lineCount = 1237)
                )
            ),
            "\${var} Variable replacement test #6 failed"
        )
        assertEquals(
            "src/Main.java, in module core", CustomTemplate(
                "\${FilePath}, in module \${ModuleName}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", moduleName = "core")
                )
            ),
            "\${var} Variable replacement test #7 failed"
        )
        assertEquals(
            "Main.java, in module test-module, with path in module '/src/Main.java'", CustomTemplate(
                "\${FileName}, in module \${ModuleName}, with path in module '\${PathInModule}'"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "Main.java", moduleName = "test-module", pathInModule = "/src/Main.java")
                )
            ),
            "\${var} Variable replacement test #8 failed"
        )

        ///////////////////////////// Tests above, but using `$var` syntax instead of `${var}` ////////////////////////

        assertEquals(
            "Main.java, unique: a/Main.java", CustomTemplate(
                "\$FileName, unique: \$FileNameUnique"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileName = "Main.java", fileNameUnique = "a/Main.java")
                )
            ),
            "\$var Variable replacement test #1 failed"
        )
        assertEquals(
            // see the $NotNull{VcsBranch} test in the notNullTest below; just know this is what's supposed to happen when vcsBranch is null
            "JetBrains-Discord-Integration, on branch: ", CustomTemplate(
                "\$ProjectName, on branch: \$VcsBranch"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = null)
                )
            ),
            "\$var Variable replacement test #2 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration, on branch: master", CustomTemplate(
                "\$ProjectName, on branch: \$VcsBranch"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = "master")
                )
            ),
            "\$var Variable replacement test #3 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration - Discord rich presence integration for all JetBrains IDEs", CustomTemplate(
                "\$ProjectName - \$ProjectDescription"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", projectDescription = "Discord rich presence integration for all JetBrains IDEs")
                )
            ),
            "\$var Variable replacement test #4 failed"
        )
        assertEquals(
            "2020.1.2 - JetBrains-Discord-Integration", CustomTemplate(
                "\$ApplicationVersion - \$ProjectName"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", applicationVersion = "2020.1.2")
                )
            ),
            "\$var Variable replacement test #5 failed"
        )
        assertEquals(
            "src/Main.java, language: Java - 158/1237", CustomTemplate(
                "\$FilePath, language: \$Language - \$CaretLine/\$LineCount"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", caretLine = 158, lineCount = 1237)
                )
            ),
            "\$var Variable replacement test #6 failed"
        )
        assertEquals(
            "src/Main.java, in module core", CustomTemplate(
                "\$FilePath, in module \$ModuleName"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", moduleName = "core")
                )
            ),
            "\$var Variable replacement test #7 failed"
        )
        assertEquals(
            "Main.java, in module test-module, with path in module '/src/Main.java'", CustomTemplate(
                "\$FileName, in module \$ModuleName, with path in module '\$PathInModule'"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "Main.java", moduleName = "test-module", pathInModule = "/src/Main.java")
                )
            ),
            "\$var Variable replacement test #8 failed"
        )
    }

    /**
     * Test cases involving $FileIsWritable
     */
    @Test
    fun fileReadableWritableTest() {
        assertEquals(
            "a", CustomTemplate(
                "%if(\$FileIsWritable){a}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileIsWriteable = true)
                )
            ),
            "File readable/writable test #1 failed"
        )
        assertEquals(
            "def", CustomTemplate(
                "%if(\$FileIsWritable){abc}{def}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileIsWriteable = false)
                )
            ),
            "File readable/writable test #2 failed"
        )
        assertEquals(
            "Reading AbcDef.java", CustomTemplate(
                "%if(\$FileIsWritable){Editing}{Reading} \${FileName}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "AbcDef.java", fileIsWriteable = false)
                )
            ),
            "File readable/writable test #3 failed"
        )
        assertEquals(
            "Editing AbcDef.java", CustomTemplate(
                "%if(\$FileIsWritable){Editing}{Reading} \${FileName}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "AbcDef.java", fileIsWriteable = true)
                )
            ),
            "File readable/writable test #4 failed"
        )
    }

    /**
     * Test cases involving $NotNull{}
     */
    @Test
    fun notNullTest() {
        assertEquals(
            "on branch abc", CustomTemplate(
                "%if(\$NotNull{VcsBranch}){on branch \${VcsBranch}}{with no VCS detected}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(vcsBranch = "abc")
                )
            ),
            "NotNull test #1 failed"
        )
        assertEquals(
            "with no VCS detected", CustomTemplate(
                "%if(\$NotNull{VcsBranch}){on branch \${VcsBranch}}{with no VCS detected}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createProjectData(vcsBranch = null)
                )
            ),
            "NotNull test #2 failed"
        )
        assertEquals(
            "in module core", CustomTemplate(
                "%if(\$NotNull{ModuleName}){in module \${ModuleName}}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "core")
                )
            ),
            "NotNull test #3 failed"
        )
        assertEquals(
            "", CustomTemplate(
                "%if(\$NotNull{ModuleName}){in module \${ModuleName}}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = null)
                )
            ),
            "NotNull test #4 failed"
        )
    }

    /**
     * Test cases involving $IsTextEditor
     */
    @Test
    fun isTextEditorTest() {
        assertEquals(
            "Java: Main.java, (1563/1921)", CustomTemplate(
                "\${Language}: \${FileName}, %if(\$IsTextEditor){(\${CaretLine}/\${LineCount})}{not in a text editor}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(editorIsTextEditor = true, fileName = "Main.java", caretLine = 1563, lineCount = 1921)
                )
            ),
            "IsTextEditor test #1 failed"
        )
        assertEquals(
            "Java: Main.java, not in a text editor", CustomTemplate(
                "\${Language}: \${FileName}, %if(\$IsTextEditor){(\${CaretLine}/\${LineCount})}{not in a text editor}"
            ).execute(
                CustomTemplateContext(
                    "Java",
                    createFileData(editorIsTextEditor = false, fileName = "Main.java", caretLine = 1563, lineCount = 1921)
                )
            ),
            "IsTextEditor test #2 failed"
        )
    }

    /**
     * Test cases involving $DebuggerActive
     */
    @Test
    fun debuggerActiveTest() {
        assertEquals(
            "Main.java", CustomTemplate(
                "%if(\$DebuggerActive){Debugging }\$FileName"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileName = "Main.java", debuggerActive = false)
                )
            ),
            "DebuggerActive test #1 failed"
        )
        assertEquals(
            "Debugging Main.java", CustomTemplate(
                "%if(\$DebuggerActive){Debugging }\$FileName"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(fileName = "Main.java", debuggerActive = true)
                )
            ),
            "DebuggerActive test #1 failed"
        )
    }

    @Test
    fun regexEscapeTest() {
        // this uses Java/Kotlin's implementation of escaping regex with \Q and \E
        assertEquals(
            "\\QA+\\E", CustomTemplate(
                "\$RegexEscape{A+}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData()
                )
            ),
            "RegexEscape test #1 failed"
        )
    }

    @Test
    fun matchesTest() {
        assertEquals(
            ", in module plugin", CustomTemplate(
                "%if(\$Matches{\${ModuleName}}{\$RegexEscape{\${ProjectName}}}){}{, in module \${ModuleName}}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "plugin", projectName = "JetBrains-Discord-Integration")
                )
            ),
            "Matches test #1 failed"
        )
        assertEquals(
            "", CustomTemplate(
                "%if(\$Matches{\${ModuleName}}{\$RegexEscape{\${ProjectName}}}){}{, in module \${ModuleName}}"
            ).execute(
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "JetBrains-Discord-Integration", projectName = "JetBrains-Discord-Integration")
                )
            ),
            "Matches test #2 failed"
        )
    }

    @Test
    fun rawTextTest() {
        assertEquals(
            "Some weird text{ \$That's'{ \${\$DefinitelyNotValid{", CustomTemplate(
                "#\"Some weird text{ \$That's'{ \${\$DefinitelyNotValid{\"#"
            ).execute(
                CustomTemplateContext(null, createFileData())
            ),
            "Raw text test #1 failed"
        )
    }

    @Test
    fun replaceTest() {
        assertEquals(
            "b c d e f", CustomTemplate(
                "\$ReplaceAll{a b c d e f}{#\"a \"#}{}"
            ).execute(
                CustomTemplateContext(null, createFileData())
            ),
            "Replace test #1 failed"
        )
        assertEquals(
            "/Main.java", CustomTemplate(
                "\$ReplaceFirst{\${PathInModule}}{/src}{}"
            ).execute(
                CustomTemplateContext(null, createFileData(pathInModule = "/src/Main.java"))
            ),
            "Replace test #2 failed"
        )
        assertEquals(
            "/Main.java", CustomTemplate(
                "\$ReplaceAll{\${PathInModule}}{#\"(/src){3}\"#}{}"
            ).execute(
                CustomTemplateContext(null, createFileData(pathInModule = "/src/src/src/Main.java"))
            ),
            "Replace test #3 failed"
        )
        assertEquals(
            "/src/src/src/Main.java", CustomTemplate(
                "\$ReplaceAll{\${PathInModule}}{#\"(/src){4}\"#}{}"
            ).execute(
                CustomTemplateContext(null, createFileData(pathInModule = "/src/src/src/Main.java"))
            ),
            "Replace test #3 failed"
        )
        assertEquals(
            "Main.java", CustomTemplate(
                "\$ReplaceAll{/src/a/b/c/d/e/f/g/h/Main.java}{\\#\"/([a-z]+/)*\"#}{}"
            ).execute(
                CustomTemplateContext(null, createFileData())
            ),
            "Replace test #4 failed"
        )
        assertEquals(
            "/src_x/a_x/b_x/c_x/d_x/e_x/f_x/g_x/h_x/Main.java", CustomTemplate(
                "\$ReplaceAll{/src/a/b/c/d/e/f/g/h/Main.java}{#\"([a-z]+)/\"#}{#\"\$1_x/\"#}"
            ).execute(
                CustomTemplateContext(null, createFileData())
            ),
            "Replace test #5 failed"
        )
    }

    @Test
    fun fileSizeTests() {
        assertEquals(
            "100 bytes", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 100))
            ),
            "File size test #1 failed"
        )
        assertEquals(
            "1000 bytes", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 1000))
            ),
            "File size test #2 failed"
        )
        assertEquals(
            "2047 bytes", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = (2 shl 10) - 1))
            ),
            "File size test #3 failed"
        )
        assertEquals(
            "2.0 KiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 2 shl 10))
            ),
            "File size test #4 failed"
        )
        assertEquals(
            "1000.0 KiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 1000 shl 10))
            ),
            "File size test #5 failed"
        )
        assertEquals(
            "2047.0 KiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = (2 shl 20) - (1 shl 10)))
            ),
            "File size test #6 failed"
        )
        assertEquals(
            "2.0 MiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 2 shl 20))
            ),
            "File size test #7 failed"
        )
        assertEquals(
            "1023.0 MiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = (1 shl 30) - (1 shl 20)))
            ),
            "File size test #8 failed"
        )
        assertEquals(
            "1.0 GiB", CustomTemplate(
                "\${FileSize}"
            ).execute(
                CustomTemplateContext(null, createFileData(fileSize = 1 shl 30))
            ),
            "File size test #9 failed"
        )
    }
}
