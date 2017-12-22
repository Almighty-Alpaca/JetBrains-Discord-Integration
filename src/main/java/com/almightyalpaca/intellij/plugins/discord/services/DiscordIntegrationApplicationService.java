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
import club.minnced.discord.rpc.DiscordRichPresence;
import com.almightyalpaca.intellij.plugins.discord.data.*;
import com.almightyalpaca.intellij.plugins.discord.notifications.DiscordIntegrationErrorNotification;
import com.almightyalpaca.intellij.plugins.discord.rpc.RPC;
import com.almightyalpaca.intellij.plugins.discord.utils.JGroupsUtil;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

import java.util.concurrent.TimeUnit;

public class DiscordIntegrationApplicationService implements Receiver, ReplicatedData.Notifier
{
    public static final String CLIENT_ID = "382629176030658561";

    private volatile JChannel channel;
    private volatile ReplicatedData data;
    private volatile InstanceInfo instanceInfo;
    private volatile boolean initialized = false;

    public static DiscordIntegrationApplicationService getInstance()
    {
        return ServiceManager.getService(DiscordIntegrationApplicationService.class);
    }

    public synchronized void init() throws Exception
    {
        if (!this.initialized)
        {
            try
            {
                String props = "udp.xml";

                this.channel = new JChannel(props);
                this.channel.setReceiver(this);
                this.channel.connect("IntelliJDiscordIntegration");

                this.instanceInfo = new InstanceInfo(channel.getAddress().hashCode(), ApplicationInfo.getInstance());

                this.data = new ReplicatedData(channel, this);
                this.data.instanceAdd(this.instanceInfo);

                this.initialized = true;
            }
            catch (Exception e)
            {
                if (this.channel != null)
                {
                    this.channel.close();
                    this.channel = null;
                }

                if (this.data != null)
                {
                    this.data.close();
                    this.data = null;
                }

                RPC.dispose();

                throw e;
            }
        }
    }

    public synchronized void dispose()
    {
        if (this.initialized)
        {
            this.initialized = false;

            RPC.dispose();

            channel.close();
            channel = null;
        }
    }

    private void rpcError(int code, @Nullable String text)
    {
        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void rpcDisconnected(int code, @Nullable String text)
    {
        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void rpcReady() {}

    @Override
    public void receive(Message msg) {}

    @Override
    public void viewAccepted(@NotNull View view)
    {
        if (JGroupsUtil.isLeader(channel))
        {
            DiscordEventHandlers handlers = new DiscordEventHandlers();

            handlers.ready = this::rpcReady;
            handlers.errored = this::rpcError;
            handlers.disconnected = this::rpcDisconnected;

            RPC.init(handlers, CLIENT_ID);
        }
    }

    @Override
    public synchronized void dataUpdated(@NotNull Level level)
    {
        if (!JGroupsUtil.isLeader(channel))
            return;

        checkInitialized();

        final long timeout;
        switch (level)
        {
            case INSTANCE:
                timeout = 10;
                break;
            case UNKNOWN:
            case PROJECT:
                timeout = 5;
                break;
            case FILE:
            default:
                timeout = 2;
                break;
        }
        RPC.setPresenceDelay(timeout, TimeUnit.SECONDS);

        RenderContext context = new RenderContext(data);

        InstanceInfo instance = context.getInstance();
        ProjectInfo project = context.getProject();
        FileInfo file = context.getFile();

        DiscordRichPresence presence = new DiscordRichPresence();

        if (instance != null)
        {
            InstanceInfo.DistributionInfo distribution = instance.getDistribution();

            if (project != null)
            {
                presence.details = "Working on " + project.getName();
                presence.startTimestamp = project.getTimeOpened() / 1000;

                if (file != null)
                {
                    presence.state = "Editing " + (instance.getSettings().isShowFileExtensions() ? file.getName() : file.getBaseName());

                    presence.largeImageKey = file.getAssetName(instance.getSettings().isShowUnknownImageFile()) + "-large";
                    presence.largeImageText = file.getLanguageName();

                    presence.smallImageKey = distribution.getAssetName(instance.getSettings().isShowUnknownImageIDE()) + "-small";
                    presence.smallImageText = "Using " + distribution.getName() + " version " + distribution.getVersion();
                }
            }

            if (presence.largeImageKey == null || presence.largeImageKey.equals("none-large"))
            {
                presence.largeImageKey = distribution.getAssetName(instance.getSettings().isShowUnknownImageIDE()) + "-large";
                presence.largeImageText = "Using " + distribution.getName() + " version " + distribution.getVersion();

                presence.smallImageKey = null;
                presence.smallImageText = null;
            }
        }

        RPC.setRichPresence(presence);
    }

    public synchronized ReplicatedData getData() throws IllegalStateException
    {
        checkInitialized();

        return data;
    }

    public synchronized InstanceInfo getInstanceInfo() throws IllegalStateException
    {
        checkInitialized();

        return instanceInfo;
    }

    public synchronized void checkInitialized()
    {
        if (!this.initialized)
        {
            try
            {
                this.init();
            }
            catch (Exception e)
            {
                Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has thrown an unexpected exception: " + e));

                throw new IllegalStateException("DiscordIntegrationApplicationService could not be initialized", e);
            }
        }
    }
}
