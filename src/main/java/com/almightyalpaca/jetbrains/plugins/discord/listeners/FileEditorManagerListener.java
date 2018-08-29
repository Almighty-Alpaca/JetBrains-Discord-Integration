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
package com.almightyalpaca.jetbrains.plugins.discord.listeners;


import com.almightyalpaca.jetbrains.plugins.discord.components.ProjectComponent;
import com.almightyalpaca.jetbrains.plugins.discord.debug.Logger;
import com.almightyalpaca.jetbrains.plugins.discord.debug.LoggerFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class FileEditorManagerListener implements com.intellij.openapi.fileEditor.FileEditorManagerListener
{
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(FileEditorManagerListener.class);

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        LOG.trace("FileEditorManagerListener#fileOpened({}, {})", source, file);

        Project project = source.getProject();
        ProjectComponent component = ProjectComponent.getInstance(project);

        if (component != null)
            component.fileUpdateTimeAccessed(file);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file)
    {
        LOG.trace("FileEditorManagerListener#fileClosed({}, {})", source, file);

        Project project = source.getProject();
        ProjectComponent component = ProjectComponent.getInstance(project);

        if (component != null)
            component.fileRemove(file);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event)
    {
        LOG.trace("FileEditorManagerListener#selectionChanged({})", event);

        Project project = event.getManager().getProject();
        ProjectComponent component = ProjectComponent.getInstance(project);

        if (component != null)
            component.fileUpdateTimeAccessed(event.getNewFile());
    }
}
