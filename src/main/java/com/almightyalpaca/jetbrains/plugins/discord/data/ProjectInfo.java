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
package com.almightyalpaca.jetbrains.plugins.discord.data;

import com.almightyalpaca.jetbrains.plugins.discord.settings.ProjectSettings;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectInfo implements Serializable, Comparable<ProjectInfo>
{
    @NotNull
    private static final Gson GSON = new Gson();

    @NotNull
    private final String name;
    @NotNull
    private final String id;
    @NotNull
    private final Map<String, FileInfo> files;
    @NotNull
    private com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings settings;
    private long timeAccessed;
    private long timeOpened;

    public ProjectInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings settings, @NotNull String name, long timeOpened)
    {
        this(id, settings, name, timeOpened, timeOpened);
    }

    public ProjectInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings settings, @NotNull String name, long timeOpened, long timeAccessed)
    {
        this(id, settings, name, timeOpened, timeAccessed, new HashMap<>());
    }

    public ProjectInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings settings, @NotNull String name, long timeOpened, long timeAccessed, @NotNull Map<String, FileInfo> files)
    {
        this.settings = settings;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;
        this.name = name;
        this.id = id;
        this.files = Collections.synchronizedMap(new HashMap<>(files));
    }

    public ProjectInfo(@NotNull Project project)
    {
        this(project.getLocationHash(), ProjectSettings.getInstance(project).getSettings(), project.getName(), System.currentTimeMillis());
    }

    public long getTimeOpened()
    {
        return this.timeOpened;
    }

    void setTimeOpened(long timeOpened)
    {
        this.timeOpened = timeOpened;

        if (timeOpened > this.timeAccessed)
            this.timeAccessed = timeOpened;

        this.files.values().forEach(f -> f.setTimeOpened(timeOpened));
    }

    public long getTimeAccessed()
    {
        return this.timeAccessed;
    }

    void setTimeAccessed(long timeAccessed)
    {
        this.timeAccessed = timeAccessed;
    }

    @NotNull
    public String getName()
    {
        return this.name;
    }

    @NotNull
    public String getId()
    {
        return this.id;
    }

    @NotNull
    public com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings getSettings()
    {
        return this.settings;
    }

    void setSettings(@NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings settings)
    {
        this.settings = settings;
    }

    @NotNull
    public Map<String, FileInfo> getFiles()
    {
        return new HashMap<>(this.files);
    }

    @Override
    public int compareTo(@NotNull ProjectInfo project)
    {
        return Long.compare(this.timeAccessed, project.timeAccessed);
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        return obj instanceof ProjectInfo && Objects.equals(this.id, ((ProjectInfo) obj).id);
    }

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }

    void addFile(@NotNull FileInfo file)
    {
        this.files.put(file.getId(), file);
    }

    void removeFile(@NotNull String fileId)
    {
        this.files.remove(fileId);
    }
}
