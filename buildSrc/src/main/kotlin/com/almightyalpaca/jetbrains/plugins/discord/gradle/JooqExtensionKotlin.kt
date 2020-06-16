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

import nu.studer.gradle.jooq.JooqConfiguration
import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqExtension
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target

/*
 * Original source: https://github.com/etiennestuder/gradle-jooq-plugin/blob/master/example/use_kotlin_dsl/buildSrc/src/main/kotlin/JooqExtensionExts.kt
 */

/**
 * Applies the supplied action to the Project's instance of [JooqExtensionKotlin]
 *
 * ```
 * jooq {
 *  val java = project.the<JavaPluginConvention>()
 *  "com.almightyalpaca.jetbrains.plugins.discord.gradle.schema"(java.sourceSets.getByName("main")) {
 *      jdbc {
 *          ....
 *      }
 *      generator {
 *          database {
 *              ...
 *          }
 *          target {
 *              ...
 *          }
 *      }
 * }
 * ```
 * @receiver [Project] The project for which the plugin configuration will be applied
 * @param action A configuration lambda to apply on a receiver of type [JooqExtensionKotlin]
 */
fun Project.jooq(action: JooqExtensionKotlin.() -> Unit) {
    project.configure<JooqExtension> {
        JooqExtensionKotlin(project, this).apply(action)
    }
}

/**
 * Applies jdbc configuration to [Configuration]
 *
 * @receiver the Jooq [Configuration]
 * @param action A configuration lambda to apply on a receiver of type [Configuration]
 */
fun Configuration.jdbc(action: Jdbc.() -> Unit) {
    this.withJdbc((this.jdbc ?: Jdbc()).apply(action))
}

/**
 * Applies generator configuration to [Configuration]
 *
 * @receiver the Jooq [Configuration]
 * @param action A configuration lambda to apply on a receiver of type [Configuration]
 */
fun Configuration.generator(action: Generator.() -> Unit) {
    this.withGenerator((this.generator ?: Generator()).apply(action))
}

/**
 * Applies database configuration to [Generator]
 *
 * @receiver the Jooq [Generator]
 * @param action A configuration lambda to apply on a receiver of type [Generator]
 */
fun Generator.database(action: Database.() -> Unit) {
    this.withDatabase((this.database ?: Database()).apply(action))
}

/**
 * Applies target configuration to [Generator]
 *
 * @receiver the Jooq [Generator]
 * @param action A configuration lambda to apply on a receiver of type [Generator]
 */
fun Generator.target(action: Target.() -> Unit) {
    this.withTarget((this.target ?: Target()).apply(action))
}

/**
 * Applies strategy configuration to [Generator]
 *
 * @receiver the Jooq [Generator]
 * @param action A configuration lambda to apply on a receiver of type [Generator]
 */
fun Generator.strategy(action: Strategy.() -> Unit) {
    this.withStrategy((this.strategy ?: Strategy()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.matchers configuration to [Strategy]
 *
 * @receiver the Jooq [Strategy]
 * @param action A configuration lambda to apply on a receiver of type [Strategy]
 */
fun Strategy.matchers(action: Matchers.() -> Unit) {
    this.withMatchers((this.matchers ?: Matchers()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.tables configuration to [Matchers]
 *
 * @receiver the Jooq [Matchers]
 * @param action A configuration lambda to apply on a receiver of type [Matchers]
 */
fun Matchers.tables(action: MutableList<MatchersTableType>.() -> Unit) {
    this.withTables((this.tables ?: mutableListOf()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.table configuration to [MutableList] of [MatchersTableType]
 *
 * @receiver the Jooq [MutableList] of [MatchersTableType]
 * @param action A configuration lambda to apply on a receiver of type [MutableList] of [MatchersTableType]
 */
fun MutableList<MatchersTableType>.table(action: MatchersTableType.() -> Unit) {
    this += MatchersTableType().apply(action)
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.pojoClass configuration to [MatchersTableType]
 *
 * @receiver the Jooq [MatchersTableType]
 * @param action A configuration lambda to apply on a receiver of type [MatchersTableType]
 */
fun MatchersTableType.pojoClass(action: MatcherRule.() -> Unit) {
    this.withPojoClass((this.pojoClass ?: MatcherRule()).apply(action))
}

/**
 * Applies generate configuration to [Generator]
 *
 * @receiver the Jooq [Generator]
 * @param action A configuration lambda to apply on a receiver of type [Generator]
 */
fun Generator.generate(action: Generate.() -> Unit) {
    this.withGenerate((this.generate ?: Generate()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.forcedTypes configuration to [Database]
 *
 * @receiver the Jooq [Database]
 * @param action A configuration lambda to apply on a receiver of type [Database]
 */
fun Database.forcedTypes(action: MutableList<ForcedType>.() -> Unit) {
    this.withForcedTypes((this.forcedTypes ?: mutableListOf()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.forcedType configuration to [MutableList] of [ForcedType]
 *
 * @receiver the Jooq [MutableList] of [ForcedType]
 * @param action A configuration lambda to apply on a receiver of type [MutableList] of [ForcedType]
 */
fun MutableList<ForcedType>.forcedType(action: ForcedType.() -> Unit) {
    this += ForcedType().apply(action)
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.schemata configuration to [Database]
 *
 * @receiver the Jooq [Database]
 * @param action A configuration lambda to apply on a receiver of type [Database]
 */
fun Database.schemata(action: MutableList<SchemaMappingType>.() -> Unit) {
    this.withSchemata((this.schemata ?: mutableListOf()).apply(action))
}

/**
 * Applies com.almightyalpaca.jetbrains.plugins.discord.gradle.schema configuration to [MutableList] of [SchemaMappingType]
 *
 * @receiver the Jooq [MutableList] of [SchemaMappingType]
 * @param action A configuration lambda to apply on a receiver of type [MutableList] of [SchemaMappingType]
 */
fun MutableList<SchemaMappingType>.schema(action: SchemaMappingType.() -> Unit) {
    this += SchemaMappingType().apply(action)
}

fun Database.properties(action: MutableList<Property>.() -> Unit) {
    this.withProperties((this.properties ?: mutableListOf()).apply(action))
}

fun MutableList<Property>.property(key: String, value: String) {
    this += Property().withKey(key).withValue(value)
}

/**
 * JooqExtension Wrapper that allows us to dynamically create configurations
 */
class JooqExtensionKotlin(
    private val project: Project,
    private val jooq: JooqExtension
) {

    var version: String
        set(value) {
            jooq.version = value
        }
        get() = jooq.version

    var edition: JooqEdition
        set(value) {
            jooq.edition = value
        }
        get() = jooq.edition

    var generateSchemaSourceOnCompilation: Boolean
        set(value) {
            jooq.generateSchemaSourceOnCompilation = value
        }
        get() = jooq.generateSchemaSourceOnCompilation

    operator fun String.invoke(sourceSet: SourceSet, action: Configuration.() -> Unit) {
        val jooqConfig = JooqConfiguration(
            this,
            sourceSet,
            Configuration()
        )
        jooq.whenConfigAdded.invoke(jooqConfig)
        if (jooq.generateSchemaSourceOnCompilation) {
            project.tasks.getByName(sourceSet.getCompileTaskName("kotlin")).dependsOn(jooqConfig.jooqTaskName.toString())
        }
        jooqConfig.configuration.apply(action)
    }
}
