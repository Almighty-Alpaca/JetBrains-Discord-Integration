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

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

@Immutable
public class FileInfo implements Serializable, Cloneable
{
    @NotNull
    private final String name;
    @Nullable
    private final String extension;

    public FileInfo(@NotNull VirtualFile file)
    {
        this(file.getNameWithoutExtension(), file.getExtension());
    }

    public FileInfo(@NotNull String name, @Nullable String extension)
    {
        this.name = name;
        this.extension = extension;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @Nullable
    public String getExtension()
    {
        return extension;
    }

    @NotNull
    public String getNameWithExtension()
    {
        return name + '.' + extension;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof FileInfo && name.equals(((FileInfo) o).name) && Objects.equals(extension, ((FileInfo) o).extension);
    }

    @Override
    public String toString()
    {
        return "FileInfo{" + "name='" + name + '\'' + ", extension='" + extension + '\'' + '}';
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected FileInfo clone()
    {
        return this;
    }
}
