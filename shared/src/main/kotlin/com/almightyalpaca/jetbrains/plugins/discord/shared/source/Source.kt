package com.almightyalpaca.jetbrains.plugins.discord.shared.source

import com.almightyalpaca.jetbrains.plugins.discord.shared.utils.getCompletedOrNull
import kotlinx.coroutines.Deferred

interface Source {
    fun getLanguagesAsync(): Deferred<LanguageMap>
    fun getThemesAsync(): Deferred<ThemeMap>

    suspend fun getLanguages(): LanguageMap = getLanguagesAsync().await()
    suspend fun getThemes(): ThemeMap = getThemesAsync().await()

    fun getLanguagesOrNull(): LanguageMap? = getLanguagesAsync().getCompletedOrNull()
    fun getThemesOrNull(): ThemeMap? = getThemesAsync().getCompletedOrNull()
}
