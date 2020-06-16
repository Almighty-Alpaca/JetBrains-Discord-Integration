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
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import java.nio.file.Files
import java.util.*

@Suppress("unused")
class SecretsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target == target.rootProject) {
            target.extensions.add(SecretsExtension::class, "secrets", SecretsExtensionRoot(target))
        } else {
            val rootProject = target.rootProject
            if (!rootProject.plugins.hasPlugin(SecretsPlugin::class)) {
                rootProject.plugins.apply(SecretsPlugin::class)
            }

            val root = rootProject.extensions.getByType<SecretsExtension>() as SecretsExtensionRoot

            target.extensions.add(SecretsExtension::class, "secrets", SecretsExtensionDelegate(root))
        }
    }
}

interface SecretsExtension {
    val exists: Boolean

    val tokens: Tokens
    val server: Server

    val checkTask: TaskProvider<out Task>

    fun get(name: String): String?

    class Tokens internal constructor(extension: SecretsExtensionRoot) {
        val discord: String? = extension.get("tokens.discord")
        val bintray: String? = extension.get("tokens.bintray")
        val jetbrains: String? = extension.get("tokens.jetbrains")
    }

    class Server internal constructor(extension: SecretsExtensionRoot) {
        val authentication = Authentication(extension)

        class Authentication internal constructor(extension: SecretsExtensionRoot) {
            val token: String? = extension.get("server.authentication.token")
        }

        val database = Database(extension)

        class Database internal constructor(extension: SecretsExtensionRoot) {
            val url: String? = extension.get("server.database.url")
            val user: String? = extension.get("server.database.user")
            val password: String? = extension.get("server.database.password")
            val name: String? = extension.get("server.database.name")
        }
    }
}

class SecretsExtensionDelegate(root: SecretsExtensionRoot) : SecretsExtension by root

class SecretsExtensionRoot(project: Project) : SecretsExtension {
    private val path = project.rootDir.toPath().resolve("secrets.properties").toAbsolutePath()

    override val exists = Files.exists(path)

    private val properties = Properties().apply {
        if (exists) {
            load(Files.newBufferedReader(path))
        }
    }

    override fun get(name: String): String? = properties.getProperty(name)

    override val tokens = SecretsExtension.Tokens(this)

    override val server = SecretsExtension.Server(this)

    override val checkTask: TaskProvider<out Task> = project.createCheckTask(this)
}

private fun Project.createCheckTask(extension: SecretsExtension): TaskProvider<out Task> = tasks.register("checkSecrets") task@{
    allprojects {
        afterEvaluate {
            tasks.all {
                mustRunAfter(this@task)
            }
        }
    }

    doLast {
        if (!extension.exists) {
            throw IllegalStateException("Secrets not found. Please copy the 'secrets.template.properties' to 'secrets.properties' and fill in the relevant data")
        }
    }
}
