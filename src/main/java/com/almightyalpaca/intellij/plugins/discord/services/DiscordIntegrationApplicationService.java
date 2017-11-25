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

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.almightyalpaca.intellij.plugins.discord.notifications.DiscordIntegrationErrorNotification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordIntegrationApplicationService
{
    public static final Logger LOGGER = LoggerFactory.getLogger(DiscordIntegrationApplicationService.class);

    public static final String CLIENT_ID = "382629176030658561";

    private static Thread callbackRunner;

    public synchronized void init()
    {
        LOGGER.debug("INIT");

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = this::ready;
        handlers.errored = this::error;
        handlers.disconnected = this::disconnected;

        DiscordRPC.INSTANCE.Discord_Initialize(CLIENT_ID, handlers, true, null);

        updateProject(null);

        if (callbackRunner == null)
        {
            callbackRunner = new Thread(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            DiscordRPC.INSTANCE.Discord_RunCallbacks();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ignored) {}
                        }
                    }, "RPC-Callback-Handler");

            callbackRunner.start();
        }
    }

    public synchronized void dispose()
    {
        LOGGER.debug("DISPOSE");

        DiscordRPC.INSTANCE.Discord_UpdatePresence(null);

        DiscordRPC.INSTANCE.Discord_Shutdown();

        if (callbackRunner != null)
            callbackRunner.interrupt();
    }

    private void error(int code, @Nullable String text)
    {
        LOGGER.warn("ERROR: " + code + "/" + text);

        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void disconnected(int code, @Nullable String text)
    {
        LOGGER.warn("DISCONNECTED: " + code + "/" + text);

        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void ready()
    {
        LOGGER.debug("READY");
    }

    public void updateProject(@Nullable Project project)
    {
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.largeImageKey = "intellij-logo-large";
        presence.largeImageText = "Intellij";
        presence.smallImageKey = "intellij-logo-small";
        presence.smallImageText = "Intellij";

        if (project != null)
        {
            presence.details = "Working on " + project.getName();
            presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        }

        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }
}
