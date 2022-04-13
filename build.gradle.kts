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

@file:Suppress("SuspiciousCollectionReassignment")

import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

plugins {
    kotlin("jvm") apply false
    id("com.github.ben-manes.versions")
    id("com.palantir.git-version")
}

group = "com.almightyalpaca.jetbrains.plugins.discord"

val versionDetails: Closure<VersionDetails> by project.extra

var version = versionDetails().lastTag.removePrefix("v")
version += when (versionDetails().commitDistance) {
    0 -> ""
    else -> "+${versionDetails().commitDistance}"
}

project.version = version

allprojects {
    repositories {
        mavenCentral()
    }

    fun secret(name: String) {
        project.extra[name] = System.getenv(name) ?: return
    }

    secret("DISCORD_TOKEN")
    secret("BINTRAY_KEY")
    secret("JETBRAINS_TOKEN")
}

subprojects {
    group = rootProject.group.toString() + "." + project.name.toLowerCase()
    version = rootProject.version

    val secrets: File = rootProject.file("secrets.gradle.kts")
    if (secrets.exists()) {
        apply(from = secrets)
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs += "-Xjvm-default=enable"

                languageVersion = "1.4"
            }
        }

        withType<JavaCompile> {
            targetCompatibility = "11"
            sourceCompatibility = "11"

            if (JavaVersion.current() >= JavaVersion.VERSION_1_9) {
                options.compilerArgs as MutableList<String> += listOf("--release", "11")
            }
        }
    }
}

defaultTasks = mutableListOf("default")

tasks {
    dependencyUpdates {
        gradleReleaseChannel = GradleReleaseChannel.CURRENT.toString()

        rejectVersionIf {
            val preview = Regex("""[+_.-]?(alpha|beta|rc|cr|m|preview|eap|pr|M)[.\d-_]*$""", RegexOption.IGNORE_CASE)
                .containsMatchIn(candidate.version)

            val wrongKotlinVersion = candidate.group.startsWith("org.jetbrains.kotlin") && candidate.version != currentVersion

            return@rejectVersionIf preview || wrongKotlinVersion
        }
    }

    withType<Wrapper> {
        val versionGradle: String by project

        distributionType = Wrapper.DistributionType.BIN
        gradleVersion = versionGradle
    }

    create<Delete>("clean") {
        group = "build"

        val regex = Regex("""JetBrains-Discord-Integration-\d+.\d+.\d+(?:\+\d+)?.zip""")

        Files.newDirectoryStream(project.projectDir.toPath())
            .filter { p -> regex.matches(p.fileName.toString()) }
            .forEach { p -> delete(p) }

        delete(project.buildDir)
    }

    create("default") {
        val buildPlugin = project.tasks.getByPath("plugin:buildPlugin") as Zip

        dependsOn(buildPlugin)

        doLast {
            copy {
                from(buildPlugin.outputs)
                into(".")
            }
        }
    }

    create<Delete>("clean-sandbox") {
        group = "build"

        delete(project.file(".sandbox"))
    }
}
