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
package com.almightyalpaca.jetbrains.plugins.discord.settings;

import com.almightyalpaca.jetbrains.plugins.discord.debug.Debug;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscordIntegrationSettingsPanel
{
    private final DiscordIntegrationApplicationSettings applicationSettings;
    private final DiscordIntegrationProjectSettings projectSettings;
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
    private JBCheckBox applicationShowIDEWhenNoProjectIsAvailable;
    private JBCheckBox applicationHideAfterPeriodOfInactivity;
    private JSpinner applicationInactivityTimeout;
    private JLabel applicationInactivityTimeoutLabel;
    private JBCheckBox applicationResetOpenTimeAfterInactivity;
    private JPanel panelExperimental;
    private JBCheckBox applicationExperimentalWindowListenerEnabled;
    private JPanel panelDebug;
    private JBCheckBox applicationDebuggingEnabled;
    private TextFieldWithBrowseButton applicationDebugLogFolder;
    private JButton buttonDumpCurrentState;

    public DiscordIntegrationSettingsPanel(DiscordIntegrationApplicationSettings applicationSettings, DiscordIntegrationProjectSettings projectSettings)
    {
        this.applicationSettings = applicationSettings;
        this.projectSettings = projectSettings;

        this.panelProject.setBorder(IdeBorderFactory.createTitledBorder("Project Settings (" + projectSettings.getProject().getName() + ")"));
        this.panelApplication.setBorder(IdeBorderFactory.createTitledBorder("Application Settings"));
        this.panelExperimental.setBorder(IdeBorderFactory.createTitledBorder("Experimental Settings"));
        this.panelDebug.setBorder(IdeBorderFactory.createTitledBorder("Debugging Settings"));

        this.applicationHideReadOnlyFiles.addItemListener(e -> this.applicationShowReadingInsteadOfEditing.setEnabled(!this.applicationHideReadOnlyFiles.isSelected()));

        this.applicationHideAfterPeriodOfInactivity.addItemListener(e -> {
            this.applicationInactivityTimeoutLabel.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());
            this.applicationInactivityTimeout.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());
            this.applicationResetOpenTimeAfterInactivity.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());
        });

        this.applicationDebuggingEnabled.addItemListener(e -> this.applicationDebugLogFolder.setEnabled(this.applicationDebuggingEnabled.isSelected()));
        this.applicationDebuggingEnabled.addItemListener(e -> this.buttonDumpCurrentState.setEnabled(this.applicationDebuggingEnabled.isSelected()));

        this.applicationDebugLogFolder.setTextFieldPreferredWidth(60);
        this.applicationDebugLogFolder.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()));
        this.applicationDebugLogFolder.getTextField().getDocument().addDocumentListener(new DocumentAdapter()
        {
            @Override
            protected void textChanged(DocumentEvent e)
            {
                verifyLogFolder();
            }
        });

        this.buttonDumpCurrentState.addActionListener(e -> Debug.printDebugInfo());
    }

    public boolean isModified()
    {
        // @formatter:off
        return this.projectEnabled.isSelected() != this.projectSettings.getState().isEnabled()
                || this.applicationEnabled.isSelected() != this.applicationSettings.getState().isEnabled()
                || this.applicationUnknownImageIDE.isSelected() != this.applicationSettings.getState().isShowUnknownImageIDE()
                || this.applicationUnknownImageFile.isSelected() != this.applicationSettings.getState().isShowUnknownImageFile()
                || this.applicationShowFileExtensions.isSelected() != this.applicationSettings.getState().isShowFileExtensions()
                || this.applicationHideReadOnlyFiles.isSelected() != this.applicationSettings.getState().isHideReadOnlyFiles()
                || this.applicationShowReadingInsteadOfEditing.isSelected() != this.applicationSettings.getState().isShowReadingInsteadOfWriting()
                || this.applicationShowIDEWhenNoProjectIsAvailable.isSelected() != this.applicationSettings.getState().isShowIDEWhenNoProjectIsAvailable()
                || this.applicationHideAfterPeriodOfInactivity.isSelected() != this.applicationSettings.getState().isHideAfterPeriodOfInactivity()
                || (long) this.applicationInactivityTimeout.getValue() != this.applicationSettings.getState().getInactivityTimeout(TimeUnit.MINUTES)
                || this.applicationResetOpenTimeAfterInactivity.isSelected() != this.applicationSettings.getState().isResetOpenTimeAfterInactivity()
                || this.applicationExperimentalWindowListenerEnabled.isSelected() != this.applicationSettings.getState().isExperimentalWindowListenerEnabled()
                || this.applicationDebuggingEnabled.isSelected() != this.applicationSettings.getState().isDebugLoggingEnabled()
                || !Objects.equals(this.applicationDebugLogFolder.getText(), this.applicationSettings.getState().getDebugLogFolder());
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
        this.applicationSettings.getState().setShowReadingInsteadOfWriting(this.applicationShowReadingInsteadOfEditing.isSelected());
        this.applicationSettings.getState().setShowIDEWhenNoProjectIsAvailable(this.applicationShowIDEWhenNoProjectIsAvailable.isSelected());
        this.applicationSettings.getState().setHideAfterPeriodOfInactivity(this.applicationHideAfterPeriodOfInactivity.isSelected());
        this.applicationSettings.getState().setInactivityTimeout((long) this.applicationInactivityTimeout.getValue(), TimeUnit.MINUTES);
        this.applicationSettings.getState().setExperimentalWindowListenerEnabled(this.applicationExperimentalWindowListenerEnabled.isSelected());
        this.applicationSettings.getState().setResetOpenTimeAfterInactivity(this.applicationResetOpenTimeAfterInactivity.isSelected());
        this.applicationSettings.getState().setDebugLoggingEnabled(this.applicationDebuggingEnabled.isSelected());

        if (verifyLogFolder())
            this.applicationSettings.getState().setDebugLogFolder(this.applicationDebugLogFolder.getText());
    }

    public void reset()
    {
        this.projectEnabled.setSelected(this.projectSettings.getState().isEnabled());
        this.applicationEnabled.setSelected(this.applicationSettings.getState().isEnabled());
        this.applicationUnknownImageIDE.setSelected(this.applicationSettings.getState().isShowUnknownImageIDE());
        this.applicationUnknownImageFile.setSelected(this.applicationSettings.getState().isShowUnknownImageFile());
        this.applicationShowFileExtensions.setSelected(this.applicationSettings.getState().isShowFileExtensions());
        this.applicationHideReadOnlyFiles.setSelected(this.applicationSettings.getState().isHideReadOnlyFiles());
        this.applicationShowReadingInsteadOfEditing.setSelected(this.applicationSettings.getState().isShowReadingInsteadOfWriting());
        this.applicationShowIDEWhenNoProjectIsAvailable.setSelected(this.applicationSettings.getState().isShowIDEWhenNoProjectIsAvailable());
        this.applicationHideAfterPeriodOfInactivity.setSelected(this.applicationSettings.getState().isHideAfterPeriodOfInactivity());
        this.applicationInactivityTimeout.setValue(this.applicationSettings.getState().getInactivityTimeout(TimeUnit.MINUTES));
        this.applicationResetOpenTimeAfterInactivity.setSelected(this.applicationSettings.getState().isResetOpenTimeAfterInactivity());
        this.applicationExperimentalWindowListenerEnabled.setSelected(this.applicationSettings.getState().isExperimentalWindowListenerEnabled());
        this.applicationDebuggingEnabled.setSelected(this.applicationSettings.getState().isDebugLoggingEnabled());
        this.applicationDebugLogFolder.setText(this.applicationSettings.getState().getDebugLogFolder());

        verifyLogFolder();
    }

    private boolean verifyLogFolder()
    {
        Path path;
        try
        {
            path = Paths.get(this.applicationDebugLogFolder.getText());
        }
        catch (Exception e)
        {
            this.applicationDebugLogFolder.getTextField().setForeground(JBColor.RED);
            this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(new JBPopupMenu("Invalid path"));

            return false;
        }

        if (Files.exists(path) && !Files.isWritable(path))
        {
            this.applicationDebugLogFolder.getTextField().setForeground(JBColor.RED);
            this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(new JBPopupMenu("Cannot write to this path"));

            return false;
        }

        if (!Files.isDirectory(path))
        {
            this.applicationDebugLogFolder.getTextField().setForeground(JBColor.RED);
            this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(new JBPopupMenu("Path is a file"));

            return false;
        }

        this.applicationDebugLogFolder.getTextField().setForeground(JBColor.foreground());
        this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(null);

        return true;
    }

    @NotNull
    public JPanel getRootPanel()
    {
        return this.panelRoot;
    }

    private void createUIComponents()
    {
        Long value = 1L;
        Long minimum = 1L;
        Long maximum = TimeUnit.MINUTES.convert(1, TimeUnit.DAYS);
        Long stepSize = 1L;

        this.applicationInactivityTimeout = new JSpinner(new SpinnerNumberModel(value, minimum, maximum, stepSize));
    }
}
