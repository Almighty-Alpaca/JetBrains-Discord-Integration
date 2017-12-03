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
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DiscordIntegrationProjectComponent implements ProjectComponent, FileEditorManagerListener
{
    private final DiscordIntegrationApplicationService service = ServiceManager.getService(DiscordIntegrationApplicationService.class);
    private final Project project;
    private ProjectInfo projectInfo;
    private final Map<VirtualFile, FileInfo> files;

    private MessageBusConnection bus;
    private long time = 0;

    public DiscordIntegrationProjectComponent(Project project)
    {
        this.project = project;

        this.files = new HashMap<>();
    }

    @Override
    public void projectClosed()
    {
        this.bus.disconnect();
        this.bus = null;

        this.service.getData().removeProject(this.service.getInstanceInfo(), this.projectInfo);

        this.time = 0;
        this.projectInfo = null;
    }

    @Override
    public void projectOpened()
    {
        this.time = System.currentTimeMillis();
        this.projectInfo = new ProjectInfo(this.project, this.time);

        this.bus = project.getMessageBus().connect();
        this.bus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        this.service.getData().addProject(this.service.getInstanceInfo(), this.projectInfo);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        FileInfo fileInfo = new FileInfo(file);

        this.files.put(file, fileInfo);

        this.service.getData().addFile(service.getInstanceInfo(), projectInfo, fileInfo);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        this.service.getData().removeFile(service.getInstanceInfo(), projectInfo, this.files.remove(file));
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event)
    {
        VirtualFile file = event.getNewFile();

        if (file != null)
            this.service.getData().addFile(service.getInstanceInfo(), projectInfo, this.files.get(file));
    }
}
