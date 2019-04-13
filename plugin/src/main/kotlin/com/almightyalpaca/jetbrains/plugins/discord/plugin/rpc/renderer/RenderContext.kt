package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.getIconSet
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.IconSet
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.ThemeMap

data class RenderContext(val themes: ThemeMap?, val languages: LanguageMap?, val application: ApplicationData, val mode: Renderer.Mode) {
    val theme: Theme? = themes?.get("material") // TODO: theme selection
    val icons: IconSet? = theme?.getIconSet(application)

    val project = application.projects.values
            .filter { p -> !p.platformProject.settings.hide.getValue() }
            .maxBy { p -> p.accessedAt }
    val file = project?.files?.values?.maxBy { f -> f.accessedAt }

    val match by lazy { file?.let { languages?.findLanguage(file) } }

    fun <T> SimpleValue<T>.getValue(): T = when (mode) {
        Renderer.Mode.NORMAL -> get()
        Renderer.Mode.PREVIEW -> getComponent()
    }
}
