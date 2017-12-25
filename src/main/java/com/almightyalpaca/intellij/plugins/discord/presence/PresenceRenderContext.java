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
package com.almightyalpaca.intellij.plugins.discord.presence;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import com.almightyalpaca.intellij.plugins.discord.data.FileInfo;
import com.almightyalpaca.intellij.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.intellij.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.intellij.plugins.discord.data.ReplicatedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Comparator;

public class PresenceRenderContext implements Serializable, ReallyCloneable<PresenceRenderContext>
{
    @Nullable
    private final InstanceInfo instance;
    @Nullable
    private final ProjectInfo project;
    @Nullable
    private final FileInfo file;

    public PresenceRenderContext(@Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file)
    {
        this.instance = instance;
        this.project = project;
        this.file = file;
    }

    public PresenceRenderContext(@NotNull ReplicatedData data)
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
                        .filter(f -> !(this.instance.getSettings().isHideReadOnlyFiles() && f.isReadOnly()))
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
        return this.instance;
    }

    @Nullable
    public ProjectInfo getProject()
    {
        return this.project;
    }

    @Nullable
    public FileInfo getFile()
    {
        return this.file;
    }

    @NotNull
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public PresenceRenderContext clone()
    {
        // @formatter:off
        return new PresenceRenderContext(ReallyCloneable.clone(this.instance),
                                         ReallyCloneable.clone(this.project),
                                         ReallyCloneable.clone(this.file));
        // @formatter:on
    }
}
