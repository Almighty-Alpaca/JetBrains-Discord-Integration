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

plugins {
    kotlin("jvm")
    fileIndices
}

dependencies {
    val versionCoroutines: String by project
    val versionCommonsIo: String by project
    val versionJackson: String by project

    implementation(kotlin(module = "stdlib"))

    implementation(platform(kotlinx("coroutines-bom", versionCoroutines)))
    implementation(kotlinx("coroutines-core"))

    implementation(group = "commons-io", name = "commons-io", version = versionCommonsIo)

    implementation(platform("com.fasterxml.jackson:jackson-bom:$versionJackson"))
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml")
}

tasks {
    generateFileIndices {
        paths += "discord/applications"
        paths += "discord/languages"
        paths += "discord/themes"
    }

    checkImplicitDependencies {
        ignore("org.jetbrains", "annotations")
    }

    val generateIcons = create("generate-icons") {
        group = "icons"
    }

    val generateMaterialApplicationIcons = create<Exec>("generate-material-application-icons") {
        workingDir(project.file("src/main/resources/discord/applications/material"))
        commandLine = listOf(
            "magick",
            "mogrify",
            "-resize",
            "800x800",
            "-gravity",
            "center",
            "-bordercolor",
            "\"#23272A\"",
            "-border",
            "112x112",
            "-path",
            ".",
            "../*.png"
        )
    }

    val deleteMaterialApplicationIcons = create<Delete>("delete-material-application-icons") {
        delete(fileTree("src/main/resources/discord/applications/material/") {
            include("*.png")
        })
    }

    generateIcons.dependsOn(generateMaterialApplicationIcons)
    generateMaterialApplicationIcons.dependsOn(deleteMaterialApplicationIcons)
}
