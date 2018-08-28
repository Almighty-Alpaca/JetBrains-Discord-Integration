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
package com.almightyalpaca.jetbrains.plugins.discord.settings.data.storage;

import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings;
import com.google.gson.Gson;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProjectSettingsStorage extends SettingsStorage implements ProjectSettings
{
    @NotNull
    private static final Gson GSON = new Gson();

    @Attribute
    @NotNull
    private String description = "";

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(@Nullable String description)
    {
        this.description = description == null ? "" : description.trim();
    }

    @Override
    public boolean equals(Object o)
    {
        // @formatter:off
        if (this == o)
            return true;
        if (!(o instanceof ProjectSettingsStorage))
            return false;
        ProjectSettingsStorage that = (ProjectSettingsStorage) o;
        return super.equals(that)
                && Objects.equals(this.getDescription(), that.getDescription());
        // @formatter:on
    }

    @Override
    public int hashCode()
    {
        // @formatter:off
        return Objects.hash(
                super.hashCode(),
                this.getDescription());
        // @formatter:on
    }
}
