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

import com.almightyalpaca.jetbrains.plugins.discord.utils.FileUtil;
import com.google.gson.Gson;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class FileInfo implements Serializable, Comparable<FileInfo>
{
    @NotNull
    private static final Gson GSON = new Gson();

    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private String firstLine;
    private boolean readOnly;
    private long timeAccessed;
    private long timeOpened;

    public FileInfo(@NotNull VirtualFile file)
    {
        this(file.getPath(), file.getName(), FileUtil.readFirstLine(file), !file.isWritable(), System.currentTimeMillis());
    }

    public FileInfo(@NotNull String id, @NotNull String name, @NotNull String firstLine, boolean readOnly, long timeOpened)
    {
        this(id, name, firstLine, readOnly, timeOpened, timeOpened);
    }

    public FileInfo(@NotNull String id, @NotNull String name, @NotNull String firstLine, boolean readOnly, long timeOpened, long timeAccessed)
    {
        this.id = id;
        this.name = name;
        this.firstLine = firstLine;
        this.readOnly = readOnly;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;
    }

    @NotNull
    public String getId()
    {
        return this.id;
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
    public String getFirstLine()
    {
        return firstLine;
    }

    void setFirstLine(@NotNull String firstLine)
    {
        this.firstLine = firstLine;
    }

    public boolean isReadOnly()
    {
        return this.readOnly;
    }

    void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @NotNull
    public String getBaseName()
    {
        return FilenameUtils.getBaseName(this.name);
    }

    @Nullable
    public String getExtension()
    {
        return FilenameUtils.getExtension(this.name);
    }

    @Override
    public int compareTo(@NotNull FileInfo file)
    {
        return Long.compare(this.timeAccessed, file.timeAccessed);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof FileInfo))
            return false;
        FileInfo fileInfo = (FileInfo) o;
        return isReadOnly() == fileInfo.isReadOnly() &&
               getTimeAccessed() == fileInfo.getTimeAccessed() &&
               getTimeOpened() == fileInfo.getTimeOpened() &&
               Objects.equals(getId(), fileInfo.getId()) &&
               Objects.equals(getName(), fileInfo.getName()) &&
               Objects.equals(getFirstLine(), fileInfo.getFirstLine());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getId(), getName(), getFirstLine(), isReadOnly(), getTimeAccessed(), getTimeOpened());
    }

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }
}
