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
package com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage;

import com.almightyalpaca.jetbrains.plugins.discord.JetbrainsDiscordIntegration;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings;
import com.google.gson.Gson;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ApplicationSettingsStorage extends SettingsStorage implements ApplicationSettings
{
    @NotNull
    public static final TimeUnit INACTIVITY_TIMEOUT_TIMEUNIT = TimeUnit.MILLISECONDS;
    public static final long INACTIVITY_TIMEOUT_DEFAULT_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(10, TimeUnit.MINUTES);
    public static final long INACTIVITY_TIMEOUT_MIN_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(1, TimeUnit.MINUTES);
    public static final long INACTIVITY_TIMEOUT_MAX_VALUE = INACTIVITY_TIMEOUT_TIMEUNIT.convert(1, TimeUnit.DAYS);

    private static final long serialVersionUID = JetbrainsDiscordIntegration.PROTOCOL_VERSION;
    @NotNull
    private static final Gson GSON = new Gson();

    @Attribute
    private boolean showUnknownImageIDE = true;
    @Attribute
    private boolean showUnknownImageFile = true;
    @Attribute
    private boolean showFileExtensions = true;
    @Attribute
    private boolean hideReadOnlyFiles = true;
    @Attribute
    private boolean showReadingInsteadOfWriting = true;
    @Attribute
    private boolean showIDEWhenNoProjectIsAvailable = true;
    @Attribute
    private boolean hideAfterPeriodOfInactivity = true;
    @Attribute
    private long inactivityTimeout = INACTIVITY_TIMEOUT_DEFAULT_VALUE;
    @Attribute
    private boolean resetOpenTimeAfterInactivity = true;

    @Override
    public boolean isShowFileExtensions()
    {
        return this.showFileExtensions;
    }

    public void setShowFileExtensions(boolean showFileExtensions)
    {
        this.showFileExtensions = showFileExtensions;
    }

    @Override
    public boolean isShowUnknownImageIDE()
    {
        return this.showUnknownImageIDE;
    }

    public void setShowUnknownImageIDE(boolean showUnknownImageIDE)
    {
        this.showUnknownImageIDE = showUnknownImageIDE;
    }

    @Override
    public boolean isShowUnknownImageFile()
    {
        return this.showUnknownImageFile;
    }

    public void setShowUnknownImageFile(boolean showUnknownImageFile)
    {
        this.showUnknownImageFile = showUnknownImageFile;
    }

    @Override
    public boolean isHideReadOnlyFiles()
    {
        return this.hideReadOnlyFiles;
    }

    public void setHideReadOnlyFiles(boolean hideReadOnlyFiles)
    {
        this.hideReadOnlyFiles = hideReadOnlyFiles;
    }

    @Override
    public boolean isShowReadingInsteadOfWriting()
    {
        return this.showReadingInsteadOfWriting;
    }

    public void setShowReadingInsteadOfWriting(boolean showReadingInsteadOfWriting)
    {
        this.showReadingInsteadOfWriting = showReadingInsteadOfWriting;
    }

    @Override
    public boolean isShowIDEWhenNoProjectIsAvailable()
    {
        return showIDEWhenNoProjectIsAvailable;
    }

    public void setShowIDEWhenNoProjectIsAvailable(boolean showIDEWhenNoProjectIsAvailable)
    {
        this.showIDEWhenNoProjectIsAvailable = showIDEWhenNoProjectIsAvailable;
    }

    public boolean isHideAfterPeriodOfInactivity()
    {
        return hideAfterPeriodOfInactivity;
    }

    public void setHideAfterPeriodOfInactivity(boolean hideAfterPeriodOfInactivity)
    {
        this.hideAfterPeriodOfInactivity = hideAfterPeriodOfInactivity;
    }

    @Override
    public long getInactivityTimeout(TimeUnit unit)
    {
        return unit.convert(inactivityTimeout, INACTIVITY_TIMEOUT_TIMEUNIT);
    }

    protected long getInactivityTimeout()
    {
        return inactivityTimeout;
    }

    public void setInactivityTimeout(long inactivityTimeout, @NotNull TimeUnit unit)
    {
        this.inactivityTimeout = INACTIVITY_TIMEOUT_TIMEUNIT.convert(inactivityTimeout, unit);
    }

    @Override
    public boolean isResetOpenTimeAfterInactivity()
    {
        return resetOpenTimeAfterInactivity;
    }

    public void setResetOpenTimeAfterInactivity(boolean resetOpenTimeAfterInactivity)
    {
        this.resetOpenTimeAfterInactivity = resetOpenTimeAfterInactivity;
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
                && this.getInactivityTimeout() == that.getInactivityTimeout()
                && this.isResetOpenTimeAfterInactivity() == that.isResetOpenTimeAfterInactivity();
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
                this.getInactivityTimeout(),
                this.isResetOpenTimeAfterInactivity());
        // @formatter:on
    }
}
