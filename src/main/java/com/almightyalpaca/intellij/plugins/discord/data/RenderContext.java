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

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Comparator;

public class RenderContext implements Serializable, ReallyCloneable<RenderContext>
{
    @Nullable
    private final InstanceInfo instance;
    @Nullable
    private final ProjectInfo project;
    @Nullable
    private final FileInfo file;

    public RenderContext(@Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file)
    {
        this.instance = instance;
        this.project = project;
        this.file = file;
    }

    public RenderContext(@NotNull ReplicatedData data)
    {
        // @formatter:off
        this.instance = data.getInstances().values().stream()
                .filter(i -> i.getSettings().isEnabled())
                .max(Comparator.naturalOrder())
                .orElse(null);
        if (this.instance != null)
        {
            this.project = this.instance.getProjects().values().stream()
                    .filter(p -> p.getSettings().isEnabled())
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            if (this.project != null)
                this.file = this.project.getFiles().values().stream()
                        .filter(f -> !(instance.getSettings().isHideReadOnlyFiles() && f.isReadOnly()))
                        .max(Comparator.naturalOrder())
                        .orElse(null);
            else
                this.file = null;
        }
        else
        {
            this.project = null;
            this.file = null;
        }
        // @formatter:on
    }

    @Nullable
    public InstanceInfo getInstance()
    {
        return instance;
    }

    @Nullable
    public ProjectInfo getProject()
    {
        return project;
    }

    @Nullable
    public FileInfo getFile()
    {
        return file;
    }

    @NotNull
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public RenderContext clone()
    {
        return new RenderContext(ReallyCloneable.clone(instance), ReallyCloneable.clone(project), ReallyCloneable.clone(file));
    }
}
