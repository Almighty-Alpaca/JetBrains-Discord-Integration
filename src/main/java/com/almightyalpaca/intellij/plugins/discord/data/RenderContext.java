package com.almightyalpaca.intellij.plugins.discord.data;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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
        Triple<InstanceInfo, ProjectInfo,FileInfo> triple = data.instances.values().stream()
                .flatMap(i -> i.getProjects().values().stream()
                    .map(p-> Pair.of(i, p))
                    .filter(p -> p.getLeft().getSettings().isEnabled() && p.getRight().getSettings().isEnabled()))
                    .flatMap(p -> p.getRight().getFiles().values().stream().map(f-> Triple.of(p.getLeft(),p.getRight(),f)))
                .max(Comparator.comparing(Triple::getRight))
                .orElse(Triple.of(null,null,null));

        this.instance = triple.getLeft();
        this.project = triple.getMiddle();
        this.file = triple.getRight();
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

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public RenderContext clone()
    {
        return new RenderContext(ReallyCloneable.clone(instance), ReallyCloneable.clone(project), ReallyCloneable.clone(file));
    }
}
