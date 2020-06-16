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

package com.almightyalpaca.jetbrains.plugins.discord.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get
import java.io.File

@Suppress("unused")
class DockerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(ShadowPlugin::class)
        target.extensions.add("docker", DockerExtension(target))
    }
}

class DockerExtension(private val project: Project) {
    private val configurations = mutableMapOf<String, DockerConfiguration>()

    operator fun String.invoke(action: DockerConfiguration.() -> Unit = {}): DockerConfiguration {
        return configurations.computeIfAbsent(this.toLowerCase()) { DockerConfiguration(this, project) }.apply(action)
    }

    operator fun get(name: String) = configurations[name]
}

class DockerConfiguration(val name: String, project: Project) {
    var tag: String = "latest"
    var devContainerName: String = "development"
    var buildContainerName: String = "builder"
    var localArchitecture: String = "linux/amd64"
    var dockerfile: File = project.file("Dockerfile")

    internal val mounts = mutableListOf<String>()
    fun mount(mount: String) = mounts.add(mount)
    fun mounts(mount: Iterable<String>) = mounts.addAll(mount)

    internal lateinit var dependency: TaskProvider<*>

    init {
        project.createTasks(this)
    }
}

private fun Project.createTasks(configuration: DockerConfiguration) {
    val dockerBuildDir = File(buildDir, "docker")

    val name = configuration.name.capitalize()

    tasks.apply {
        val dockerCopy = register<Sync>("docker${name}Copy") {
            group = "docker"
            description = "Copies sources and Dockerfile to build directory"

            val shadowJar = project.tasks["shadowJar"] as ShadowJar

            dependsOn(shadowJar)

            from(configuration.dockerfile)

            from(shadowJar.archiveFile) {
                rename { "app.jar" }
            }

            into(dockerBuildDir)
        }

        val dockerContextCreate = register<Exec>("docker${name}ContextCreate") {
            group = "docker"
            description = "Creates Docker build context"

            //                errorOutput = OutputStream.nullOutputStream()
            //                standardOutput = OutputStream.nullOutputStream()
            //                standardInput = InputStream.nullInputStream()

            isIgnoreExitValue = true

            commandLine = listOf(
                "docker", "buildx", "create",
                "--name", configuration.buildContainerName
            )
        }

        val dockerContextUse = register<Exec>("docker${name}ContextUse") {
            group = "docker"
            description = "Selects Docker build context as active one"

            dependsOn(dockerContextCreate)

            commandLine = listOf(
                "docker", "buildx",
                "use", configuration.buildContainerName
            )
        }

        register<Exec>("docker${name}ContextDelete") {
            group = "docker"
            description = "Deletes Docker build context"

            commandLine = listOf(
                "docker", "buildx",
                "rm", configuration.buildContainerName
            )
        }

        val dockerPrepare = create("docker${name}Prepare") {
            group = "docker"
            description = "Copies files into build directory and prepares Docker build context"

            dependsOn(dockerCopy)
            dependsOn(dockerContextUse)
        }

        register<Exec>("docker${name}Build") {
            group = "docker"
            description = "Builds Docker image"

            dependsOn(dockerPrepare)

            workingDir = dockerBuildDir

            commandLine = listOf(
                "docker", "buildx", "build",
                "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
                "--tag", configuration.tag,
                "."
            )
        }

        val dockerBuildLoad = register<Exec>("docker${name}BuildLoad") {
            group = "docker"
            description = "Builds Docker image and loads it into local daemon"

            dependsOn(dockerPrepare)

            workingDir = dockerBuildDir

            commandLine = listOf(
                "docker", "buildx", "build",
                // TODO: build and export multi-arch manifest as soon as Docker supports it
                // "--platform", "linux/amd64,linux/arm/v7",
                "--platform", configuration.localArchitecture,
                "--tag", configuration.tag,
                "--load",
                "."
            )
        }

        configuration.dependency = dockerBuildLoad

        register<Exec>("docker${name}BuildPush") {
            group = "docker"
            description = "Builds Docker image and pushes it to Docker Hub"

            dependsOn(dockerPrepare)

            workingDir = dockerBuildDir

            commandLine = listOf(
                "docker", "buildx", "build",
                "--platform", "linux/amd64,linux/arm64,linux/arm/v7",
                "--tag", configuration.tag,
                "--push",
                "."
            )
        }

        val mounts = configuration.mounts.insertBefore { "--mount" }

        register<Exec>("docker${name}Run") {
            group = "docker"
            description = "Runs Docker image"

            dependsOn(dockerBuildLoad)

            commandLine = mutableListOf(
                "docker", "run",
                "--name", configuration.devContainerName,
                "--rm"
            )
            commandLine.addAll(mounts)
            commandLine.add(configuration.tag)
        }

        register<Exec>("docker${name}RunDaemon") {
            group = "docker"
            description = "Runs Docker image as daemon"

            dependsOn(dockerBuildLoad)

            commandLine = mutableListOf(
                "docker", "run",
                "-d",
                "--name", configuration.devContainerName,
                "--rm"
            )
            commandLine.addAll(mounts)
            commandLine.add(configuration.tag)
        }

        register<Exec>("docker${name}Status") {
            group = "docker"
            description = "Shows stats about running Docker image"

            commandLine = listOf(
                "docker", "container", "ls",
                "--filter", "name=${configuration.devContainerName}"
            )
        }

        register<Exec>("docker${name}Stop") {
            group = "docker"
            description = "Stops Docker image"

            commandLine = listOf(
                "docker", "container", "stop", configuration.devContainerName
            )
        }
    }
}

private fun <E> List<E>.insertBefore(function: (Int) -> E): List<E> {
    return MutableList(size * 2) {
        when (it % 2 == 0) {
            true -> function(it / 2)
            false -> this[it / 2]
        }
    }
}
