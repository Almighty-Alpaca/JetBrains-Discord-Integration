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

package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ApplicationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.PersistentStateOptionHolderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.*
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "DiscordApplicationSettings", storages = [Storage("discord.xml")])
class ApplicationSettingsImpl : ApplicationSettings, PersistentStateOptionHolderImpl() {
    override val show by check("Enable Rich Presence", true)

    private val timeoutToggle by toggleable<Boolean>(false)
    override val timeoutEnabled by timeoutToggle.toggle.check("Hide Rich Presence after inactivity", true)

    private val timeoutOptionPair by timeoutToggle.option.pair()
    override val timeoutMinutes by timeoutOptionPair.first.spinner("Timeout", 5, 1..120, format = "# Minutes")
    override val timeoutResetTimeEnabled by timeoutOptionPair.second.check("Reset open time", true)

    private val group by group("Rich Presence Layout")
    private val preview by group.preview()
    private val tabs by preview.tabbed()

    /* ---------- Application Tab ---------- */

    private val applicationTab = tabs["Application"]

    private val applicationDetailsToggle by applicationTab.toggleable<PresenceText>()
    override val applicationDetails by applicationDetailsToggle.toggle { it == PresenceText.CUSTOM }.selection("First line", PresenceText.Application1)
    override val applicationDetailsCustom by applicationDetailsToggle.option.text("Custom", "", 128)

    private val applicationStateToggle by applicationTab.toggleable<PresenceText>()
    override val applicationState by applicationStateToggle.toggle { it == PresenceText.CUSTOM }.selection("Second line", PresenceText.Application2)
    override val applicationStateCustom by applicationStateToggle.option.text("Custom", "", 128)

    private val applicationIconLargeToggle by applicationTab.toggleable<PresenceIcon>()
    override val applicationIconLarge by applicationIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.Application)
    private val applicationIconLargeTextToggle by applicationIconLargeToggle.option.toggleable<PresenceText>()
    override val applicationIconLargeText by applicationIconLargeTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.ApplicationIconLarge)
    override val applicationIconLargeTextCustom by applicationIconLargeTextToggle.option.text("Custom", "", 128)

    private val applicationIconSmallToggle by applicationTab.toggleable<PresenceIcon>()
    override val applicationIconSmall by applicationIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.Application)
    private val applicationIconSmallTextToggle by applicationIconSmallToggle.option.toggleable<PresenceText>()
    override val applicationIconSmallText by applicationIconSmallTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.ApplicationIconSmall)
    override val applicationIconSmallTextCustom by applicationIconSmallTextToggle.option.text("Custom", "", 128)

    override val applicationTime by applicationTab.selection("Show elapsed time", PresenceTime.Application)

    /* ---------- Project Tab ---------- */

    private val projectTab = tabs["Project"]

    private val projectDetailsToggle by projectTab.toggleable<PresenceText>()
    override val projectDetails by projectDetailsToggle.enableOn(PresenceText.CUSTOM).selection("First line", PresenceText.Project1)
    override val projectDetailsCustom by projectDetailsToggle.option.text("Custom", "", 128)

    private val projectStateToggle by projectTab.toggleable<PresenceText>()
    override val projectState by projectStateToggle.enableOn(PresenceText.CUSTOM).selection("Second line", PresenceText.Project2)
    override val projectStateCustom by projectStateToggle.option.text("Custom", "", 128)

    private val projectIconLargeToggle by projectTab.toggleable<PresenceIcon>()
    override val projectIconLarge by projectIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.Project)
    private val projectIconLargeTextToggle by projectIconLargeToggle.option.toggleable<PresenceText>()
    override val projectIconLargeText by projectIconLargeTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.ProjectIconLarge)
    override val projectIconLargeTextCustom by projectIconLargeTextToggle.option.text("Custom", "", 128)

    private val projectIconSmallToggle by projectTab.toggleable<PresenceIcon>()
    override val projectIconSmall by projectIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.Project)
    private val projectIconSmallTextToggle by projectIconSmallToggle.option.toggleable<PresenceText>()
    override val projectIconSmallText by projectIconSmallTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.ProjectIconSmall)
    override val projectIconSmallTextCustom by projectIconSmallTextToggle.option.text("Custom", "", 128)

    override val projectTime by projectTab.selection("Show elapsed time", PresenceTime.Project)

    /* ---------- File Tab ---------- */

    private val fileTab = tabs["File"]

    private val fileDetailsToggle by fileTab.toggleable<PresenceText>()
    override val fileDetails by fileDetailsToggle.enableOn(PresenceText.CUSTOM).selection("First line", PresenceText.File1)
    override val fileDetailsCustom by fileDetailsToggle.option.text("Custom", "", 128)

    private val fileStateToggle by fileTab.toggleable<PresenceText>()
    override val fileState by fileStateToggle.enableOn(PresenceText.CUSTOM).selection("Second line", PresenceText.File2)
    override val fileStateCustom by fileStateToggle.option.text("Custom", "", 128)

    private val fileIconLargeToggle by fileTab.toggleable<PresenceIcon>()
    override val fileIconLarge by fileIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.File)
    private val fileIconLargeTextToggle by fileIconLargeToggle.option.toggleable<PresenceText>()
    override val fileIconLargeText by fileIconLargeTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.FileIconLarge)
    override val fileIconLargeTextCustom by fileIconLargeTextToggle.option.text("Custom", "", 128)

    private val fileIconSmallToggle by fileTab.toggleable<PresenceIcon>()
    override val fileIconSmall by fileIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.File)
    private val fileIconSmallTextToggle by fileIconSmallToggle.option.toggleable<PresenceText>()
    override val fileIconSmallText by fileIconSmallTextToggle.enableOn(PresenceText.CUSTOM).selection("Text", PresenceText.FileIconSmall)
    override val fileIconSmallTextCustom by fileIconSmallTextToggle.option.text("Custom", "", 128)

    override val fileTime by fileTab.selection("Show elapsed time", PresenceTime.File)

    override val filePrefixEnabled by fileTab.check("Prefix files names with Reading/Editing", true)

    override val fileHideVcsIgnored by fileTab.check("Hide VCS ignored files", false)

    override val applicationType by selection("Application name", ApplicationType.IDE_EDITION)
    override val theme by themeChooser("Theme")

    /* ---------- Hidden Settings ---------- */

    private val hidden by hidden()

    override val applicationLastUpdateNotification by hidden.text("<unused>", "")
}
