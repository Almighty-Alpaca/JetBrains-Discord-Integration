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
import com.almightyalpaca.intellij.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationApplicationService;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DiscordIntegrationProjectComponent extends VirtualFileAdapter implements ProjectComponent, FileEditorManagerListener
{
    @NotNull
    private static final Map<Project, DiscordIntegrationProjectComponent> INSTANCES = new HashMap<>();

    @NotNull
    private final DiscordIntegrationApplicationService applicationService;
    @NotNull
    private final Project project;
    @NotNull
    private final Map<VirtualFile, FileInfo> files;
    @Nullable
    private MessageBusConnection bus;
    @Nullable
    private ProjectInfo projectInfo;

    public DiscordIntegrationProjectComponent(@NotNull Project project)
    {
        this.project = project;
        this.applicationService = DiscordIntegrationApplicationService.getInstance();

        this.files = new HashMap<>();

        DiscordIntegrationProjectComponent.INSTANCES.put(project, this);
    }

    @Nullable
    public static DiscordIntegrationProjectComponent getInstance(Project project)
    {
        return DiscordIntegrationProjectComponent.INSTANCES.get(project);
    }

    @Override
    public synchronized void projectClosed()
    {
        if (bus != null)
        {
            this.bus.disconnect();
            this.bus = null;
        }

        if (this.getProjectInfo() != null)
            this.applicationService.getData().projectRemove(this.applicationService.getInstanceInfo(), this.getProjectInfo());

        this.projectInfo = null;
        files.clear();
    }

    @Override
    public synchronized void projectOpened()
    {
        this.bus = project.getMessageBus().connect();
        this.bus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        ProjectInfo projectInfo = new ProjectInfo(project);
        this.projectInfo = projectInfo;

        this.applicationService.getData().projectAdd(this.applicationService.getInstanceInfo(), projectInfo);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        this.fileOpened(file);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        this.fileClosed(file);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event)
    {
        VirtualFile file = event.getNewFile();

        if (file != null && this.getProjectInfo() != null)
            this.applicationService.getData().fileSetTimeAccessed(applicationService.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new), System.currentTimeMillis());
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event)
    {
        if ((event.getPropertyName().equals(VirtualFile.PROP_NAME)) && files.containsKey(event.getFile()))
        {
            fileClosed(event.getFile());
            fileOpened(event.getFile());
        }
        else if ((event.getPropertyName().equals(VirtualFile.PROP_WRITABLE)) && files.containsKey(event.getFile()))
        {
            fileUpdateReadOnly(event.getFile());
        }
    }

    public void fileOpened(@NotNull VirtualFile file)
    {
        if (this.getProjectInfo() != null)
            this.applicationService.getData().fileAdd(applicationService.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new));
    }

    public void fileClosed(@NotNull VirtualFile file)
    {
        if (this.getProjectInfo() != null)
            this.applicationService.getData().fileRemove(applicationService.getInstanceInfo(), getProjectInfo(), this.files.remove(file));
    }

    public void fileUpdateReadOnly(@NotNull VirtualFile file)
    {
        if (this.getProjectInfo() != null)
            this.applicationService.getData().fileSetReadOnly(applicationService.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new), !file.isWritable());
    }

    @Nullable
    public ProjectInfo getProjectInfo()
    {
        return projectInfo;
    }

    @Override
    public void initComponent()
    {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
    }

    @Override
    public void disposeComponent()
    {
        VirtualFileManager.getInstance().removeVirtualFileListener(this);
        DiscordIntegrationProjectComponent.INSTANCES.remove(project);
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return DiscordIntegrationApplicationComponent.class.getSimpleName();
    }
}
