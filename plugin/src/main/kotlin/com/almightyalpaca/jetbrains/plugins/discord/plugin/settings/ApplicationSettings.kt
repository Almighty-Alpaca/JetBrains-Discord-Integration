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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.ApplicationTypeValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.IconValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.TextValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.TimeValue
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.service
import org.jdom.Element

val settings: ApplicationSettings
    get() = service()

interface ApplicationSettings : PersistentStateComponent<Element>, OptionHolder {
    val show: BooleanValue

    val timeoutEnabled: BooleanValue
    val timeoutMinutes: IntValue
    val timeoutResetTimeEnabled: BooleanValue

    val filePrefixEnabled: BooleanValue
    val fileHideVcsIgnored: BooleanValue

    val applicationDetails: TextValue
    val applicationDetailsCustom: TemplateValue
    val applicationState: TextValue
    val applicationStateCustom: TemplateValue
    val applicationIconLarge: IconValue
    val applicationIconLargeText: TextValue
    val applicationIconLargeTextCustom: TemplateValue
    val applicationIconSmall: IconValue
    val applicationIconSmallText: TextValue
    val applicationIconSmallTextCustom: TemplateValue
    val applicationTime: TimeValue

    val projectDetails: TextValue
    val projectDetailsCustom: TemplateValue
    val projectState: TextValue
    val projectStateCustom: TemplateValue
    val projectIconLarge: IconValue
    val projectIconLargeText: TextValue
    val projectIconLargeTextCustom: TemplateValue
    val projectIconSmall: IconValue
    val projectIconSmallText: TextValue
    val projectIconSmallTextCustom: TemplateValue
    val projectTime: TimeValue

    val fileDetails: TextValue
    val fileDetailsCustom: TemplateValue
    val fileState: TextValue
    val fileStateCustom: TemplateValue
    val fileIconLarge: IconValue
    val fileIconLargeText: TextValue
    val fileIconLargeTextCustom: TemplateValue
    val fileIconSmall: IconValue
    val fileIconSmallText: TextValue
    val fileIconSmallTextCustom: TemplateValue
    val fileTime: TimeValue

    val applicationType: ApplicationTypeValue
    val theme: ThemeValue

    val applicationLastUpdateNotification: StringValue
}
