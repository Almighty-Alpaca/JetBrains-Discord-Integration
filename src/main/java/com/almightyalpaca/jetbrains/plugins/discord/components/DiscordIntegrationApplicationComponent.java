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
package com.almightyalpaca.jetbrains.plugins.discord.components;

import club.minnced.discord.rpc.DiscordEventHandlers;
import com.almightyalpaca.jetbrains.plugins.discord.JetbrainsDiscordIntegration;
import com.almightyalpaca.jetbrains.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.ReplicatedData;
import com.almightyalpaca.jetbrains.plugins.discord.debug.Logger;
import com.almightyalpaca.jetbrains.plugins.discord.debug.LoggerFactory;
import com.almightyalpaca.jetbrains.plugins.discord.listeners.*;
import com.almightyalpaca.jetbrains.plugins.discord.notifications.DiscordIntegrationErrorNotification;
import com.almightyalpaca.jetbrains.plugins.discord.presence.PresenceRenderContext;
import com.almightyalpaca.jetbrains.plugins.discord.presence.PresenceRenderer;
import com.almightyalpaca.jetbrains.plugins.discord.rpc.RPC;
import com.almightyalpaca.jetbrains.plugins.discord.settings.DiscordIntegrationApplicationSettings;
import com.intellij.AppTopics;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Consumer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscordIntegrationApplicationComponent implements ApplicationComponent, Receiver, ReplicatedData.Notifier
{
    @NotNull
    public static final String CLIENT_ID = "382629176030658561";
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(DiscordIntegrationApplicationComponent.class);

    @NotNull
    private final Application application;
    @NotNull
    private final Object rpcLock = new Object();
    @Nullable
    public MessageBusConnection connection;
    @Nullable
    private InstanceInfo instanceInfo;
    @Nullable
    private JChannel channel;
    @Nullable
    private ReplicatedData data;
    @Nullable
    private DocumentListener documentListener;
    @Nullable
    private EditorMouseListener editorMouseListener;
    @Nullable
    private VisibleAreaListener visibleAreaListener;
    @Nullable
    private VirtualFileListener virtualFileListener;

    public DiscordIntegrationApplicationComponent(@NotNull Application application)
    {
        this.application = application;
    }

    @NotNull
    public static DiscordIntegrationApplicationComponent getInstance()
    {
        return ApplicationManager.getApplication().getComponent(DiscordIntegrationApplicationComponent.class);
    }

    @NotNull
    public Application getApplication()
    {
        return this.application;
    }

    @Override
    public synchronized void initComponent()
    {
        try // Fixes problems that crashes JGroups if those two properties aren't properly set
        {
            Locale locale = Locale.getDefault();
            if (System.getProperty("user.language") == null)
                System.setProperty("user.language", locale.getLanguage());
            if (System.getProperty("user.country") == null)
                System.setProperty("user.country", locale.getCountry());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            String props = "fast.xml";

            LOG.trace("DiscordIntegrationApplicationComponent#initComponent()#props = {}", props);

            JChannel channel = new JChannel(props);
            channel.setReceiver(this);

            this.channel = channel;

            channel.connect("JetbrainsDiscordIntegration v" + JetbrainsDiscordIntegration.PROTOCOL_VERSION);

            ReplicatedData data = new ReplicatedData(channel, this);
            this.data = data;

            this.instanceInfo = new InstanceInfo(channel.getAddressAsString(), ApplicationInfo.getInstance());

            LOG.trace("DiscordIntegrationApplicationComponent#initComponent()#this.instanceInfo = {}", this.instanceInfo);

            data.instanceAdd(System.currentTimeMillis(), this.instanceInfo);

            RPC.updatePresence(2, TimeUnit.SECONDS);
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

            throw new RuntimeException(e);
        }

        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerListener());
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener());

        EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
        multicaster.addDocumentListener(this.documentListener = new DocumentListener());
        multicaster.addEditorMouseListener(this.editorMouseListener = new EditorMouseListener());

        checkExperiementalWindowListener();

        VirtualFileManager.getInstance().addVirtualFileListener(this.virtualFileListener = new VirtualFileListener());
    }

    public synchronized void checkExperiementalWindowListener()
    {

        if (DiscordIntegrationApplicationSettings.getInstance().getSettings().isExperimentalWindowListenerEnabled())
        {
            if (this.visibleAreaListener == null)
            {
                EditorFactory.getInstance().getEventMulticaster().addVisibleAreaListener(this.visibleAreaListener = new VisibleAreaListener());
            }
        }
        else
        {
            if (this.visibleAreaListener != null)
            {
                EditorFactory.getInstance().getEventMulticaster().removeVisibleAreaListener(this.visibleAreaListener);
                this.visibleAreaListener = null;
            }
        }
    }

    @Override
    public synchronized void disposeComponent()
    {
        RPC.dispose();

        if (this.connection != null)
        {
            this.connection.disconnect();
            this.connection = null;
        }

        EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();

        if (this.documentListener != null)
        {
            multicaster.removeDocumentListener(this.documentListener);
            this.documentListener = null;
        }

        if (this.editorMouseListener != null)
        {
            multicaster.removeEditorMouseListener(this.editorMouseListener);
            this.editorMouseListener = null;
        }

        if (this.virtualFileListener != null)
        {
            VirtualFileManager.getInstance().removeVirtualFileListener(this.virtualFileListener);
            this.virtualFileListener = null;
        }

        if (this.visibleAreaListener != null)
        {
            multicaster.removeVisibleAreaListener(this.visibleAreaListener);
            this.visibleAreaListener = null;
        }

        if (this.data != null)
        {
            this.data.close();
            this.data = null;
        }

        if (this.channel != null)
        {
            this.channel.close();
            this.channel = null;
        }
    }

    @Override
    public void receive(Message msg) {}

    @Override
    public void viewAccepted(@NotNull View view)
    {
        LOG.trace("DiscordIntegrationApplicationComponent#viewAccepted({})", view);
    }

    private void rpcError(int code, @Nullable String text)
    {
        LOG.trace("DiscordIntegrationApplicationComponent#rpcError({}, {})", code, text);

        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void rpcDisconnected(int code, @Nullable String text)
    {
        LOG.trace("DiscordIntegrationApplicationComponent#rpcDisconnected({}, {})", code, text);

        Notifications.Bus.notify(new DiscordIntegrationErrorNotification("The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text));
    }

    private void rpcReady()
    {
        LOG.trace("DiscordIntegrationApplicationComponent#rpcReady()");
    }

    @Override
    public void dataUpdated(@NotNull Type type)
    {
        LOG.trace("DiscordIntegrationApplicationComponent#dataUpdated({})", type);
        LOG.trace("DiscordIntegrationApplicationComponent#dataUpdated()#this.instanceInfo = {}", this.instanceInfo);
        LOG.trace("DiscordIntegrationApplicationComponent#dataUpdated()#this.instanceInfo.isHasRpcConnection() = {}", this.instanceInfo != null && this.instanceInfo.isHasRpcConnection());

        if (!instanceInfo.isHasRpcConnection())
            checkRpcConnection(null);

        if (this.instanceInfo != null && this.instanceInfo.isHasRpcConnection())
            RPC.updatePresence(type.getDelay(), TimeUnit.SECONDS);
    }

    protected long presenceUpdated(@NotNull PresenceRenderContext renderContext)
    {
        LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated({})", renderContext);

        InstanceInfo instance = renderContext.getInstance();

        checkRpcConnection(renderContext);

        LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#instance = {}", instance);
        LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#instance.getSettings().isHideAfterPeriodOfInactivity() = {}", instance != null && instance.getSettings().isHideAfterPeriodOfInactivity());

        if (instance != null && instance.getSettings().isHideAfterPeriodOfInactivity())
        {
            long delay = TimeUnit.NANOSECONDS.convert(instance.getTimeAccessed() + instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

            LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#instance.getTimeAccessed() = {}", instance.getTimeAccessed());
            LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS) = {}", instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS));
            LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#System.currentTimeMillis() = {}", System.currentTimeMillis());
            LOG.trace("DiscordIntegrationApplicationComponent#presenceUpdated()#delay = {}", delay);

            return delay;
        }

        return Long.MAX_VALUE;
    }

    private void checkRpcConnection(@Nullable PresenceRenderContext renderContext)
    {
        synchronized (rpcLock)
        {
            LOG.trace("DiscordIntegrationApplicationComponent#checkRpcConnection()");

            if (this.data == null || this.instanceInfo == null)
                return;

            Map<String, InstanceInfo> instances = this.data.getInstances();

            boolean rpcConnectionExists;

            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (instances)
            {
                rpcConnectionExists = instances.values().stream().anyMatch(InstanceInfo::isHasRpcConnection);
            }

            LOG.trace("DiscordIntegrationApplicationComponent#checkRpcConnection()#instanceInfo.isHasRpcConnection() = {}", instanceInfo.isHasRpcConnection());

            if (instanceInfo.isHasRpcConnection() && (renderContext == null ? renderContext = new PresenceRenderContext(data) : renderContext).isEmpty())
            {
                this.data.instanceSetHasRpcConnection(System.currentTimeMillis(), instanceInfo, false);

                RPC.dispose();
            }
            // @formatter:off
            else if (!rpcConnectionExists
                    && !instanceInfo.isHasRpcConnection()
                    && this.channel != null
                    && Objects.equals(channel.getView().getCoord(), this.channel.getAddress())
                    && !(renderContext == null ? new PresenceRenderContext(data) : renderContext).isEmpty())
            // @formatter:on
            {
                this.data.instanceSetHasRpcConnection(System.currentTimeMillis(), instanceInfo, true);

                new Thread(() -> {
                    DiscordEventHandlers handlers = new DiscordEventHandlers();

                    handlers.ready = this::rpcReady;
                    handlers.errored = this::rpcError;
                    handlers.disconnected = this::rpcDisconnected;

                    RPC.init(handlers, CLIENT_ID, () -> new PresenceRenderContext(this.data), new PresenceRenderer(), this::presenceUpdated);
                }, "JetbrainsDiscordIntegration-RPC-Starter").start();
            }
        }
    }

    public void updateData(Consumer<ReplicatedData> consumer)
    {
        if (this.data != null)
            consumer.consume(this.data);
    }

    @NotNull
    @Override
    public String getComponentName()
    {
        return DiscordIntegrationApplicationComponent.class.getSimpleName();
    }

    @Nullable
    public InstanceInfo getInstanceInfo()
    {
        return instanceInfo;
    }

    @Nullable
    public ReplicatedData getData()
    {
        return data;
    }
}
