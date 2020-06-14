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
            vcsBranch: String? = "master"
        ): TemplateData.Project {
            return TemplateData.Project(
                applicationVersion,
                projectName,
                projectDescription,
                vcsBranch
            )
        }

        fun createFileData(
            applicationVersion: String = "2020.1.2",
            projectName: String = "Dummy project",
            projectDescription: String = "A dummy project description",
            vcsBranch: String? = "master",
            fileName: String = "Main.java",
            fileNameUnique: String = "Main.java",
            filePath: String = "src/Main.java",
            fileIsWriteable: Boolean = true,
            editorIsTextEditor: Boolean = true,

            /// chosen at random
            caretLine: Int = 172,
            lineCount: Int = 526,
            moduleName: String? = "dummy-module",
            pathInModule: String? = "/src/Main.java"
        ): TemplateData.File {
            return TemplateData.File(
                applicationVersion,
                projectName,
                projectDescription,
                vcsBranch,
                fileName,
                fileNameUnique,
                filePath,
                fileIsWriteable,
                editorIsTextEditor,
                caretLine,
                lineCount,
                moduleName,
                pathInModule
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
                "abc",
                CustomTemplateContext(
                    null,
                    createApplicationData()
                )
            ).execute(),
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
                "\${FileName}, unique: \${FileNameUnique}",
                CustomTemplateContext(
                    null,
                    createFileData(fileName = "Main.java", fileNameUnique = "a/Main.java")
                )
            ).execute(),
            "Variable replacement test #1 failed"
        )
        assertEquals(
            // see the $NotNull{VcsBranch} test in the notNullTest below; just know this is what's supposed to happen when vcsBranch is null
            "JetBrains-Discord-Integration, on branch: ", CustomTemplate(
                "\${ProjectName}, on branch: \${VcsBranch}",
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = null)
                )
            ).execute(),
            "Variable replacement test #2 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration, on branch: master", CustomTemplate(
                "\${ProjectName}, on branch: \${VcsBranch}",
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", vcsBranch = "master")
                )
            ).execute(),
            "Variable replacement test #3 failed"
        )
        assertEquals(
            "JetBrains-Discord-Integration - Discord rich presence integration for all JetBrains IDEs", CustomTemplate(
                "\${ProjectName} - \${ProjectDescription}",
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", projectDescription = "Discord rich presence integration for all JetBrains IDEs")
                )
            ).execute(),
            "Variable replacement test #4 failed"
        )
        assertEquals(
            "2020.1.2 - JetBrains-Discord-Integration", CustomTemplate(
                "\${ApplicationVersion} - \${ProjectName}",
                CustomTemplateContext(
                    null,
                    createProjectData(projectName = "JetBrains-Discord-Integration", applicationVersion = "2020.1.2")
                )
            ).execute(),
            "Variable replacement test #5 failed"
        )
        assertEquals(
            "src/Main.java, language: Java - 158/1237", CustomTemplate(
                "\${FilePath}, language: \${Language} - \${CaretLine}/\${LineCount}",
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", caretLine = 158, lineCount = 1237)
                )
            ).execute(),
            "Variable replacement test #6 failed"
        )
        assertEquals(
            "src/Main.java, in module core", CustomTemplate(
                "\${FilePath}, in module \${ModuleName}",
                CustomTemplateContext(
                    "Java",
                    createFileData(filePath = "src/Main.java", moduleName = "core")
                )
            ).execute(),
            "Variable replacement test #7 failed"
        )
        assertEquals(
            "Main.java, in module test-module, with path in module '/src/Main.java'", CustomTemplate(
                "\${FileName}, in module \${ModuleName}, with path in module '\${PathInModule}'",
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "Main.java", moduleName = "test-module", pathInModule = "/src/Main.java")
                )
            ).execute(),
            "Variable replacement test #8 failed"
        )
    }

    /**
     * Test cases involving $FileIsWritable{}{}
     */
    @Test
    fun fileReadableWritableTest() {
        assertEquals(
            "a", CustomTemplate(
                "\$FileIsWritable{a}{b}",
                CustomTemplateContext(
                    null,
                    createFileData(fileIsWriteable = true)
                )
            ).execute(),
            "File readable/writable test #1 failed"
        )
        assertEquals(
            "def", CustomTemplate(
                "\$FileIsWritable{abc}{def}",
                CustomTemplateContext(
                    null,
                    createFileData(fileIsWriteable = false)
                )
            ).execute(),
            "File readable/writable test #2 failed"
        )
        assertEquals(
            "Reading AbcDef.java", CustomTemplate(
                "\$FileIsWritable{Editing}{Reading} \${FileName}",
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "AbcDef.java", fileIsWriteable = false)
                )
            ).execute(),
            "File readable/writable test #3 failed"
        )
        assertEquals(
            "Editing AbcDef.java", CustomTemplate(
                "\$FileIsWritable{Editing}{Reading} \${FileName}",
                CustomTemplateContext(
                    "Java",
                    createFileData(fileName = "AbcDef.java", fileIsWriteable = true)
                )
            ).execute(),
            "File readable/writable test #4 failed"
        )
    }

    /**
     * Test cases involving $NotNull{}{}{}
     */
    @Test
    fun notNullTest() {
        assertEquals(
            "on branch abc", CustomTemplate(
                "\$NotNull{VcsBranch}{on branch \${VcsBranch}}{with no VCS detected}",
                CustomTemplateContext(
                    null,
                    createProjectData(vcsBranch = "abc")
                )
            ).execute(),
            "NotNull test #1 failed"
        )
        assertEquals(
            "with no VCS detected", CustomTemplate(
                "\$NotNull{VcsBranch}{on branch \${VcsBranch}}{with no VCS detected}",
                CustomTemplateContext(
                    null,
                    createProjectData(vcsBranch = null)
                )
            ).execute(),
            "NotNull test #2 failed"
        )
        assertEquals(
            "in module core", CustomTemplate(
                "\$NotNull{ModuleName}{in module \${ModuleName}}{}",
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "core")
                )
            ).execute(),
            "NotNull test #3 failed"
        )
        assertEquals(
            "", CustomTemplate(
                "\$NotNull{ModuleName}{in module \${ModuleName}}{}",
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = null)
                )
            ).execute(),
            "NotNull test #4 failed"
        )
    }

    /**
     * Test cases involving $IsTextEditor{}{}
     */
    @Test
    fun isTextEditorTest() {
        assertEquals(
            "Java: Main.java, (1563/1921)", CustomTemplate(
                "\${Language}: \${FileName}, \$IsTextEditor{(\${CaretLine}/\${LineCount})}{not in a text editor}",
                CustomTemplateContext(
                    "Java",
                    createFileData(editorIsTextEditor = true, fileName = "Main.java", caretLine = 1563, lineCount = 1921)
                )
            ).execute(),
            "IsTextEditor test #1 failed"
        )
        assertEquals(
            "Java: Main.java, not in a text editor", CustomTemplate(
                "\${Language}: \${FileName}, \$IsTextEditor{(\${CaretLine}/\${LineCount})}{not in a text editor}",
                CustomTemplateContext(
                    "Java",
                    createFileData(editorIsTextEditor = false, fileName = "Main.java", caretLine = 1563, lineCount = 1921)
                )
            ).execute(),
            "IsTextEditor test #2 failed"
        )
    }

    @Test
    fun regexEscapeTest() {
        // this uses Java/Kotlin's implementation of escaping regex with \Q and \E
        assertEquals(
            "\\QA+\\E", CustomTemplate(
                "\$RegexEscape{A+}",
                CustomTemplateContext(
                    null,
                    createFileData()
                )
            ).execute(),
            "RegexEscape test #1 failed"
        )
    }

    @Test
    fun matchesTest() {
        assertEquals(
            ", in module plugin", CustomTemplate(
                "\$Matches{\${ModuleName}}{\$RegexEscape{\${ProjectName}}}{}{, in module \${ModuleName}}",
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "plugin", projectName = "JetBrains-Discord-Integration")
                )
            ).execute(),
            "Matches test #1 failed"
        )
        assertEquals(
            "", CustomTemplate(
                "\$Matches{\${ModuleName}}{\$RegexEscape{\${ProjectName}}}{}{, in module \${ModuleName}}",
                CustomTemplateContext(
                    null,
                    createFileData(moduleName = "JetBrains-Discord-Integration", projectName = "JetBrains-Discord-Integration")
                )
            ).execute(),
            "Matches test #2 failed"
        )
    }
}
