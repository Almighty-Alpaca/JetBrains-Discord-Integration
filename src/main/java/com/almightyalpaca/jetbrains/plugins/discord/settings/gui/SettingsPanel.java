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
package com.almightyalpaca.jetbrains.plugins.discord.settings.gui;

import com.almightyalpaca.jetbrains.plugins.discord.components.ApplicationComponent;
import com.almightyalpaca.jetbrains.plugins.discord.debug.Debug;
import com.almightyalpaca.jetbrains.plugins.discord.debug.Logger;
import com.almightyalpaca.jetbrains.plugins.discord.debug.LoggerFactory;
import com.almightyalpaca.jetbrains.plugins.discord.settings.ApplicationSettings;
import com.almightyalpaca.jetbrains.plugins.discord.settings.ProjectSettings;
import com.almightyalpaca.jetbrains.plugins.discord.settings.gui.themes.ThemeChooser;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Theme;
import com.almightyalpaca.jetbrains.plugins.discord.themes.ThemeLoader;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class SettingsPanel
{
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponent.class);

    private final ApplicationSettings applicationSettings;
    private final ProjectSettings projectSettings;
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
    private JBCheckBox applicationDebugLoggingEnabled;
    private TextFieldWithBrowseButton applicationDebugLogFolder;
    private JButton buttonDumpCurrentState;
    private JButton buttonOpenDebugLogFolder;
    private JBCheckBox applicationShowFiles;
    private JBTextField projectDescription;
    private JBCheckBox applicationShowElapsedTime;
    private JBCheckBox applicationForceBigIDEIcon;
    private Theme applicationTheme;
    private JButton applicationThemeButton;
    private JBLabel applicationThemeLabel;

    public SettingsPanel(ApplicationSettings applicationSettings, ProjectSettings projectSettings)
    {
        this.applicationSettings = applicationSettings;
        this.projectSettings = projectSettings;

        this.panelProject.setBorder(IdeBorderFactory.createTitledBorder(
                "Project Settings (" + projectSettings.getProject().getName() + ")"));
        this.panelApplication.setBorder(IdeBorderFactory.createTitledBorder("Application Settings"));
        this.panelExperimental.setBorder(IdeBorderFactory.createTitledBorder("Experimental Settings"));
        this.panelDebug.setBorder(IdeBorderFactory.createTitledBorder("Debugging Settings"));

        PlainDocument document = new PlainDocument();
        document.setDocumentFilter(new DocumentFilter()
        {
            @Override
            public void insertString(FilterBypass bypass, int offset, String text, AttributeSet attributes) throws BadLocationException
            {
                if (bypass.getDocument().getLength() + text.length() <= 128)
                    super.insertString(bypass, offset, text, attributes);
            }

            @Override
            public void replace(FilterBypass bypass, int offset, int length, String text, AttributeSet attributes)
                    throws BadLocationException
            {
                if (bypass.getDocument().getLength() - length + text.length() <= 128)
                    super.replace(bypass, offset, length, text, attributes);
            }
        });
        this.projectDescription.setDocument(document);

        this.applicationHideReadOnlyFiles.addItemListener(e -> this.updateButtons());
        this.applicationHideAfterPeriodOfInactivity.addItemListener(e -> this.updateButtons());
        this.applicationDebugLoggingEnabled.addItemListener(e -> this.updateButtons());
        this.applicationShowFiles.addItemListener(e -> updateButtons());

        this.applicationDebugLogFolder.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()));
        this.applicationDebugLogFolder.getTextField().getDocument().addDocumentListener(new DocumentAdapter()
        {
            @Override
            protected void textChanged(DocumentEvent e)
            {
                verifyLogFolder();
            }
        });

        this.buttonDumpCurrentState.addActionListener(e -> Debug.printDebugInfo(this.applicationDebugLogFolder.getText()));
        this.buttonOpenDebugLogFolder.addActionListener(e -> {
            try
            {
                if (verifyLogFolder())
                    Desktop.getDesktop().open(createFolder(applicationDebugLogFolder.getText()).toFile());
            }
            catch (Exception ex)
            {
                LOG.error("An error occurred while trying to open the debug log folder", ex);
            }
        });

        this.applicationThemeButton.addActionListener(e -> SwingUtilities.invokeLater(() -> new ThemeChooser(this).show()));
    }

    public boolean isModified()
    {
        // @formatter:off
        return verifyLogFolder() &&
                (this.projectEnabled.isSelected() != this.projectSettings.getState().isEnabled()
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
                        || this.applicationDebugLoggingEnabled.isSelected() != this.applicationSettings.getState().isDebugLoggingEnabled()
                        || !Objects.equals(this.applicationDebugLogFolder.getText(), this.applicationSettings.getState().getDebugLogFolder()))
                || this.applicationShowFiles.isSelected() != this.applicationSettings.getState().isShowFiles()
                || !Objects.equals(this.projectDescription.getText(), this.projectSettings.getState().getDescription())
                || this.applicationShowElapsedTime.isSelected() != this.applicationSettings.getState().isShowElapsedTime()
                || this.applicationForceBigIDEIcon.isSelected() != this.applicationSettings.getState().isForceBigIDEIcon()
                || !Objects.equals(this.applicationTheme, this.applicationSettings.getState().getTheme());
        // @formatter:on
    }

    public void apply()
    {
        this.projectSettings.getState().setEnabled(this.projectEnabled.isSelected());
        this.projectSettings.getState().setDescription(this.projectDescription.getText());
        this.applicationSettings.getState().setEnabled(this.applicationEnabled.isSelected());
        this.applicationSettings.getState().setShowUnknownImageIDE(this.applicationUnknownImageIDE.isSelected());
        this.applicationSettings.getState().setShowUnknownImageFile(this.applicationUnknownImageFile.isSelected());
        this.applicationSettings.getState().setShowFileExtensions(this.applicationShowFileExtensions.isSelected());
        this.applicationSettings.getState().setHideReadOnlyFiles(this.applicationHideReadOnlyFiles.isSelected());
        this.applicationSettings.getState().setShowReadingInsteadOfWriting(this.applicationShowReadingInsteadOfEditing.isSelected());
        this.applicationSettings
                .getState()
                .setShowIDEWhenNoProjectIsAvailable(this.applicationShowIDEWhenNoProjectIsAvailable.isSelected());
        this.applicationSettings.getState().setHideAfterPeriodOfInactivity(this.applicationHideAfterPeriodOfInactivity.isSelected());
        this.applicationSettings.getState().setInactivityTimeout((long) this.applicationInactivityTimeout.getValue(), TimeUnit.MINUTES);
        this.applicationSettings
                .getState()
                .setExperimentalWindowListenerEnabled(this.applicationExperimentalWindowListenerEnabled.isSelected());
        this.applicationSettings.getState().setResetOpenTimeAfterInactivity(this.applicationResetOpenTimeAfterInactivity.isSelected());
        this.applicationSettings.getState().setDebugLoggingEnabled(this.applicationDebugLoggingEnabled.isSelected());
        this.applicationSettings.getState().setShowFiles(this.applicationShowFiles.isSelected());
        this.applicationSettings.getState().setShowElapsedTime(this.applicationShowElapsedTime.isSelected());
        this.applicationSettings.getState().setForceBigIDEIcon(this.applicationForceBigIDEIcon.isSelected());
        this.applicationSettings.getState().setTheme(this.applicationTheme);

        if (verifyLogFolder())
            this.applicationSettings
                    .getState()
                    .setDebugLogFolder(createFolder(this.applicationDebugLogFolder.getText()).toAbsolutePath().toString());
    }

    public void reset()
    {
        System.out.println("RESET");

        this.projectEnabled.setSelected(this.projectSettings.getState().isEnabled());
        this.projectDescription.setText(this.projectSettings.getState().getDescription());
        this.applicationEnabled.setSelected(this.applicationSettings.getState().isEnabled());
        this.applicationUnknownImageIDE.setSelected(this.applicationSettings.getState().isShowUnknownImageIDE());
        this.applicationUnknownImageFile.setSelected(this.applicationSettings.getState().isShowUnknownImageFile());
        this.applicationShowFileExtensions.setSelected(this.applicationSettings.getState().isShowFileExtensions());
        this.applicationHideReadOnlyFiles.setSelected(this.applicationSettings.getState().isHideReadOnlyFiles());
        this.applicationShowReadingInsteadOfEditing.setSelected(this.applicationSettings.getState().isShowReadingInsteadOfWriting());
        this.applicationShowIDEWhenNoProjectIsAvailable.setSelected(this.applicationSettings
                .getState()
                .isShowIDEWhenNoProjectIsAvailable());
        this.applicationHideAfterPeriodOfInactivity.setSelected(this.applicationSettings.getState().isHideAfterPeriodOfInactivity());
        this.applicationInactivityTimeout.setValue(this.applicationSettings.getState().getInactivityTimeout(TimeUnit.MINUTES));
        this.applicationResetOpenTimeAfterInactivity.setSelected(this.applicationSettings.getState().isResetOpenTimeAfterInactivity());
        this.applicationExperimentalWindowListenerEnabled.setSelected(this.applicationSettings
                .getState()
                .isExperimentalWindowListenerEnabled());
        this.applicationDebugLoggingEnabled.setSelected(this.applicationSettings.getState().isDebugLoggingEnabled());
        this.applicationDebugLogFolder.setText(this.applicationSettings.getState().getDebugLogFolder());
        this.applicationShowFiles.setSelected(this.applicationSettings.getState().isShowFiles());
        this.applicationShowElapsedTime.setSelected(this.applicationSettings.getState().isShowElapsedTime());
        this.applicationForceBigIDEIcon.setSelected(this.applicationSettings.getState().isForceBigIDEIcon());
        this.setTheme(this.applicationSettings.getState().getTheme());

        this.updateButtons();
    }

    public void updateButtons()
    {
        this.applicationInactivityTimeoutLabel.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());
        this.applicationInactivityTimeout.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());
        this.applicationResetOpenTimeAfterInactivity.setEnabled(this.applicationHideAfterPeriodOfInactivity.isSelected());

        this.applicationUnknownImageFile.setEnabled(this.applicationShowFiles.isSelected());
        this.applicationShowFileExtensions.setEnabled(this.applicationShowFiles.isSelected());
        this.applicationHideReadOnlyFiles.setEnabled(this.applicationShowFiles.isSelected());

        this.applicationShowReadingInsteadOfEditing.setEnabled(
                this.applicationShowFiles.isSelected() && !this.applicationHideReadOnlyFiles.isSelected());

        this.applicationDebugLogFolder.setEnabled(this.applicationDebugLoggingEnabled.isSelected());

        this.verifyLogFolder();
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
            this.buttonDumpCurrentState.setEnabled(false);
            this.buttonOpenDebugLogFolder.setEnabled(false);

            return false;
        }

        if (Files.isRegularFile(path))
        {
            this.applicationDebugLogFolder.getTextField().setForeground(JBColor.RED);
            this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(new JBPopupMenu("Path is a file"));
            this.buttonDumpCurrentState.setEnabled(false);
            this.buttonOpenDebugLogFolder.setEnabled(false);

            return false;
        }

        if (!Files.isWritable(path))
        {
            this.applicationDebugLogFolder.getTextField().setForeground(JBColor.RED);
            this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(new JBPopupMenu("Cannot write to this path"));
            this.buttonOpenDebugLogFolder.setEnabled(false);
        }

        this.applicationDebugLogFolder.getTextField().setForeground(JBColor.foreground());
        this.applicationDebugLogFolder.getTextField().setComponentPopupMenu(null);
        this.buttonDumpCurrentState.setEnabled(applicationDebugLoggingEnabled.isSelected());
        this.buttonOpenDebugLogFolder.setEnabled(true);

        if (!Files.exists(path))
        {
            this.buttonDumpCurrentState.setEnabled(false);
            this.buttonOpenDebugLogFolder.setEnabled(false);
        }

        return true;
    }

    private Path createFolder(@NotNull String path)
    {
        return createFolder(Paths.get(path));
    }

    private Path createFolder(@NotNull Path path)
    {
        if (this.applicationDebugLoggingEnabled.isSelected() && verifyLogFolder() && !Files.isDirectory(path))
        {
            try
            {
                Files.createDirectories(path);
            }
            catch (IOException e)
            {
                LOG.warn("Could not create folder", e);
            }
        }

        return path;
    }

    @NotNull
    public JPanel getRootPanel()
    {
        return this.panelRoot;
    }

    private void createUIComponents()
    {
        Long timeoutValue = 1L;
        Long timeoutMin = 1L;
        Long timeoutMax = TimeUnit.MINUTES.convert(1, TimeUnit.DAYS);
        Long timeoutStepSize = 1L;

        this.applicationInactivityTimeout = new JSpinner(new SpinnerNumberModel(timeoutValue, timeoutMin, timeoutMax, timeoutStepSize));
    }

    @NotNull
    public SortedMap<String, Theme> getThemes()
    {
        return ThemeLoader.getInstance().getThemes();
    }

    @NotNull
    public Theme getThemeById(@NotNull String name)
    {
        return Optional.ofNullable(getThemes().get(name))
                .orElse(getThemes().get("Classic"));
    }

    public Theme getTheme()
    {
        return this.applicationTheme;
    }

    public void setTheme(@NotNull Theme theme)
    {
        this.applicationTheme = theme;

        this.applicationThemeLabel.setText("<html><b>" + theme.getName() + "</b></html>");
    }
}
