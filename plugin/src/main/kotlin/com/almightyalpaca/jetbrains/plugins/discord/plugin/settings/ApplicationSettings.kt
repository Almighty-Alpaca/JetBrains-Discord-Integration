package com.almightyalpaca.jetbrains.plugins.discord.plugin.settings

import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.OptionHolder
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.BooleanValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.IntValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.StringValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.ThemeValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.IconTextValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.IconValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.LineValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.values.TimeValue
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import org.jdom.Element

interface ApplicationSettings : PersistentStateComponent<Element>, OptionHolder {
    val hide: BooleanValue

    val timeoutEnabled: BooleanValue
    val timeoutMinutes: IntValue

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
}

val settings: ApplicationSettings
    get() = ServiceManager.getService(ApplicationSettings::class.java)
