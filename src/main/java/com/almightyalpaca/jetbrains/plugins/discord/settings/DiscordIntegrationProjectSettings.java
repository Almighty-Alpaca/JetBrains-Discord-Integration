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

import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage.ProjectSettingsStorage;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@State(name = "DiscordIntegrationProjectSettings", storages = @Storage("discord.xml"))
public class DiscordIntegrationProjectSettings implements PersistentStateComponent<ProjectSettingsStorage>, SettingsProvider<ProjectSettings<?>>
{
    @NotNull
    private final Project project;
    @NotNull
    private final ProjectSettingsStorage state = new ProjectSettingsStorage();

    public DiscordIntegrationProjectSettings(@NotNull Project project)
    {
        this.project = project;
    }

    @NotNull
    public static DiscordIntegrationProjectSettings getInstance(@NotNull Project project)
    {
        return ServiceManager.getService(project, DiscordIntegrationProjectSettings.class);
    }

    @NotNull
    public Project getProject()
    {
        return this.project;
    }

    @NotNull
    @Override
    public ProjectSettingsStorage getState()
    {
        return this.state;
    }

    @NotNull
    @Override
    public ProjectSettings<ProjectSettingsStorage> getSettings()
    {
        return this.state;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsStorage state)
    {
        this.state.clone(state);
    }
}
