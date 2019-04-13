package com.almightyalpaca.jetbrains.plugins.discord.shared.source

interface Source {
    fun getLanguages(): LanguageMap
    fun getThemes(): ThemeMap

    fun getLanguagesOrNull(): LanguageMap?
    fun getThemesOrNull(): ThemeMap?
}
