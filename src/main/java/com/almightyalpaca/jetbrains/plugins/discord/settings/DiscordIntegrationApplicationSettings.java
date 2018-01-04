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
package com.almightyalpaca.jetbrains.plugins.discord.settings;

import com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage.ApplicationSettingsStorage;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "DiscordIntegrationApplicationSettings", storages = @Storage("discord.xml"))
public class DiscordIntegrationApplicationSettings implements PersistentStateComponent<ApplicationSettingsStorage>, SettingsProvider<ApplicationSettingsStorage>
{
    @NotNull
    private final Application application;
    @NotNull
    private final ApplicationSettingsStorage state = new ApplicationSettingsStorage();

    public DiscordIntegrationApplicationSettings(@NotNull Application application)
    {
        this.application = application;
    }

    @NotNull
    public static DiscordIntegrationApplicationSettings getInstance()
    {
        return ServiceManager.getService(DiscordIntegrationApplicationSettings.class);
    }

    @NotNull
    public Application getApplication()
    {
        return this.application;
    }

    @NotNull
    @Override
    public ApplicationSettingsStorage getState()
    {
        return this.state;
    }

    @NotNull
    @Override
    public ApplicationSettingsStorage getSettings()
    {
        return this.state;
    }

    @Override
    public void loadState(@NotNull ApplicationSettingsStorage state)
    {
        this.state.clone(state);
    }
}
