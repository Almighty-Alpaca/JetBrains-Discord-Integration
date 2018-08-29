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
package com.almightyalpaca.jetbrains.plugins.discord.components;

import com.almightyalpaca.jetbrains.plugins.discord.data.FileInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.ProjectInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProjectComponent implements com.intellij.openapi.components.ProjectComponent
{
    @NotNull
    private final ApplicationComponent applicationComponent;
    @NotNull
    private final Project project;
    @NotNull
    private final Map<VirtualFile, FileInfo> files;
    @Nullable
    private ProjectInfo projectInfo;

    public ProjectComponent(@NotNull Project project)
    {
        this.project = project;

        this.applicationComponent = ApplicationComponent.getInstance();
        this.files = new HashMap<>();
    }

    @Nullable
    @Contract(pure = true, value = "null -> null; _ -> _")
    public static ProjectComponent getInstance(@Nullable Project project)
    {
        return project != null ? project.getComponent(ProjectComponent.class) : null;
    }

    @NotNull
    public Map<VirtualFile, FileInfo> getFiles()
    {
        return Collections.unmodifiableMap(files);
    }

    @Override
    public synchronized void projectClosed()
    {
        this.applicationComponent.updateData(data -> data.projectRemove(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), this.getProjectInfo()));

        this.projectInfo = null;
        this.files.clear();
    }

    @Override
    public synchronized void projectOpened()
    {
        ProjectInfo projectInfo = new ProjectInfo(this.project);
        this.projectInfo = projectInfo;

        this.applicationComponent.updateData(data -> data.projectAdd(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), projectInfo));
    }

    public void fileUpdateTimeAccessed(@Nullable VirtualFile file)
    {
        if (this.getProjectInfo() != null && file != null && FileUtil.isAbsolute(file.getPath()))
        {
            if (!files.containsKey(file))
                this.applicationComponent.updateData(data -> data.fileAdd(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new)));

            this.applicationComponent.updateData(data -> data.fileUpdate(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new)));
        }
    }

    public void fileRemove(@Nullable VirtualFile file)
    {
        if (file != null)
            this.applicationComponent.updateData(data -> data.fileRemove(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), getProjectInfo(), this.files.remove(file)));
    }

    public void fileUpdateReadOnly(@Nullable VirtualFile file)
    {
        if (file != null && FileUtil.isAbsolute(file.getPath()))
            this.applicationComponent.updateData(data -> data.fileSetReadOnly(System.currentTimeMillis(), this.applicationComponent.getInstanceInfo(), getProjectInfo(), this.files.computeIfAbsent(file, FileInfo::new), !file.isWritable()));
    }

    public void updateTimeAccessed(VirtualFile file)
    {
        if (file == null)
            this.updateTimeAccessed();
        else
            this.fileUpdateTimeAccessed(file);
    }

    public void updateTimeAccessed()
    {
        this.applicationComponent.updateData(data -> data.projectUpdate(System.currentTimeMillis(), applicationComponent.getInstanceInfo(), getProjectInfo()));
    }

    @Nullable
    public ProjectInfo getProjectInfo()
    {
        return this.projectInfo;
    }

    @Override
    public void initComponent() {}

    @Override
    public void disposeComponent() {}

    @NotNull
    @Override
    public String getComponentName()
    {
        return ApplicationComponent.class.getSimpleName();
    }

    public void fileUpdateName(VirtualFile file)
    {
        this.fileRemove(file);
        this.fileUpdateTimeAccessed(file);
    }
}
