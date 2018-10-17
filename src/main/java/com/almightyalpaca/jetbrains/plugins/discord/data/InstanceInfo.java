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

import com.almightyalpaca.jetbrains.plugins.discord.settings.ApplicationSettings;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstanceInfo implements Serializable, Comparable<InstanceInfo>
{
    @NotNull
    private static final Gson GSON = new Gson();

    @NotNull
    private final Map<String, ProjectInfo> projects;
    @NotNull
    private final DistributionInfo distribution;
    @NotNull
    private final String id;
    @NotNull
    private com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings;
    private long timeAccessed;
    private long timeOpened;
    @Nullable
    private String connectedApplication = null;

    public InstanceInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings, @NotNull String distributionCode, @NotNull String distributionVersion, long timeOpened)
    {
        this(id, settings, distributionCode, distributionVersion, timeOpened, timeOpened);
    }

    public InstanceInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings, @NotNull String distributionCode, @NotNull String distributionVersion, long timeOpened, long timeAccessed)
    {
        this(id, settings, new DistributionInfo(distributionCode, distributionVersion), timeOpened, timeAccessed);
    }

    public InstanceInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings, @NotNull DistributionInfo distribution, long timeOpened, long timeAccessed)
    {
        this(id, settings, distribution, timeOpened, timeAccessed, new HashMap<>());
    }

    public InstanceInfo(@NotNull String id, @NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings, @NotNull DistributionInfo distribution, long timeOpened, long timeAccessed, @NotNull Map<String, ProjectInfo> projects)
    {
        this.id = id;
        this.settings = settings;
        this.distribution = distribution;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;
        this.projects = Collections.synchronizedMap(new HashMap<>(projects));
    }

    public InstanceInfo(@NotNull String id, @NotNull ApplicationInfo info)
    {
        this(id, ApplicationSettings.getInstance().getSettings(), info
                .getBuild()
                .getProductCode(), info.getFullVersion(), System.currentTimeMillis());
    }

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
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

        this.projects.values().forEach(p -> p.setTimeOpened(timeOpened));
    }

    public long getTimeAccessed()
    {
        return this.timeAccessed;
    }

    void setTimeAccessed(long timeAccessed)
    {
        this.timeAccessed = timeAccessed;
    }

    @Nullable
    public String getConnectedApplication()
    {
        return this.connectedApplication;
    }

    void setConnectedApplication(@Nullable String connectedApplication)
    {
        this.connectedApplication = connectedApplication;
    }

    @NotNull
    public com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings getSettings()
    {
        return this.settings;
    }

    void setSettings(@NotNull com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings settings)
    {
        this.settings = settings;
    }

    @NotNull
    public String getId()
    {
        return this.id;
    }

    @NotNull
    public DistributionInfo getDistribution()
    {
        return this.distribution;
    }

    @NotNull
    public Map<String, ProjectInfo> getProjects()
    {
        return new HashMap<>(this.projects);
    }

    @Override
    public int compareTo(@NotNull InstanceInfo instance)
    {
        return Long.compare(this.timeAccessed, instance.timeAccessed);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof InstanceInfo && Objects.equals(this.id, ((InstanceInfo) obj).id);
    }

    void addProject(@NotNull ProjectInfo project)
    {
        this.projects.put(project.getId(), project);
    }

    void removeProject(@NotNull String projectId)
    {
        this.projects.remove(projectId);
    }

    public static class DistributionInfo implements Serializable
    {
        @NotNull
        private final String code;
        @NotNull
        private final String version;

        public DistributionInfo(@NotNull ApplicationInfo info)
        {
            this(info.getBuild().getProductCode(), info.getFullVersion());
        }

        public DistributionInfo(@NotNull String code, @NotNull String version)
        {
            this.code = code;
            this.version = version;
        }

        @NotNull
        public String getCode()
        {
            return this.code;
        }

        @NotNull
        public String getVersion()
        {
            return this.version;
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof DistributionInfo
                   && this.code.equals(((DistributionInfo) o).code) &&
                   this.version.equals(((DistributionInfo) o).version);
        }

        @NotNull
        @Override
        public String toString()
        {
            return GSON.toJson(this);
        }
    }
}
