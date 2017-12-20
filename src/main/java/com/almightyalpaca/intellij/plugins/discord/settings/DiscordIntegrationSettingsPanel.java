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

import com.almightyalpaca.intellij.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.intellij.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationApplicationService;
import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationProjectService;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DiscordIntegrationSettingsPanel
{
    private JPanel panelRoot;
    private JPanel panelProject;
    private JBCheckBox projectEnabled;
    private JPanel panelApplication;
    private JBCheckBox applicationEnabled;
    private JBCheckBox applicationUnknownImageIDE;
    private JBCheckBox applicationUnknownImageFile;
    private JBCheckBox applicationShowFileExtensions;
    private JBCheckBox applicationHideReadOnlyFiles;
    private DiscordIntegrationApplicationSettings optionsProviderApplication;
    private DiscordIntegrationProjectSettings optionsProviderProject;

    public JComponent createPanel(@NotNull DiscordIntegrationApplicationSettings provider, @NotNull DiscordIntegrationProjectSettings projectOptionsProvider)
    {
        optionsProviderApplication = provider;
        optionsProviderProject = projectOptionsProvider;

        panelProject.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));
        panelApplication.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));

        return panelRoot;
    }

    public boolean isModified()
    {
        // @formatter:off
        return (projectEnabled.isSelected() != optionsProviderProject.getState().isEnabled())
                || (applicationEnabled.isSelected() != optionsProviderApplication.getState().isEnabled())
                || (applicationUnknownImageIDE.isSelected() != optionsProviderApplication.getState().isShowUnknownImageIDE())
                || (applicationUnknownImageFile.isSelected() != optionsProviderApplication.getState().isShowUnknownImageFile())
                || (applicationShowFileExtensions.isSelected() != optionsProviderApplication.getState().isShowFileExtensions())
                || (applicationHideReadOnlyFiles.isSelected() != optionsProviderApplication.getState().isHideReadOnlyFiles());
        // @formatter:on
    }

    public void apply()
    {
        optionsProviderProject.getState().setEnabled(projectEnabled.isSelected());
        optionsProviderApplication.getState().setEnabled(applicationEnabled.isSelected());
        optionsProviderApplication.getState().setShowUnknownImageIDE(applicationUnknownImageIDE.isSelected());
        optionsProviderApplication.getState().setShowUnknownImageFile(applicationUnknownImageFile.isSelected());
        optionsProviderApplication.getState().setShowFileExtensions(applicationUnknownImageFile.isSelected());
        optionsProviderApplication.getState().setHideReadOnlyFiles(applicationHideReadOnlyFiles.isSelected());

        DiscordIntegrationApplicationService service = DiscordIntegrationApplicationService.getInstance();

        InstanceInfo instance = service.getInstanceInfo();
        ProjectInfo project = DiscordIntegrationProjectService.getInstance(optionsProviderProject.getProject()).getProjectInfo();

        service.getData().instanceSetSettings(instance, optionsProviderApplication.getSettings());
        service.getData().projectSetSettings(instance, project, optionsProviderProject.getSettings());
    }

    public void reset()
    {
        projectEnabled.setSelected(optionsProviderProject.getState().isEnabled());
        applicationEnabled.setSelected(optionsProviderApplication.getState().isEnabled());
        applicationUnknownImageIDE.setSelected(optionsProviderApplication.getState().isShowUnknownImageIDE());
        applicationUnknownImageFile.setSelected(optionsProviderApplication.getState().isShowUnknownImageFile());
        applicationShowFileExtensions.setSelected(optionsProviderApplication.getState().isShowFileExtensions());
        applicationHideReadOnlyFiles.setSelected(optionsProviderApplication.getState().isHideReadOnlyFiles());
    }
}
