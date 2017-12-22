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
    private JBCheckBox applicationShowReadingInsteadOfEditing;
    private DiscordIntegrationApplicationSettings applicationSettings;
    private DiscordIntegrationProjectSettings projectSettings;

    public DiscordIntegrationSettingsPanel(DiscordIntegrationApplicationSettings applicationSettings, DiscordIntegrationProjectSettings projectSettings)
    {
        this.applicationSettings = applicationSettings;
        this.projectSettings = projectSettings;

        this.panelProject.setBorder(IdeBorderFactory.createTitledBorder("Project settings (" + projectSettings.getProject().getName() + ")"));
        this.panelApplication.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));

        this.applicationShowReadingInsteadOfEditing.setEnabled(!this.applicationHideReadOnlyFiles.isSelected());
        this.applicationHideReadOnlyFiles.addItemListener(e -> {
            this.applicationShowReadingInsteadOfEditing.setEnabled(!this.applicationHideReadOnlyFiles.isSelected());
        });
    }

    public boolean isModified()
    {
        // @formatter:off
        return (this.projectEnabled.isSelected() != this.projectSettings.getState().isEnabled())
                || (this.applicationEnabled.isSelected() != this.applicationSettings.getState().isEnabled())
                || (this.applicationUnknownImageIDE.isSelected() != this.applicationSettings.getState().isShowUnknownImageIDE())
                || (this.applicationUnknownImageFile.isSelected() != this.applicationSettings.getState().isShowUnknownImageFile())
                || (this.applicationShowFileExtensions.isSelected() != this.applicationSettings.getState().isShowFileExtensions())
                || (this.applicationHideReadOnlyFiles.isSelected() != this.applicationSettings.getState().isHideReadOnlyFiles());
        // @formatter:on
    }

    public void apply()
    {
        this.projectSettings.getState().setEnabled(this.projectEnabled.isSelected());
        this.applicationSettings.getState().setEnabled(this.applicationEnabled.isSelected());
        this.applicationSettings.getState().setShowUnknownImageIDE(this.applicationUnknownImageIDE.isSelected());
        this.applicationSettings.getState().setShowUnknownImageFile(this.applicationUnknownImageFile.isSelected());
        this.applicationSettings.getState().setShowFileExtensions(this.applicationShowFileExtensions.isSelected());
        this.applicationSettings.getState().setHideReadOnlyFiles(this.applicationHideReadOnlyFiles.isSelected());
    }

    public void reset()
    {
        this.projectEnabled.setSelected(this.projectSettings.getState().isEnabled());
        this.applicationEnabled.setSelected(this.applicationSettings.getState().isEnabled());
        this.applicationUnknownImageIDE.setSelected(this.applicationSettings.getState().isShowUnknownImageIDE());
        this.applicationUnknownImageFile.setSelected(this.applicationSettings.getState().isShowUnknownImageFile());
        this.applicationShowFileExtensions.setSelected(this.applicationSettings.getState().isShowFileExtensions());
        this.applicationHideReadOnlyFiles.setSelected(this.applicationSettings.getState().isHideReadOnlyFiles());
    }

    @NotNull
    public JPanel getRootPanel()
    {
        return this.panelRoot;
    }
}
