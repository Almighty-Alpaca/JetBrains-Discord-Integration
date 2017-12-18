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
package com.almightyalpaca.intellij.plugins.discord.data;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableCollections;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableHashMap;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableMap;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import com.almightyalpaca.intellij.plugins.discord.settings.DiscordIntegrationProjectSettings;
import com.almightyalpaca.intellij.plugins.discord.settings.data.ProjectSettings;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class ProjectInfo implements Serializable, ReallyCloneable<ProjectInfo>, Comparable<ProjectInfo>
{
    private final long time;
    @NotNull
    private final String name;
    private final String id;
    @NotNull
    private final CloneableMap<String, FileInfo> files;
    @NotNull
    private ProjectSettings<? extends ProjectSettings> settings;

    public ProjectInfo(String id, @NotNull ProjectSettings<? extends ProjectSettings> settings, @NotNull String name, long time)
    {
        this(id, settings, name, time, new CloneableHashMap<>());
    }

    public ProjectInfo(String id, @NotNull ProjectSettings<? extends ProjectSettings> settings, @NotNull String name, long time, @NotNull CloneableMap<String, FileInfo> files)
    {
        this.settings = settings;
        this.time = time;
        this.name = name;
        this.id = id;
        this.files = files;
    }

    public ProjectInfo(@NotNull Project project)
    {
        this(project.getLocationHash(), DiscordIntegrationProjectSettings.getInstance(project).getSettings(), project.getName(), System.currentTimeMillis());
    }

    public long getTime()
    {
        return time;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getId()
    {
        return id;
    }

    @NotNull
    public ProjectSettings<? extends ProjectSettings> getSettings()
    {
        return settings;
    }

    void setSettings(@NotNull ProjectSettings<? extends ProjectSettings> settings)
    {
        this.settings = settings;
    }

    @NotNull
    public CloneableMap<String, FileInfo> getFiles()
    {
        return CloneableCollections.unmodifiableCloneableMap(files);
    }

    @Override
    public int compareTo(@NotNull ProjectInfo project)
    {
        return Objects.compare(getNewestFile(), project.getNewestFile(), Comparator.naturalOrder());
    }

    @Nullable
    public FileInfo getNewestFile()
    {
        return this.files.values().stream().max(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        return obj instanceof ProjectInfo && Objects.equals(id, ((ProjectInfo) obj).id);
    }

    @Override
    public String toString()
    {
        return "ProjectInfo{" + "time=" + time + ", name='" + name + '\'' + ", id='" + id + '\'' + ", files=" + files + ", settings=" + settings + '}';
    }

    @NotNull
    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public ProjectInfo clone()
    {
        ProjectSettings<? extends ProjectSettings> s = settings.clone();
        return new ProjectInfo(id, s, name, time, files.clone());
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
