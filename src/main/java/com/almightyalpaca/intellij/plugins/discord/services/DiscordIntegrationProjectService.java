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
package com.almightyalpaca.intellij.plugins.discord.services;

import com.almightyalpaca.intellij.plugins.discord.data.ProjectInfo;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class DiscordIntegrationProjectService
{
    private final ProjectInfo projectInfo;

    public DiscordIntegrationProjectService(Project project)
    {
        this.projectInfo = new ProjectInfo(project);
    }

    public static DiscordIntegrationProjectService getInstance(@NotNull Project project)
    {
        return ServiceManager.getService(project, DiscordIntegrationProjectService.class);
    }

    public ProjectInfo getProjectInfo()
    {
        return projectInfo;
    }
}
