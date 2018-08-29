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
package com.almightyalpaca.jetbrains.plugins.discord.actions;

import com.almightyalpaca.jetbrains.plugins.discord.components.ApplicationComponent;
import com.almightyalpaca.jetbrains.plugins.discord.components.ProjectComponent;
import com.almightyalpaca.jetbrains.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.jetbrains.plugins.discord.settings.ProjectConfigurable;
import com.almightyalpaca.jetbrains.plugins.discord.settings.ProjectSettings;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage.ProjectSettingsStorage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ToolsMenuDisableAnAction extends AnAction
{
    @NotNull
    public static final Icon ICON_ENABLED = IconLoader.getIcon("/icons/discord/logo/blurple.png");
    @NotNull
    public static final Icon ICON_DISABLED = IconLoader.getIcon("/icons/discord/logo/white.png");

    @NotNull
    public static final String TEXT_ENABLED = "Disable Rich Presence for this project";
    @NotNull
    public static final String TEXT_DISABLED = "Enable Rich Presence for this project";

    @NotNull
    public static final String DESCRIPTION_ENABLED = "Disable Discord Rich Presence Integration for this project";
    @NotNull
    public static final String DESCRIPTION_DISABLED = "Enable Discord Rich Presence integration for this project";

    public ToolsMenuDisableAnAction()
    {
        configurePresentation(getTemplatePresentation(), true);
    }

    @SuppressWarnings("Duplicates")
    private void configurePresentation(@NotNull Presentation presentation, boolean enabled)
    {
        if (enabled)
        {
            presentation.setText(TEXT_ENABLED);
            presentation.setDescription(DESCRIPTION_ENABLED);
            presentation.setIcon(ICON_ENABLED);
            presentation.setHoveredIcon(ICON_DISABLED);
            presentation.setSelectedIcon(ICON_DISABLED);
        }
        else
        {
            presentation.setText(TEXT_DISABLED);
            presentation.setDescription(DESCRIPTION_DISABLED);
            presentation.setIcon(ICON_DISABLED);
            presentation.setHoveredIcon(ICON_ENABLED);
            presentation.setSelectedIcon(ICON_ENABLED);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        if (project == null)
            return;

        ApplicationComponent applicationComponent = ApplicationComponent.getInstance();
        ProjectComponent projectComponent = ProjectComponent.getInstance(project);

        InstanceInfo instanceInfo = applicationComponent.getInstanceInfo();

        if (projectComponent != null)
        {
            ProjectInfo projectInfo = projectComponent.getProjectInfo();

            ProjectSettingsStorage settings = ProjectSettings.getInstance(project).getState();
            settings.setEnabled(!settings.isEnabled());

            applicationComponent.updateData(data -> data.projectSetSettings(System.currentTimeMillis(), instanceInfo, projectInfo, settings));

            update(e);

            ProjectConfigurable.getInstance(project).reset();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e)
    {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        if (project == null)
            return;

        configurePresentation(e.getPresentation(), ProjectSettings.getInstance(project).getSettings().isEnabled());
    }
}
