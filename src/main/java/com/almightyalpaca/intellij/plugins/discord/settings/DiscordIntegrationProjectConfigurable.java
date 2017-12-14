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
package com.almightyalpaca.intellij.plugins.discord.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DiscordIntegrationProjectConfigurable implements SearchableConfigurable
{
    @NotNull
    private final Project project;
    @NotNull
    private final DiscordIntegrationApplicationSettings optionsProviderApplication;
    @NotNull
    private final DiscordIntegrationProjectSettings optionsProviderProject;
    @Nullable
    private DiscordIntegrationSettingsPanel panel;

    public DiscordIntegrationProjectConfigurable(@NotNull Project project)
    {
        this.project = project;

        this.optionsProviderApplication = DiscordIntegrationApplicationSettings.getInstance();
        this.optionsProviderProject = DiscordIntegrationProjectSettings.getInstance(project);
    }

    public static DiscordIntegrationProjectConfigurable getInstance(@NotNull Project project)
    {
        return ServiceManager.getService(project, DiscordIntegrationProjectConfigurable.class);
    }

    @NotNull
    public Project getProject()
    {
        return project;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName()
    {
        return "Discord";
    }

    @Nullable
    @Override
    public String getHelpTopic()
    {
        return "Discord Integration SettingsStorage";
    }

    @NotNull
    @Override
    public JComponent createComponent()
    {
        this.panel = new DiscordIntegrationSettingsPanel();
        return panel.createPanel(optionsProviderApplication, optionsProviderProject);
    }

    @Override
    public boolean isModified()
    {
        return panel != null && panel.isModified();
    }

    @Override
    public void apply()
    {
        if (panel != null)
            panel.apply();

        // TODO: set settings on data
    }

    @Override
    public void reset()
    {
        if (panel != null)
            panel.reset();
    }

    @Override
    public void disposeUIResources()
    {
        panel = null;
    }

    @NotNull
    @Override
    public String getId()
    {
        return "preferences.discord.project";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option)
    {
        return null;
    }
}

