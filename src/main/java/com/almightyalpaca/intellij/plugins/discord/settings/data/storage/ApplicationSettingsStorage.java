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
package com.almightyalpaca.intellij.plugins.discord.settings.data.storage;

import com.almightyalpaca.intellij.plugins.discord.settings.data.ApplicationSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ApplicationSettingsStorage extends SettingsStorage<ApplicationSettingsStorage, ApplicationSettings<ApplicationSettingsStorage>> implements ApplicationSettings<ApplicationSettingsStorage>
{
    private boolean showUnknownImageIDE = true;
    private boolean showUnknownImageFile = true;
    private boolean showFileExtensions = true;
    private boolean hideReadOnlyFiles = true;
    private boolean showReadingInsteadOfWriting = true;

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
                .setShowReadingInsteadOfWriting(settings.isShowReadingInsteadOfWriting());
        // @formatter:on
    }

    @NotNull
    @Override
    public String toString()
    {
        // @formatter:off
        return "ApplicationSettingsStorage{" + "showUnknownImageIDE=" + this.showUnknownImageIDE
                + ", showUnknownImageFile=" + this.showUnknownImageFile
                + ", showFileExtensions=" + this.showFileExtensions
                + ", hideReadOnlyFiles=" + this.hideReadOnlyFiles
                + ", showReadingInsteadOfWriting=" + this.showReadingInsteadOfWriting + '}';
        // @formatter:on
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
                && this.isShowReadingInsteadOfWriting() == that.isShowReadingInsteadOfWriting();
        // @formatter:on
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), this.isShowUnknownImageIDE(), this.isShowUnknownImageFile(), this.isShowFileExtensions(), this.isHideReadOnlyFiles(), this.isShowReadingInsteadOfWriting());
    }
}
