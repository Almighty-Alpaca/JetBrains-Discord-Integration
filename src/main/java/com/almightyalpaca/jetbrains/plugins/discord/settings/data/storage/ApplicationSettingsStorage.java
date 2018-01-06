/*
 * Copyright 2017 Aljoscha Grebe
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
package com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage;

import com.almightyalpaca.jetbrains.plugins.discord.JetbrainsDiscordIntegration;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ApplicationSettingsStorage extends SettingsStorage<ApplicationSettingsStorage, ApplicationSettings<ApplicationSettingsStorage>> implements ApplicationSettings<ApplicationSettingsStorage>
{
    @NotNull
    public static final TimeUnit INACTIVITY_TIMEOUT_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final long INACTIVITY_TIMEOUT_DEFAULT_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(10, TimeUnit.MINUTES);
    public static final long INACTIVITY_TIMEOUT_MIN_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(1, TimeUnit.MINUTES);
    public static final long INACTIVITY_TIMEOUT_MAX_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(1, TimeUnit.DAYS);

    private static final long serialVersionUID = JetbrainsDiscordIntegration.PROTOCOL_VERSION;
    @NotNull
    private static final Gson GSON = new Gson();

    private boolean showUnknownImageIDE = true;
    private boolean showUnknownImageFile = true;
    private boolean showFileExtensions = true;
    private boolean hideReadOnlyFiles = true;
    private boolean showReadingInsteadOfWriting = true;
    private boolean showIDEWhenNoProjectIsAvailable = true;
    private boolean hideAfterPeriodOfInactivity = true;
    private long inactivityTimeout = INACTIVITY_TIMEOUT_DEFAULT_VALUE;

    public ApplicationSettingsStorage()
    {
        super(ApplicationSettingsStorage.class);
    }

    @Override
    public boolean isShowFileExtensions()
    {
        return this.showFileExtensions;
    }

    @NotNull
    public ApplicationSettingsStorage setShowFileExtensions(boolean showFileExtensions)
    {
        this.showFileExtensions = showFileExtensions;
        return this;
    }

    @Override
    public boolean isShowUnknownImageIDE()
    {
        return this.showUnknownImageIDE;
    }

    @NotNull
    public ApplicationSettingsStorage setShowUnknownImageIDE(boolean showUnknownImageIDE)
    {
        this.showUnknownImageIDE = showUnknownImageIDE;
        return this;
    }

    @Override
    public boolean isShowUnknownImageFile()
    {
        return this.showUnknownImageFile;
    }

    @NotNull
    public ApplicationSettingsStorage setShowUnknownImageFile(boolean showUnknownImageFile)
    {
        this.showUnknownImageFile = showUnknownImageFile;
        return this;
    }

    @Override
    public boolean isHideReadOnlyFiles()
    {
        return this.hideReadOnlyFiles;
    }

    @NotNull
    public ApplicationSettingsStorage setHideReadOnlyFiles(boolean hideReadOnlyFiles)
    {
        this.hideReadOnlyFiles = hideReadOnlyFiles;
        return this;
    }

    @Override
    public boolean isShowReadingInsteadOfWriting()
    {
        return this.showReadingInsteadOfWriting;
    }

    @NotNull
    public ApplicationSettingsStorage setShowReadingInsteadOfWriting(boolean showReadingInsteadOfWriting)
    {
        this.showReadingInsteadOfWriting = showReadingInsteadOfWriting;
        return this;
    }

    @Override
    public boolean isShowIDEWhenNoProjectIsAvailable()
    {
        return showIDEWhenNoProjectIsAvailable;
    }

    @NotNull
    public ApplicationSettingsStorage setShowIDEWhenNoProjectIsAvailable(boolean showIDEWhenNoProjectIsAvailable)
    {
        this.showIDEWhenNoProjectIsAvailable = showIDEWhenNoProjectIsAvailable;
        return this;
    }

    public boolean isHideAfterPeriodOfInactivity()
    {
        return hideAfterPeriodOfInactivity;
    }

    @NotNull
    public ApplicationSettingsStorage setHideAfterPeriodOfInactivity(boolean hideAfterPeriodOfInactivity)
    {
        this.hideAfterPeriodOfInactivity = hideAfterPeriodOfInactivity;
        return this;
    }

    @Override
    public long getInactivityTimeout(TimeUnit unit)
    {
        return unit.convert(inactivityTimeout, INACTIVITY_TIMEOUT_TIMEUNIT);
    }

    @NotNull
    public ApplicationSettingsStorage setInactivityTimeout(long inactivityTimeout, @NotNull TimeUnit unit)
    {
        this.inactivityTimeout = INACTIVITY_TIMEOUT_TIMEUNIT.convert(inactivityTimeout, unit);
        return this;
    }

    @NotNull
    @Override
    public ApplicationSettingsStorage clone(@NotNull ApplicationSettings<ApplicationSettingsStorage> settings)
    {
        // @formatter:off
        return super.clone(settings)
                .setShowUnknownImageFile(settings.isShowUnknownImageFile())
                .setShowUnknownImageIDE(settings.isShowUnknownImageIDE())
                .setShowFileExtensions(settings.isShowFileExtensions())
                .setHideReadOnlyFiles(settings.isHideReadOnlyFiles())
                .setShowReadingInsteadOfWriting(settings.isShowReadingInsteadOfWriting())
                .setShowIDEWhenNoProjectIsAvailable(settings.isShowIDEWhenNoProjectIsAvailable())
                .setHideAfterPeriodOfInactivity(settings.isHideAfterPeriodOfInactivity())
                .setInactivityTimeout(settings.getInactivityTimeout(INACTIVITY_TIMEOUT_TIMEUNIT), INACTIVITY_TIMEOUT_TIMEUNIT);
        // @formatter:on
    }

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o)
    {
        // @formatter:off
        if (this == o)
            return true;
        if (!(o instanceof ApplicationSettingsStorage))
            return false;
        ApplicationSettingsStorage that = (ApplicationSettingsStorage) o;
        return super.equals(that)
                && this.isShowUnknownImageIDE() == that.isShowUnknownImageIDE()
                && this.isShowUnknownImageFile() == that.isShowUnknownImageFile()
                && this.isShowFileExtensions() == that.isShowFileExtensions()
                && this.isHideReadOnlyFiles() == that.isHideReadOnlyFiles()
                && this.isShowReadingInsteadOfWriting() == that.isShowReadingInsteadOfWriting()
                && this.isShowIDEWhenNoProjectIsAvailable() == that.isShowIDEWhenNoProjectIsAvailable()
                && this.isHideAfterPeriodOfInactivity() == that.isHideAfterPeriodOfInactivity()
                && this.getInactivityTimeout(INACTIVITY_TIMEOUT_TIMEUNIT) == that.getInactivityTimeout(INACTIVITY_TIMEOUT_TIMEUNIT);
        // @formatter:on
    }

    @Override
    public int hashCode()
    {
        // @formatter:off
        return Objects.hash(
                super.hashCode(),
                this.isShowUnknownImageIDE(),
                this.isShowUnknownImageFile(),
                this.isShowFileExtensions(),
                this.isHideReadOnlyFiles(),
                this.isShowReadingInsteadOfWriting(),
                this.isShowIDEWhenNoProjectIsAvailable(),
                this.isHideAfterPeriodOfInactivity(),
                this.getInactivityTimeout(INACTIVITY_TIMEOUT_TIMEUNIT));
        // @formatter:on
    }
}
