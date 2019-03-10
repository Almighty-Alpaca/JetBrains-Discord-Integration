package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.*
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresenceData
import com.almightyalpaca.jetbrains.plugins.shared.languages.Icon
import com.almightyalpaca.jetbrains.plugins.shared.languages.LanguageMap
import com.almightyalpaca.jetbrains.plugins.shared.themes.Theme
import com.almightyalpaca.jetbrains.plugins.shared.themes.ThemeMap
import com.almightyalpaca.jetbrains.plugins.shared.themes.icons.IconSet

interface RichPresenceRenderContext {
    val theme: Theme
    val icons: IconSet
    val application: ApplicationData
    val project: ProjectData?
    val file: FileData?
    val FileData.icon: Icon

    operator fun component1() = application
    operator fun component2() = project
    operator fun component3() = file

    fun render(): RichPresenceData {
        Logger.Level.TRACE { "RichPresenceRenderContext#render()" }

        return RichPresenceRenderer(this).render()
    }

    class Impl(private val languages: LanguageMap, themes: ThemeMap, override val application: ApplicationData) : RichPresenceRenderContext {
        override val theme: Theme = themes["material"]
        override val icons: IconSet = theme[application]
        override val project: ProjectData? = application.projects.values.maxBy { p -> p.accessedAt }
        override val file: FileData? = project?.run { files.values.maxBy { f -> f.accessedAt } }
        override val FileData.icon: Icon
            get() = languages.findLanguage(fieldProvider).findIcon(icons)
    }

    companion object : Logging()
}

fun ApplicationData.getContext(languages: LanguageMap, themes: ThemeMap) = RichPresenceRenderContext.Impl(languages, themes, this)
