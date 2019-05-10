package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.impl

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.ApplicationSettings
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.impl.PersistentStateOptionHolderImpl
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.PresenceIcon
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.PresenceIconText
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.PresenceLine
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.PresenceTime
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "DiscordApplicationSettings", storages = [Storage("discord.xml")])
class ApplicationSettingsImpl : ApplicationSettings, PersistentStateOptionHolderImpl() {
    override val hide by check("Hide Rich Presence", false)

    private val timeoutToggle by toggleable<Boolean>()
    override val timeoutEnabled by timeoutToggle.toggle.check("Hide Rich Presence after inactivity", true)
    override val timeoutMinutes by timeoutToggle.option.spinner("Timeout", 5, 1..120, format = "# Minutes")

    private val group by group("Rich Presence Layout")
    private val preview by group.preview()
    private val tabs by preview.tabbed()

    /* ---------- Application Tab ---------- */

    private val applicationTab = tabs["Application"]

    private val applicationDetailsToggle by applicationTab.toggleable<PresenceLine>()
    override val applicationDetails by applicationDetailsToggle.toggle { it == PresenceLine.CUSTOM }.selection("First line", PresenceLine.Application1)
    override val applicationDetailsCustom by applicationDetailsToggle.option.text("Custom", "", 128)

    private val applicationStateToggle by applicationTab.toggleable<PresenceLine>()
    override val applicationState by applicationStateToggle.toggle { it == PresenceLine.CUSTOM }.selection("Second line", PresenceLine.Application2)
    override val applicationStateCustom by applicationStateToggle.option.text("Custom", "", 128)

    private val applicationIconLargeToggle by applicationTab.toggleable<PresenceIcon>()
    override val applicationIconLarge by applicationIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.Application)
    override val applicationIconLargeText by applicationIconLargeToggle.option.selection("Text", PresenceIconText.Large.Application)

    private val applicationIconSmallToggle by applicationTab.toggleable<PresenceIcon>()
    override val applicationIconSmall by applicationIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.Application)
    override val applicationIconSmallText by applicationIconSmallToggle.option.selection("Text", PresenceIconText.Small.Application)

    override val applicationTime by applicationTab.selection("Show elapsed time", PresenceTime.Application)

    /* ---------- Project Tab ---------- */

    private val projectTab = tabs["Project"]

    private val projectDetailsToggle by projectTab.toggleable<PresenceLine>()
    override val projectDetails by projectDetailsToggle.enableOn(PresenceLine.CUSTOM).selection("First line", PresenceLine.Project1)
    override val projectDetailsCustom by projectDetailsToggle.option.text("Custom", "", 128)

    private val projectStateToggle by projectTab.toggleable<PresenceLine>()
    override val projectState by projectStateToggle.enableOn(PresenceLine.CUSTOM).selection("Second line", PresenceLine.Project2)
    override val projectStateCustom by projectStateToggle.option.text("Custom", "", 128)

    private val projectIconLargeToggle by projectTab.toggleable<PresenceIcon>()
    override val projectIconLarge by projectIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.Project)
    override val projectIconLargeText by projectIconLargeToggle.option.selection("Text", PresenceIconText.Large.Project)

    private val projectIconSmallToggle by projectTab.toggleable<PresenceIcon>()
    override val projectIconSmall by projectIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.Project)
    override val projectIconSmallText by projectIconSmallToggle.option.selection("Text", PresenceIconText.Small.Project)

    override val projectTime by projectTab.selection("Show elapsed time", PresenceTime.Project)

    /* ---------- File Tab ---------- */

    private val fileTab = tabs["File"]

    private val fileDetailsToggle by fileTab.toggleable<PresenceLine>()
    override val fileDetails by fileDetailsToggle.enableOn(PresenceLine.CUSTOM).selection("First line", PresenceLine.File1)
    override val fileDetailsCustom by fileDetailsToggle.option.text("Custom", "", 128)

    private val fileStateToggle by fileTab.toggleable<PresenceLine>()
    override val fileState by fileStateToggle.enableOn(PresenceLine.CUSTOM).selection("Second line", PresenceLine.File2)
    override val fileStateCustom by fileStateToggle.option.text("Custom", "", 128)

    private val fileIconLargeToggle by fileTab.toggleable<PresenceIcon>()
    override val fileIconLarge by fileIconLargeToggle.disableOn(PresenceIcon.NONE).selection("Large icon", PresenceIcon.Large.File)
    override val fileIconLargeText by fileIconLargeToggle.option.selection("Text", PresenceIconText.Large.File)

    private val fileIconSmallToggle by fileTab.toggleable<PresenceIcon>()
    override val fileIconSmall by fileIconSmallToggle.disableOn(PresenceIcon.NONE).selection("Small icon", PresenceIcon.Small.File)
    override val fileIconSmallText by fileIconSmallToggle.option.selection("Text", PresenceIconText.Small.File)

    override val fileTime by fileTab.selection("Show elapsed time", PresenceTime.File)

    override val theme by themeChooser("Theme")
}
