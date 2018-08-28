/*
 * Copyright 2017-2018 Aljoscha Grebe
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
package com.almightyalpaca.jetbrains.plugins.discord.settings.data;

import com.almightyalpaca.jetbrains.plugins.discord.themes.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public interface ApplicationSettings extends Settings
{
    boolean isShowFileExtensions();

    boolean isShowUnknownImageIDE();

    boolean isShowUnknownImageFile();

    boolean isHideReadOnlyFiles();

    boolean isShowReadingInsteadOfWriting();

    boolean isShowIDEWhenNoProjectIsAvailable();

    boolean isHideAfterPeriodOfInactivity();

    long getInactivityTimeout(TimeUnit unit);

    boolean isResetOpenTimeAfterInactivity();

    boolean isExperimentalWindowListenerEnabled();

    boolean isDebugLoggingEnabled();

    @Nullable
    String getDebugLogFolder();

    boolean isShowFiles();

    boolean isShowElapsedTime();

    boolean isForceBigIDEIcon();

    @NotNull
    Theme getTheme();
}
