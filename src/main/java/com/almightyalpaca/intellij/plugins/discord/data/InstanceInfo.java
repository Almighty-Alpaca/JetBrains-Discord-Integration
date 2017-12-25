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

import com.almightyalpaca.intellij.plugins.discord.settings.DiscordIntegrationApplicationSettings;
import com.almightyalpaca.intellij.plugins.discord.settings.data.ApplicationSettings;
import com.intellij.openapi.application.ApplicationInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InstanceInfo implements Serializable, Comparable<InstanceInfo>
{
    @NotNull
    private final Map<String, ProjectInfo> projects;
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
        this(id, settings, distribution, timeOpened, timeAccessed, new HashMap<>());
    }

    public InstanceInfo(int id, @NotNull ApplicationSettings<?> settings, @NotNull DistributionInfo distribution, long timeOpened, long timeAccessed, @NotNull Map<String, ProjectInfo> projects)
    {
        this.id = id;
        this.settings = settings;
        this.distribution = distribution;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;
        this.projects = Collections.synchronizedMap(new HashMap<>(projects));
    }

    public InstanceInfo(int id, @NotNull ApplicationInfo info)
    {
        this(id, DiscordIntegrationApplicationSettings.getInstance().getSettings(), info.getBuild().getProductCode(), info.getFullVersion(), System.currentTimeMillis());
    }

    @NotNull
    @Override
    public String toString()
    {
        return "InstanceInfo{" + "projects=" + this.projects + ", distribution=" + this.distribution + ", id=" + this.id + ", settings=" + this.settings + '}';
    }

    public long getTimeOpened()
    {
        return this.timeOpened;
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
    public ApplicationSettings getSettings()
    {
        return this.settings;
    }

    void setSettings(@NotNull ApplicationSettings<?> settings)
    {
        this.settings = settings;
    }

    public int getId()
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
            return this.code;
        }

        @NotNull
        public Type getType()
        {
            return this.type;
        }

        @NotNull
        public String getVersion()
        {
            return this.version;
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
            return o instanceof DistributionInfo && this.type.equals(((DistributionInfo) o).type) && this.version.equals(((DistributionInfo) o).version);
        }

        @NotNull
        @Override
        public String toString()
        {
            return "DistributionInfo{" + "code='" + this.code + '\'' + ", version='" + this.version + '\'' + '}';
        }

        private void readObject(@NotNull ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();

            this.type = Type.get(code);
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
                return this.name;
            }

            @NotNull
            public String[] getCodes()
            {
                return this.codes;
            }

            @NotNull
            public String getAssetName(boolean showUnknown)
            {
                return this.assetName;
            }
        }
    }
}
