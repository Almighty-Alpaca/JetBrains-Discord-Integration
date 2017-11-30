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
    private final int id;

    @Override
    public String toString()
    {
        return "InstanceInfo{" + "projects=" + projects + ", id=" + id + ", distributionCode='" + distributionCode + '\'' + ", distributionVersion='" + distributionVersion + '\'' + '}';
    }

    @NotNull
    private final String distributionCode;
    @NotNull
    private final String distributionVersion;

    public InstanceInfo(int id, @NotNull String distributionCode, @NotNull String distributionVersion)
    {
        this(id, distributionCode, distributionVersion, new UniqueLinkedDeque<>());
    }

    public InstanceInfo(int id, @NotNull String distributionCode, @NotNull String distributionVersion, @NotNull UniqueDeque<ProjectInfo> projects)
    {
        this.id = id;
        this.distributionCode = distributionCode;
        this.distributionVersion = distributionVersion;
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

    public int getId()
    {
        return id;
    }

    @NotNull
    public String getDistributionCode()
    {
        return distributionCode;
    }

    @NotNull
    public String getDistributionVersion()
    {
        return distributionVersion;
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
        return new InstanceInfo(id, distributionCode, distributionVersion, projects.stream().map(ProjectInfo::clone).collect(Collectors.toCollection(UniqueLinkedDeque::new)));
    }
}
