/*
 * Copyright 2017-2019 Aljoscha Grebe
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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.IntValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ThemeValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.utils.lazyService
import com.intellij.openapi.components.PersistentStateComponent
import org.jdom.Element

interface ApplicationSettings : PersistentStateComponent<Element>, OptionHolder {
    val show: BooleanValue

    val timeoutEnabled: BooleanValue
    val timeoutMinutes: IntValue
    val timeoutResetTimeEnabled: BooleanValue

    val filePrefixEnabled: BooleanValue

    val applicationDetails: LineValue
    val applicationDetailsCustom: StringValue
    val applicationState: LineValue
    val applicationStateCustom: StringValue
    val applicationIconLarge: IconValue
    val applicationIconLargeText: IconTextValue
    val applicationIconSmall: IconValue
    val applicationIconSmallText: IconTextValue
    val applicationTime: TimeValue

    val projectDetails: LineValue
    val projectDetailsCustom: StringValue
    val projectState: LineValue
    val projectStateCustom: StringValue
    val projectIconLarge: IconValue
    val projectIconLargeText: IconTextValue
    val projectIconSmall: IconValue
    val projectIconSmallText: IconTextValue
    val projectTime: TimeValue

    val fileDetails: LineValue
    val fileDetailsCustom: StringValue
    val fileState: LineValue
    val fileStateCustom: StringValue
    val fileIconLarge: IconValue
    val fileIconLargeText: IconTextValue
    val fileIconSmall: IconValue
    val fileIconSmallText: IconTextValue
    val fileTime: TimeValue

    val theme: ThemeValue

    val newProjectShow: NewProjectShowValue
}

val settings: ApplicationSettings by lazyService()
