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

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SelectionValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.UiValueType

typealias ProjectShowValue = SelectionValue<ProjectShow>

enum class ProjectShow(
    override val text: String,
    override val description: String? = null
) : UiValueType {
    // Do not reorder these, some logic depends on the order
    DISABLE("Hide Completely"),
    APPLICATION("Show Application"),
    PROJECT("Show Project"),
    ASK("Ask", "Show notification when first opening a new project"),
    PROJECT_FILES("Show Project and Files");

    companion object {
        val VALUES = arrayOf(PROJECT_FILES, PROJECT, APPLICATION, DISABLE)
        val VALUES_DEFAULT = arrayOf(ASK, PROJECT_FILES, PROJECT, APPLICATION, DISABLE)
    }
}
