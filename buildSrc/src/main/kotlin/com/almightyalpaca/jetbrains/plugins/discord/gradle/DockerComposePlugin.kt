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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskInputs
import java.io.File

@Suppress("unused")
class DockerComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.add("dockerCompose", DockerComposeExtension(target))
    }
}

class DockerComposeExtension(private val project: Project) {
    private val configurations = mutableMapOf<String, DockerComposeConfiguration>()

    operator fun String.invoke(action: DockerComposeConfiguration.() -> Unit = {}): DockerComposeConfiguration {
        return configurations.compute(this.toLowerCase()) { name, existing ->
            if (existing != null)
                throw IllegalArgumentException("""Configuration "$name" already exists""")

            DockerComposeConfiguration(this, project, action)
        }!!
    }

    operator fun get(name: String) = configurations[name]
}

class DockerComposeConfiguration(val name: String, project: Project, configure: DockerComposeConfiguration.() -> Unit) {
    var projectContainer: String? = null
    var file: File = project.file("docker-compose.yml")

    internal var copySpec: (CopySpec.() -> Unit) = {}
    fun copySpec(action: CopySpec.() -> Unit) {
        val old = copySpec
        copySpec = {
            old()
            action()
        }
    }

    internal var inputs: (TaskInputs.() -> Unit) = {}
    fun inputs(action: TaskInputs.() -> Unit) {
        val old = inputs
        inputs = {
            old()
            action()
        }
    }

    internal val copyDependencies = mutableListOf<Any>()
    fun copyDependsOn(any: Any) {
        copyDependencies.add(resolveTaskDependency(any))
    }

    internal val runDependencies = mutableListOf<Any>()
    fun runDependsOn(any: Any) {
        runDependencies.add(resolveTaskDependency(any))
    }

    init {
        configure()

        project.createTasks(this)
    }
}

private fun resolveTaskDependency(any: Any): Any = when (any) {
    is DockerConfiguration -> any.dependency
    else -> any
}

private fun Project.createTasks(configuration: DockerComposeConfiguration) {
    val name = configuration.name.capitalize()
    val projectName = project.group.toString().replace('.', '_')

    tasks.apply {
        val dockerCopy = register<Copy>("dockerCompose${name}Copy") {
            dependsOn(*configuration.copyDependencies.toTypedArray())

            from(configuration.file) {
                rename { "docker-compose.yml" }
            }

            run(configuration.copySpec)
            inputs.run(configuration.inputs)

            into(buildDir.resolve("docker-compose/${configuration.name}"))
        }

        register<Exec>("dockerCompose${name}Up") {
            group = "docker"
            description = "Builds, (re)creates, starts, and attaches to containers for a service"

            dependsOn(dockerCopy)

            dependsOn(*configuration.runDependencies.toTypedArray())

            workingDir = dockerCopy.get().destinationDir
            commandLine = listOf(
                "docker-compose",
                "--project-name", projectName,
                "up"
            )
        }

        register<Exec>("dockerCompose${name}UpDaemon") {
            group = "docker"
            description = "Builds, (re)creates, and starts containers for a service as daemon"

            dependsOn(dockerCopy)

            dependsOn(*configuration.runDependencies.toTypedArray())

            workingDir = dockerCopy.get().destinationDir
            commandLine = listOf(
                "docker-compose",
                "--project-name", projectName,
                "up",
                "-d"
            )
        }

        val projectContainer = configuration.projectContainer
        if (projectContainer != null) {

            register<Exec>("dockerCompose${name}UpDev") {
                group = "docker"
                description = "Builds, (re)creates, starts, and attaches to containers for a service without the development container"

                dependsOn(dockerCopy)

                dependsOn(*configuration.runDependencies.toTypedArray())

                workingDir = dockerCopy.get().destinationDir
                commandLine = listOf(
                    "docker-compose",
                    "--project-name", projectName,
                    "up",
                    "--scale", "$projectContainer=0"
                )
            }

            register<Exec>("dockerCompose${name}UpDevDaemon") {
                group = "docker"
                description = "Builds, (re)creates, and starts containers for a service without the development container as daemon"

                dependsOn(dockerCopy)

                dependsOn(*configuration.runDependencies.toTypedArray())

                workingDir = dockerCopy.get().destinationDir
                commandLine = listOf(
                    "docker-compose",
                    "--project-name", projectName,
                    "up",
                    "--scale", "$projectContainer=0",
                    "-d"
                )
            }

        }

        register<Exec>("dockerCompose${name}Down") {
            group = "docker"
            description = "Stops containers and removes containers, networks and images created by up"

            dependsOn(dockerCopy)

            workingDir = dockerCopy.get().destinationDir
            commandLine = listOf(
                "docker-compose",
                "--project-name", projectName,
                "down"
            )
        }

        register<Exec>("dockerCompose${name}Annihilate") {
            group = "docker"
            description = "Stops containers and removes containers, networks, volumes, and images created by up"

            dependsOn(dockerCopy)

            workingDir = dockerCopy.get().destinationDir
            commandLine = listOf(
                "docker-compose",
                "--project-name", projectName,
                "down",
                "--volumes"
            )
        }

        register<Exec>("dockerCompose${name}Pull") {
            group = "docker"
            description = "Pulls an image associated with a service, but does not start containers based on those images"

            dependsOn(dockerCopy)

            workingDir = dockerCopy.get().destinationDir
            commandLine = listOf(
                "docker-compose",
                "--project-name", projectName,
                "pull",
                // TODO: remove once https://github.com/docker/compose/pull/7134 is merged
                "--no-parallel",
                "--ignore-pull-failures"
            )
        }
    }
}
