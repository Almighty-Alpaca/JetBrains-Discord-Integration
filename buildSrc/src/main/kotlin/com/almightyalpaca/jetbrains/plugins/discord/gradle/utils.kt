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

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

fun hoplite(module: String? = null, version: String) = "com.sksamuel.hoplite:hoplite-$module:$version"

fun koin(module: String, version: String) = "org.koin:koin-$module:$version"

fun kotlinx(module: String, version: String? = null) = "org.jetbrains.kotlinx:kotlinx-$module:${version ?: ""}"

fun ktor(module: String, version: String? = null) = "io.ktor:ktor-$module:${version ?: ""}"

fun jooq(module: String? = null, version: String? = null) = "org.jooq:jooq${module?.let { "-$it" } ?: ""}:${version ?: ""}"

operator fun ExtraPropertiesExtension.contains(key: String) = has(key)

val isCi by lazy { System.getenv("CI") != null }

inline fun <reified T : Task> TaskContainer.register(name: String, action: Action<T>): TaskProvider<T> =
    register(name, T::class.java, action)
