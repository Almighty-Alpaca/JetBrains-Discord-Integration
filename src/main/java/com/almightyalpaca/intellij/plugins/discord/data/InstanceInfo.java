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

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableCollections;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableHashMap;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableMap;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import com.almightyalpaca.intellij.plugins.discord.settings.DiscordIntegrationApplicationSettings;
import com.almightyalpaca.intellij.plugins.discord.settings.data.ApplicationSettings;
import com.intellij.openapi.application.ApplicationInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstanceInfo implements Serializable, ReallyCloneable<InstanceInfo>, Comparable<InstanceInfo>
{
    @NotNull
    private final CloneableMap<String, ProjectInfo> projects;
    @NotNull
    private final DistributionInfo distribution;
    private final int id;
    private final long timeOpened;
    @NotNull
    private ApplicationSettings<?> settings;
    private long timeAccessed;

    public InstanceInfo(int id, @NotNull ApplicationSettings<?> settings, @NotNull String distributionCode, @NotNull String distributionVersion, long timeOpened)
    {
        this(id, settings, distributionCode, distributionVersion, timeOpened, timeOpened);
    }

    public InstanceInfo(int id, @NotNull ApplicationSettings<?> settings, @NotNull String distributionCode, @NotNull String distributionVersion, long timeOpened, long timeAccessed)
    {
        this(id, settings, new DistributionInfo(distributionCode, distributionVersion), timeOpened, timeAccessed);
    }

    public InstanceInfo(int id, @NotNull ApplicationSettings<?> settings, @NotNull DistributionInfo distribution, long timeOpened, long timeAccessed)
    {
        this(id, settings, distribution, timeOpened, timeAccessed, new CloneableHashMap<>());
    }

    public InstanceInfo(int id, @NotNull ApplicationSettings<?> settings, @NotNull DistributionInfo distribution, long timeOpened, long timeAccessed, @NotNull CloneableMap<String, ProjectInfo> projects)
    {
        this.id = id;
        this.settings = settings;
        this.distribution = distribution;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;
        this.projects = projects;
    }

    public InstanceInfo(int id, @NotNull ApplicationInfo info)
    {
        this(id, DiscordIntegrationApplicationSettings.getInstance().getSettings(), info.getBuild().getProductCode(), info.getFullVersion(), System.currentTimeMillis());
    }

    @Override
    public String toString()
    {
        return "InstanceInfo{" + "projects=" + projects + ", distribution=" + distribution + ", id=" + id + ", settings=" + settings + '}';
    }

    public long getTimeOpened()
    {
        return timeOpened;
    }

    public long getTimeAccessed()
    {
        return timeAccessed;
    }

    void setTimeAccessed(long timeAccessed)
    {
        this.timeAccessed = timeAccessed;
    }

    @NotNull
    public ApplicationSettings getSettings()
    {
        return settings;
    }

    void setSettings(@NotNull ApplicationSettings<?> settings)
    {
        this.settings = settings;
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
    public CloneableMap<String, ProjectInfo> getProjects()
    {
        return CloneableCollections.unmodifiableCloneableMap(projects);
    }

    @Override
    public int compareTo(@NotNull InstanceInfo instance)
    {
        return Long.compare(timeAccessed, instance.timeAccessed);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof InstanceInfo && Objects.equals(id, ((InstanceInfo) obj).id);
    }

    @NotNull
    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public InstanceInfo clone()
    {
        return new InstanceInfo(id, settings.clone(), distribution.clone(), timeOpened, timeAccessed, projects.clone());
    }

    void addProject(@NotNull ProjectInfo project)
    {
        this.projects.put(project.getId(), project);
    }

    void removeProject(@NotNull String projectId)
    {
        this.projects.remove(projectId);
    }

    public static class DistributionInfo implements Serializable, ReallyCloneable<DistributionInfo>
    {
        @NotNull
        private final String code;
        @NotNull
        private final String version;
        @NotNull
        private transient Type type;

        public DistributionInfo(@NotNull ApplicationInfo info)
        {
            this(info.getBuild().getProductCode(), info.getFullVersion());
        }

        public DistributionInfo(@NotNull String code, @NotNull String version)
        {
            this.code = code;
            this.type = Type.get(code);
            this.version = version;
        }

        @NotNull
        public String getCode()
        {
            return code;
        }

        @NotNull
        public Type getType()
        {
            return type;
        }

        @NotNull
        public String getVersion()
        {
            return version;
        }

        @NotNull
        public String getName()
        {
            return this.type == Type.UNKNOWN ? Type.UNKNOWN.getName() + " (" + getCode() + ")" : this.type.getName();
        }

        @NotNull
        public String getAssetName(boolean showUnknown)
        {
            return this.type.getAssetName(showUnknown);
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof DistributionInfo && type.equals(((DistributionInfo) o).type) && version.equals(((DistributionInfo) o).version);
        }

        @Override
        public String toString()
        {
            return "DistributionInfo{" + "code='" + code + '\'' + ", version='" + version + '\'' + '}';
        }

        private void writeObject(@NotNull ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }

        private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();

            this.type = Type.get(code);
        }

        @NotNull
        @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
        @Override
        public DistributionInfo clone()
        {
            return this;
        }

        public enum Type
        {
            ANDROID_STUDIO("Android Studio", "android-studio", "AI"),
            APPCODE("AppCode", "appcode", "OC"),
            CLION("CLion", "clion", "CL"),
            DATAGRIP("DataGrip", "datagrip", "DB"),
            GOLAND("GoLand", "goland", "GO"),
            INTELLIJ_IDEA_COMMUNITY("IntelliJ IDEA Community", "intellij-idea", "IC"),
            INTELLIJ_IDEA_ULTIMATE("IntelliJ IDEA Ultimate", "intellij-idea", "IU"),
            MPS("MPS", "mps", "MPS"),
            PHPSTORM("PhpStorm", "phpstorm", "PS"),
            PYCHARM_COMMUNITY("PyCharm Community", "pycharm", "PC"),
            PYCHARM_EDU("PyCharm Edu", "pycharm-edu", "PE"),
            PYCHARM_PROFESSIONAL("PyCharm Professional", "pycharm", "PY"),
            RIDER("Rider", "rider", "RD"),
            RUBYMINE("RubyMine", "rubymine", "RM"),
            WEBSTORM("WebStorm", "webstorm", "WS"),

            UNKNOWN("Unknown Distribution", "unknown", "")
                    {
                        @NotNull
                        @Override
                        public String getAssetName(boolean showUnknown)
                        {
                            return showUnknown ? super.getAssetName(true) : "none";
                        }
                    };

            @NotNull
            private static final Map<String, Type> MAP;

            static
            {
                MAP = new HashMap<>();

                for (Type distribution : Type.values())
                    for (String code : distribution.getCodes())
                        if (MAP.put(code, distribution) != null)
                            throw new ExceptionInInitializerError("Two distributions cannot have the same code");
            }

            @NotNull
            private final String name;
            @NotNull
            private final String assetName;
            @NotNull
            private final String[] codes;

            Type(@NotNull String name, @NotNull String assetName, @NotNull String... codes)
            {
                this.name = name;
                this.assetName = assetName;
                this.codes = codes;
            }

            public static Type get(@NotNull String code)
            {
                return MAP.getOrDefault(code, UNKNOWN);
            }

            @NotNull
            public String getName()
            {
                return name;
            }

            @NotNull
            public String[] getCodes()
            {
                return codes;
            }

            @NotNull
            public String getAssetName(boolean showUnknown)
            {
                return assetName;
            }
        }
    }
}
