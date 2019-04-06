package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.renderer

import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.ApplicationData
import com.almightyalpaca.jetbrains.plugins.discord.plugin.data.get
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.options.types.SimpleValue
import com.almightyalpaca.jetbrains.plugins.discord.plugin.settings.settings
import com.almightyalpaca.jetbrains.plugins.discord.shared.languages.LanguageMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.Theme
import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.ThemeMap
import com.almightyalpaca.jetbrains.plugins.discord.shared.themes.icons.IconSet

data class RenderContext(val themes: ThemeMap, val languages: LanguageMap, val application: ApplicationData, val mode: Renderer.Mode) {
    val theme: Theme = themes["material"] // TODO: theme selection
    val icons: IconSet = theme[application]

    val project = application.projects.values
            .filter { p -> !p.platformProject.settings.hide.getValue() }
            .maxBy { p -> p.accessedAt }
    val file = project?.files?.values?.maxBy { f -> f.accessedAt }

    val match by lazy { file?.let { languages.findLanguage(file) } }

    fun <T> SimpleValue<T>.getValue(): T = when (mode) {
        Renderer.Mode.NORMAL -> get()
        Renderer.Mode.PREVIEW -> getComponent()
    }
}
