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
package com.almightyalpaca.intellij.plugins.discord.components;

import com.almightyalpaca.intellij.plugins.discord.data.FileInfo;
import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationApplicationService;
import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationProjectService;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DiscordIntegrationProjectComponent implements ProjectComponent, FileEditorManagerListener
{
    @NotNull
    private final DiscordIntegrationProjectService projectService;
    @NotNull
    private final DiscordIntegrationApplicationService applicationService;
    @NotNull
    private final Project project;
    @NotNull
    private final Map<VirtualFile, FileInfo> files;
    @Nullable
    private MessageBusConnection bus;
    private long time = 0;

    public DiscordIntegrationProjectComponent(@NotNull Project project)
    {
        this.project = project;
        this.projectService = DiscordIntegrationProjectService.getInstance(project);
        this.applicationService = DiscordIntegrationApplicationService.getInstance();

        this.files = new HashMap<>();
    }

    @Override
    public void projectClosed()
    {
        if (bus != null)
        {
            this.bus.disconnect();
            this.bus = null;
        }

        if (this.projectService.getProjectInfo() != null)
            this.applicationService.getData().projectRemove(this.applicationService.getInstanceInfo(), this.projectService.getProjectInfo());

        this.time = 0;
    }

    @Override
    public void projectOpened()
    {
        this.time = System.currentTimeMillis();

        this.bus = project.getMessageBus().connect();
        this.bus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        this.applicationService.getData().projectAdd(this.applicationService.getInstanceInfo(), this.projectService.getProjectInfo());
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        if (this.projectService.getProjectInfo() != null)
            this.applicationService.getData().fileAdd(applicationService.getInstanceInfo(), projectService.getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new));
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        if (this.projectService.getProjectInfo() != null)
            this.applicationService.getData().fileRemove(applicationService.getInstanceInfo(), projectService.getProjectInfo(), this.files.remove(file));
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event)
    {
        VirtualFile file = event.getNewFile();

        if (file != null && this.projectService.getProjectInfo() != null)
            this.applicationService.getData().fileSetTimeAccessed(applicationService.getInstanceInfo(), projectService.getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new), System.currentTimeMillis());
    }

    @Override
    public void initComponent() {}

    @Override
    public void disposeComponent() {}

    @NotNull
    @Override
    public String getComponentName()
    {
        return DiscordIntegrationApplicationComponent.class.getSimpleName();
    }
}
