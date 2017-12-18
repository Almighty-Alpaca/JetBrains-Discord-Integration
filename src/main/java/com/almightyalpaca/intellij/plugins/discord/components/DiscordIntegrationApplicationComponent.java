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
package com.almightyalpaca.intellij.plugins.discord.components;

import com.almightyalpaca.intellij.plugins.discord.services.DiscordIntegrationApplicationService;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class DiscordIntegrationApplicationComponent implements ApplicationComponent
{
    private final DiscordIntegrationApplicationService service = DiscordIntegrationApplicationService.getInstance();

    @NotNull
    private final Application application;

    public DiscordIntegrationApplicationComponent(@NotNull Application application)
    {
        this.application = application;
    }

    @NotNull
    public Application getApplication()
    {
        return application;
    }

    @Override
    public void initComponent()
    {
        this.service.checkInitialized();
    }

    @Override
    public void disposeComponent()
    {
        this.service.dispose();
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return DiscordIntegrationApplicationComponent.class.getSimpleName();
    }
}
