package com.almightyalpaca.jetbrains.plugins.discord.actions;

import com.almightyalpaca.jetbrains.plugins.discord.components.DiscordIntegrationApplicationComponent;
import com.almightyalpaca.jetbrains.plugins.discord.components.DiscordIntegrationProjectComponent;
import com.almightyalpaca.jetbrains.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.jetbrains.plugins.discord.settings.DiscordIntegrationProjectConfigurable;
import com.almightyalpaca.jetbrains.plugins.discord.settings.DiscordIntegrationProjectSettings;
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

        DiscordIntegrationApplicationComponent applicationComponent = DiscordIntegrationApplicationComponent.getInstance();
        DiscordIntegrationProjectComponent projectComponent = DiscordIntegrationProjectComponent.getInstance(project);

        InstanceInfo instanceInfo = applicationComponent.getInstanceInfo();

        if (projectComponent != null)
        {
            ProjectInfo projectInfo = projectComponent.getProjectInfo();

            ProjectSettingsStorage settings = DiscordIntegrationProjectSettings.getInstance(project).getState();
            settings.setEnabled(!settings.isEnabled());

            applicationComponent.updateData(data -> data.projectSetSettings(System.currentTimeMillis(), instanceInfo, projectInfo, settings));

            update(e);

            DiscordIntegrationProjectConfigurable.getInstance(project).reset();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e)
    {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        if (project == null)
            return;

        configurePresentation(e.getPresentation(), DiscordIntegrationProjectSettings.getInstance(project).getSettings().isEnabled());
    }
}
