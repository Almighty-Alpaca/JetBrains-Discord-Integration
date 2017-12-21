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

import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;

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
    private DiscordIntegrationApplicationSettings applicationSettings;
    private DiscordIntegrationProjectSettings projectSettings;

    public DiscordIntegrationSettingsPanel(DiscordIntegrationApplicationSettings applicationSettings, DiscordIntegrationProjectSettings projectSettings)
    {
        this.applicationSettings = applicationSettings;
        this.projectSettings = projectSettings;

        this.panelProject.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));
        this.panelApplication.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));
    }

    public boolean isModified()
    {
        // @formatter:off
        return (projectEnabled.isSelected() != projectSettings.getState().isEnabled())
                || (applicationEnabled.isSelected() != applicationSettings.getState().isEnabled())
                || (applicationUnknownImageIDE.isSelected() != applicationSettings.getState().isShowUnknownImageIDE())
                || (applicationUnknownImageFile.isSelected() != applicationSettings.getState().isShowUnknownImageFile())
                || (applicationShowFileExtensions.isSelected() != applicationSettings.getState().isShowFileExtensions())
                || (applicationHideReadOnlyFiles.isSelected() != applicationSettings.getState().isHideReadOnlyFiles());
        // @formatter:on
    }

    public void apply()
    {
        projectSettings.getState().setEnabled(projectEnabled.isSelected());
        applicationSettings.getState().setEnabled(applicationEnabled.isSelected());
        applicationSettings.getState().setShowUnknownImageIDE(applicationUnknownImageIDE.isSelected());
        applicationSettings.getState().setShowUnknownImageFile(applicationUnknownImageFile.isSelected());
        applicationSettings.getState().setShowFileExtensions(applicationShowFileExtensions.isSelected());
        applicationSettings.getState().setHideReadOnlyFiles(applicationHideReadOnlyFiles.isSelected());
    }

    public void reset()
    {
        projectEnabled.setSelected(projectSettings.getState().isEnabled());
        applicationEnabled.setSelected(applicationSettings.getState().isEnabled());
        applicationUnknownImageIDE.setSelected(applicationSettings.getState().isShowUnknownImageIDE());
        applicationUnknownImageFile.setSelected(applicationSettings.getState().isShowUnknownImageFile());
        applicationShowFileExtensions.setSelected(applicationSettings.getState().isShowFileExtensions());
        applicationHideReadOnlyFiles.setSelected(applicationSettings.getState().isHideReadOnlyFiles());
    }

    public JPanel getRootPanel()
    {
        return panelRoot;
    }
}
