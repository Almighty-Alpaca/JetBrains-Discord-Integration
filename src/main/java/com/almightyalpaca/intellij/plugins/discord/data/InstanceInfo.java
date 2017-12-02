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
import com.intellij.openapi.application.ApplicationInfo;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;

public class InstanceInfo implements Serializable, Cloneable
{
    @NotNull
    final UniqueDeque<ProjectInfo> projects;
    @NotNull
    private final DistributionInfo distribution;
    private final int id;

    public InstanceInfo(int id, @NotNull DistributionInfo distribution)
    {
        this(id, distribution, new UniqueLinkedDeque<>());
    }

    public InstanceInfo(int id, @NotNull String distributionCode, @NotNull String distributionVersion)
    {
        this(id, distributionCode, distributionVersion, new UniqueLinkedDeque<>());
    }

    public InstanceInfo(int id, @NotNull String distributionCode, @NotNull String distributionVersion, @NotNull UniqueDeque<ProjectInfo> projects)
    {
        this(id, new DistributionInfo(distributionCode, distributionVersion), projects);
    }

    public InstanceInfo(int id, @NotNull DistributionInfo distribution, @NotNull UniqueDeque<ProjectInfo> projects)
    {
        this.id = id;
        this.distribution = distribution;
        this.projects = projects;
    }

    public InstanceInfo(int id, @NotNull ApplicationInfo info)
    {
        this(id, info, new UniqueLinkedDeque<>());
    }

    public InstanceInfo(int id, @NotNull ApplicationInfo info, @NotNull UniqueDeque<ProjectInfo> projects)
    {
        this(id, info.getBuild().getProductCode(), info.getFullVersion(), projects);
    }

    @Override
    public String toString()
    {
        return "InstanceInfo{" + "distribution=" + distribution + ", projects=" + projects + ", id=" + id + '}';
    }

    public int getId()
    {
        return id;
    }

    @NotNull
    public DistributionInfo getDistribution()
    {
        return distribution;
    }

    @NotNull
    public UniqueDeque<ProjectInfo> getProjects()
    {
        return new UniqueLinkedDeque<>(projects);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof InstanceInfo && Objects.equals(id, ((InstanceInfo) obj).id);
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected InstanceInfo clone()
    {
        return new InstanceInfo(id, distribution, projects.stream().map(ProjectInfo::clone).collect(Collectors.toCollection(UniqueLinkedDeque::new)));
    }
}
