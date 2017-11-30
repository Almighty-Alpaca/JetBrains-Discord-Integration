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

import com.almightyalpaca.intellij.plugins.discord.collections.UniqueDeque;
import com.almightyalpaca.intellij.plugins.discord.collections.UniqueLinkedDeque;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectInfo implements Serializable, Cloneable
{
    final long time;
    @NotNull
    final String name;
    @NotNull
    final String id;
    @NotNull
    final UniqueDeque<FileInfo> files;

    public ProjectInfo(@NotNull String name, @NotNull String id, long time)
    {
        this(name, id, time, new UniqueLinkedDeque<>());
    }

    public ProjectInfo(@NotNull String name, @NotNull String id, long time, @NotNull UniqueDeque<FileInfo> files)
    {
        this.name = name;
        this.id = id;
        this.time = time;
        this.files = files;
    }

    public ProjectInfo(@NotNull Project project, long time)
    {
        this(project, time, new UniqueLinkedDeque<>());
    }

    public ProjectInfo(@NotNull Project project, long time, @NotNull UniqueDeque<FileInfo> files)
    {
        this(project.getName(), project.getLocationHash(), time, files);
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
    public UniqueDeque<FileInfo> getFiles()
    {
        return new UniqueLinkedDeque<>(files);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof ProjectInfo && Objects.equals(id, ((ProjectInfo) obj).id);
    }

    @Override
    public String toString()
    {
        return "ProjectInfo{" + "time=" + time + ", name='" + name + '\'' + ", id='" + id + '\'' + ", files=" + files + '}';
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected ProjectInfo clone()
    {
        return new ProjectInfo(name, id, time, files.stream().map(FileInfo::clone).collect(Collectors.toCollection(UniqueLinkedDeque::new)));
    }
}
