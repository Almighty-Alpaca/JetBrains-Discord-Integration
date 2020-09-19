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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType
import com.intellij.openapi.application.ApplicationNamesInfo
import java.util.*

typealias ApplicationTypeValue = SimpleValue<ApplicationType>

enum class ApplicationType(override val text: String, override val description: String? = null) : UiValueType {
    JETBRAINS("JetBrains IDE") {
        override val applicationNameReadable = "JetBrains IDE"
    },
    IDE("IDE Name", description = "e.g. IntelliJ IDEA") {
        override val applicationNameReadable: String by lazy {
            ApplicationNamesInfo.getInstance()
                .fullProductName
        }
    },
    IDE_EDITION("IDE Name and Edition", description = "e.g. IntelliJ IDEA Ultimate") {
        override val applicationNameReadable: String by lazy {
            ApplicationNamesInfo.getInstance()
                .fullProductNameWithEdition
                .replace("Edition", "")
                .trim()
        }
    };

    abstract val applicationNameReadable: String

    val applicationName by lazy {
        applicationNameReadable.split(' ')
            .asSequence()
            .map { it.toLowerCase(Locale.ENGLISH) }
            .joinToString(separator = "_")
    }
}
