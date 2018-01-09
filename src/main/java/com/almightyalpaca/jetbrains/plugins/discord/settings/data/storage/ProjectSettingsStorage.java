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

import com.almightyalpaca.jetbrains.plugins.discord.JetbrainsDiscordIntegration;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class ProjectSettingsStorage extends SettingsStorage implements ProjectSettings
{
    private static final long serialVersionUID = JetbrainsDiscordIntegration.PROTOCOL_VERSION;

    @NotNull
    private static final Gson GSON = new Gson();

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }
}
