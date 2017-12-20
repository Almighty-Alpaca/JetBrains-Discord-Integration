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
import kotlin.Pair;
import kotlin.Triple;
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
        Triple<InstanceInfo, ProjectInfo,FileInfo> triple = data.instances.values().stream()
                .flatMap(i -> i.getProjects().values().stream()
                    .map(p-> new Pair<>(i, p))
                    .filter(p -> p.getFirst().getSettings().isEnabled() && p.getSecond().getSettings().isEnabled()))
                    .flatMap(p -> p.getSecond().getFiles().values().stream()
                            .map(f-> new Triple<>(p.getFirst(),p.getSecond(),f)))
                .filter(t -> !t.getFirst().getSettings().isHideReadOnlyFiles() || !t.getThird().isReadOnly())
                .max(Comparator.comparing(Triple::getSecond))
                .orElse(new Triple<>(null,null,null));
        // @formatter:on

        this.instance = triple.getFirst();
        this.project = triple.getSecond();
        this.file = triple.getThird();
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
