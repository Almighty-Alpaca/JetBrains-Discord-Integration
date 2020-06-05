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

package com.almightyalpaca.jetbrains.plugins.discord.analytics.server.jooq.codegen

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.Definition

@Suppress("unused")
class SingularNameGeneratorStrategy : DefaultGeneratorStrategy() {
    private val entityModes = listOf(
        GeneratorStrategy.Mode.RECORD,
        GeneratorStrategy.Mode.POJO,
        GeneratorStrategy.Mode.INTERFACE,
        GeneratorStrategy.Mode.DAO
    )

    override fun getJavaClassName(definition: Definition, mode: GeneratorStrategy.Mode): String = when (mode) {
        in entityModes -> super.getJavaClassName(SingularDifinition(definition), mode)
        else -> super.getJavaClassName(definition, mode)
    }
}

class SingularDifinition(definition: Definition) : Definition by definition {
    override fun getOutputName(): String = when {
        name.endsWith("s", ignoreCase = true) -> name.dropLast(1)
        else -> name
    }
}
