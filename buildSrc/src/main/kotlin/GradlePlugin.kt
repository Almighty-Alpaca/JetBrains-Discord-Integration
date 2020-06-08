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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import java.io.File

open class DockerExtension {
    var tag: String = "latest"
    var devContainerName: String = "development"
    var buildContainerName: String = "builder"
    var localArchitecture: String = "linux/amd64"
}

fun Project.docker(action: DockerExtension.() -> Unit) = configure(action)

class DockerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply()
    }

    @JvmName("applyOnProject")
    fun Project.apply() {
        val extension = extensions.create<DockerExtension>("docker")

        val dockerBuildDir = File(buildDir, "docker")

        tasks.apply {
            val dockerCopy = register<Sync>("dockerCopy") {
                group = "docker"
                description = "Copies sources and Dockerfile to build directory"

                val shadowJar = project.tasks["shadowJar"] as ShadowJar

                dependsOn(shadowJar)

                from("Dockerfile")

                from(shadowJar.archiveFile) {
                    rename { "app.jar" }
                }

                into(dockerBuildDir)
            }

            val dockerContextCreate = register<Exec>("dockerContextCreate") {
                group = "docker"
                description = "Creates Docker build context"

                //                errorOutput = OutputStream.nullOutputStream()
                //                standardOutput = OutputStream.nullOutputStream()
                //                standardInput = InputStream.nullInputStream()

                isIgnoreExitValue = true

                commandLine = listOf(
                    "docker", "buildx", "create",
                    "--name", extension.buildContainerName
                )
            }

            val dockerContextUse = register<Exec>("dockerContextUse") {
                group = "docker"
                description = "Selects Docker build context as active one"

                dependsOn(dockerContextCreate)

                commandLine = listOf(
                    "docker", "buildx",
                    "use", extension.buildContainerName
                )
            }

            register<Exec>("dockerContextDelete") {
                group = "docker"
                description = "Deletes Docker build context"

                commandLine = listOf(
                    "docker", "buildx",
                    "rm", extension.buildContainerName
                )
            }

            val dockerPrepare = create("dockerPrepare") {
                group = "docker"
                description = "Copies files into build directory and prepares Docker build context"

                dependsOn(dockerCopy)
                dependsOn(dockerContextUse)
            }

            register<Exec>("dockerBuild") {
                group = "docker"
                description = "Builds Docker image"

                dependsOn(dockerPrepare)

                workingDir = dockerBuildDir

                commandLine = listOf(
                    "docker", "buildx", "build",
                    "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
                    "--tag", extension.tag,
                    "."
                )
            }

            val dockerBuildLoad = register<Exec>("dockerBuildLoad") {
                group = "docker"
                description = "Builds Docker image and loads it into local daemon"

                dependsOn(dockerPrepare)

                workingDir = dockerBuildDir

                commandLine = listOf(
                    "docker", "buildx", "build",
                    // TODO: build and export multi-arch manifest as soon as Docker supports it
                    // "--platform", "linux/amd64,linux/arm/v7",
                    "--platform", extension.localArchitecture,
                    "--tag", extension.tag,
                    "--load",
                    "."
                )
            }

            register<Exec>("dockerBuildPush") {
                group = "docker"
                description = "Builds Docker image and pushes it to Docker Hub"

                dependsOn(dockerPrepare)

                workingDir = dockerBuildDir

                commandLine = listOf(
                    "docker", "buildx", "build",
                    "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
                    "--tag", extension.tag,
                    "--push",
                    "."
                )
            }

            register<Exec>("dockerRun") {
                group = "docker"
                description = "Runs Docker image"

                dependsOn(dockerBuildLoad)

                commandLine = listOf(
                    "docker", "run",
                    "--name", extension.devContainerName,
                    "--rm",
                    "--mount", "type=bind,source=${project.file("config.yaml").absolutePath},target=/config/config.yaml",
                    extension.tag
                )
            }

            register<Exec>("dockerRunDaemon") {
                group = "docker"
                description = "Runs Docker image as daemon"

                dependsOn(dockerBuildLoad)

                commandLine = listOf(
                    "docker", "run",
                    "-d",
                    "--name", extension.devContainerName,
                    "--rm",
                    "--mount", "type=bind,source=${project.file("config.yaml").absolutePath},target=/config/config.yaml",
                    extension.tag
                )
            }

            register<Exec>("dockerStatus") {
                group = "docker"
                description = "Shows stats about running Docker image"

                commandLine = listOf(
                    "docker", "container", "ls",
                    "--filter", "name=${extension.devContainerName}"
                )
            }

            register<Exec>("dockerStop") {
                group = "docker"
                description = "Stops Docker image"

                commandLine = listOf(
                    "docker", "container", "stop", extension.devContainerName
                )
            }
        }
    }
}

private inline fun <reified T : Task> TaskContainer.register(name: String, action: Action<T>) =
    register(name, T::class.java, action)
