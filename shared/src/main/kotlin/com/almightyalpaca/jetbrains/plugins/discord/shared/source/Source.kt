/*
 * Copyright 2017-2019 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
