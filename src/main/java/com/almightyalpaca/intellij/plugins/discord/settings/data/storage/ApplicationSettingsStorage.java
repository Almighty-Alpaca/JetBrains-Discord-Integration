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

public class ApplicationSettingsStorage extends SettingsStorage<ApplicationSettingsStorage, ApplicationSettings<ApplicationSettingsStorage>> implements ApplicationSettings<ApplicationSettingsStorage>
{
    private boolean showUnknownImageIDE = true;
    private boolean showUnknownImageFile = true;
    private boolean showFileExtensions = true;

    public ApplicationSettingsStorage()
    {
        super(ApplicationSettingsStorage.class, ApplicationSettingsStorage::new);
    }

    @Override
    public boolean isShowFileExtensions()
    {
        return showFileExtensions;
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
        return showUnknownImageIDE;
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
        return showUnknownImageFile;
    }

    @NotNull
    public ApplicationSettingsStorage setShowUnknownImageFile(boolean showUnknownImageFile)
    {
        this.showUnknownImageFile = showUnknownImageFile;
        return this;
    }

    @NotNull
    @Override
    public ApplicationSettingsStorage clone()
    {
        return super.clone().setShowUnknownImageFile(showUnknownImageFile).setShowUnknownImageIDE(showUnknownImageIDE).setShowFileExtensions(showFileExtensions);
    }

    @NotNull
    @Override
    public ApplicationSettingsStorage clone(ApplicationSettings<ApplicationSettingsStorage> settings)
    {
        return super.clone(settings).setShowUnknownImageFile(settings.isShowUnknownImageFile()).setShowUnknownImageIDE(settings.isShowUnknownImageIDE()).setShowFileExtensions(settings.isShowFileExtensions());
    }
}
