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
package com.almightyalpaca.jetbrains.plugins.discord.data;

import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ApplicationSettings;
import com.almightyalpaca.jetbrains.plugins.discord.settings.data.ProjectSettings;
import com.google.gson.Gson;
import com.intellij.openapi.util.Pair;
import gnu.trove.TIntObjectHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgroups.*;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ReplicatedData implements MembershipListener, StateListener, Closeable
{
    public static final long STATE_TIMEOUT = 1000L;

    @NotNull
    private static final Gson GSON = new Gson();
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(ReplicatedData.class);
    @NotNull
    private static final TIntObjectHashMap<Method> METHODS;
    /*
     * xx1 -> add
     * xx2 -> remove
     * xx3 -> update (time accessed)
     * xx4 -> set settings
     *
     * x50 -> file - set read-only
     * x51 -> instance - set has rpc connection
     */
    private static final short INSTANCE_ADD = 1;
    private static final short INSTANCE_REMOVE = 2;
    private static final short INSTANCE_UPDATE = 3;
    private static final short INSTANCE_SET_SETTINGS = 4;
    private static final short INSTANCE_SET_HAS_RPC_CONNECTION = 51;
    private static final short PROJECT_ADD = 101;
    private static final short PROJECT_REMOVE = 102;
    private static final short PROJECT_UPDATE = 103;
    private static final short PROJECT_SET_SETTINGS = 104;
    private static final short FILE_ADD = 201;
    private static final short FILE_REMOVE = 202;
    private static final short FILE_UPDATE = 203;
    private static final short FILE_SET_READ_ONLY = 250;

    static
    {
        try
        {
            METHODS = new TIntObjectHashMap<>(13);

            METHODS.put(INSTANCE_ADD, ReplicatedData.class.getDeclaredMethod("_instanceAdd", long.class, InstanceInfo.class));
            METHODS.put(INSTANCE_REMOVE, ReplicatedData.class.getDeclaredMethod("_instanceRemove", long.class, String.class));
            METHODS.put(INSTANCE_UPDATE, ReplicatedData.class.getDeclaredMethod("_instanceUpdate", long.class, String.class));
            METHODS.put(INSTANCE_SET_SETTINGS, ReplicatedData.class.getDeclaredMethod("_instanceSetSettings", long.class, String.class, ApplicationSettings.class));
            METHODS.put(INSTANCE_SET_HAS_RPC_CONNECTION, ReplicatedData.class.getDeclaredMethod("_instanceSetHasRpcConnection", long.class, String.class, boolean.class));

            METHODS.put(PROJECT_ADD, ReplicatedData.class.getDeclaredMethod("_projectAdd", long.class, String.class, ProjectInfo.class));
            METHODS.put(PROJECT_REMOVE, ReplicatedData.class.getDeclaredMethod("_projectRemove", long.class, String.class, String.class));
            METHODS.put(PROJECT_UPDATE, ReplicatedData.class.getDeclaredMethod("_projectUpdate", long.class, String.class, String.class));
            METHODS.put(PROJECT_SET_SETTINGS, ReplicatedData.class.getDeclaredMethod("_projectSetSettings", long.class, String.class, String.class, ProjectSettings.class));

            METHODS.put(FILE_ADD, ReplicatedData.class.getDeclaredMethod("_fileAdd", long.class, String.class, String.class, FileInfo.class));
            METHODS.put(FILE_REMOVE, ReplicatedData.class.getDeclaredMethod("_fileRemove", long.class, String.class, String.class, String.class));
            METHODS.put(FILE_UPDATE, ReplicatedData.class.getDeclaredMethod("_fileUpdate", long.class, String.class, String.class, String.class));
            METHODS.put(FILE_SET_READ_ONLY, ReplicatedData.class.getDeclaredMethod("_fileSetReadOnly", long.class, String.class, String.class, String.class, boolean.class));

            METHODS.forEachValue(m -> {
                m.setAccessible(true);
                return true;
            });
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    protected transient final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "JetbrainsDiscordIntegration-Notifier"));
    @NotNull
    protected transient final AtomicReference<Notifier.Type> modified = new AtomicReference<>(null);
    @NotNull
    protected transient final RequestOptions call_options = new RequestOptions(ResponseMode.GET_ALL, 100);
    @NotNull
    protected transient final Set<Notifier> notifiers = new CopyOnWriteArraySet<>();
    @NotNull
    protected transient final List<Address> members = new ArrayList<>();
    @NotNull
    protected transient final JChannel channel;
    @NotNull
    protected final Map<String, InstanceInfo> instances;
    @NotNull
    protected transient RpcDispatcher dispatcher;

    public ReplicatedData(@NotNull JChannel channel, @NotNull Notifier... notifiers) throws Exception
    {
        LOG.trace("ReplicatedData#new()");
        this.executor.scheduleAtFixedRate(() -> {
            Notifier.Type type = modified.getAndSet(null);
            if (type != null)
                this.notifiers.forEach(n -> n.dataUpdated(type));
        }, 1000, 500, TimeUnit.MILLISECONDS);

        this.notifiers.addAll(Arrays.asList(notifiers));

        this.instances = Collections.synchronizedMap(new HashMap<>(1));

        this.dispatcher = new RpcDispatcher(channel, this);
        this.dispatcher.setMethodLookup(METHODS::get);
        this.dispatcher.setMembershipListener(this);
        this.dispatcher.setStateListener(this);

        this.channel = channel.getState(null, STATE_TIMEOUT);

        LOG.trace("ReplicatedData#new() end");
    }

    public boolean isBlockingUpdates()
    {
        return this.call_options.mode() == ResponseMode.GET_ALL;
    }

    /**
     * Whether updates across the cluster should be asynchronous (default) or synchronous)
     */
    public void setBlockingUpdates(boolean blocking_updates)
    {
        this.call_options.mode(blocking_updates ? ResponseMode.GET_ALL : ResponseMode.GET_NONE);
    }

    /**
     * The timeout (in milliseconds) for blocking updates
     */
    public long getTimeout()
    {
        return this.call_options.timeout();
    }

    /**
     * Sets the cluster call timeout (until all acks have been received)
     *
     * @param timeout The timeout (in milliseconds) for blocking updates
     */
    public void setTimeout(long timeout)
    {
        this.call_options.timeout(timeout);
    }

    public void addNotifier(@NotNull Notifier n)
    {
        this.notifiers.add(n);
    }

    public void removeNotifier(@NotNull Notifier n)
    {
        this.notifiers.remove(n);
    }

    @Override
    public void close()
    {
        this.dispatcher.stop();
        this.executor.shutdownNow();
        Util.close(this.channel);
    }

    @NotNull
    @Override
    public String toString()
    {
        return GSON.toJson(this);
    }

    @NotNull
    public Map<String, InstanceInfo> getInstances()
    {
        return new HashMap<>(this.instances);
    }

    public void instanceAdd(long timestamp, @Nullable InstanceInfo instance)
    {
        LOG.trace("ReplicatedData#instanceAdd({})", instance);

        if (instance == null)
            return;

        this._instanceAdd(timestamp, instance);

        try
        {
            MethodCall call = new MethodCall(INSTANCE_ADD, timestamp, instance);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceAdd(" + instance + ") failed", e);
        }
    }

    public void instanceRemove(long timestamp, @Nullable InstanceInfo instance)
    {
        LOG.trace("ReplicatedData#instanceRemove({})", instance);

        if (instance == null)
            return;

        this._instanceRemove(timestamp, instance.getId());

        try
        {
            MethodCall call = new MethodCall(INSTANCE_REMOVE, timestamp, instance.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceRemove(" + instance + ") failed", e);
        }
    }

    public void instanceSetSettings(long timestamp, @Nullable InstanceInfo instance, @Nullable ApplicationSettings settings)
    {
        LOG.trace("ReplicatedData#instanceSetSettings({}, {}, {})", timestamp, instance, settings);

        if (timestamp < 0 || instance == null || settings == null)
            return;

        this._instanceSetSettings(timestamp, instance.getId(), settings);

        try
        {
            MethodCall call = new MethodCall(INSTANCE_SET_SETTINGS, timestamp, instance.getId(), settings);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceSetSettings(" + timestamp + ", " + instance + ", " + settings + ") failed", e);
        }
    }

    public void instanceSetHasRpcConnection(long timestamp, @Nullable InstanceInfo instance, boolean hasRpcConnection)
    {
        LOG.trace("ReplicatedData#instanceSetHasRpcConnection({}, {})", instance, hasRpcConnection);

        if (instance == null)
            return;

        this._instanceSetHasRpcConnection(timestamp, instance.getId(), hasRpcConnection);

        try
        {
            MethodCall call = new MethodCall(INSTANCE_SET_HAS_RPC_CONNECTION, timestamp, instance.getId(), hasRpcConnection);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceSetHasRpcConnection(" + instance + ", " + hasRpcConnection + ") failed", e);
        }
    }

    public void instanceUpdate(long timestamp, @Nullable InstanceInfo instance)
    {
        LOG.trace("ReplicatedData#instanceUpdate({}, {})", timestamp, instance);

        if (timestamp < 0 || instance == null)
            return;

        this._instanceUpdate(timestamp, instance.getId());

        try
        {
            MethodCall call = new MethodCall(INSTANCE_UPDATE, timestamp, instance.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceUpdate(" + timestamp + ", " + instance + ") failed", e);
        }
    }

    public void projectAdd(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project)
    {
        LOG.trace("ReplicatedData#projectAdd({}, {})", instance, project);

        if (instance == null || project == null)
            return;

        this._projectAdd(timestamp, instance.getId(), project);

        try
        {
            MethodCall call = new MethodCall(PROJECT_ADD, timestamp, instance.getId(), project);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectAdd(" + instance + ", " + project + ") failed", e);
        }
    }

    public void projectRemove(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project)
    {
        LOG.trace("ReplicatedData#projectRemove({}, {})", instance, project);

        if (instance == null || project == null)
            return;

        this._projectRemove(timestamp, instance.getId(), project.getId());

        try
        {
            MethodCall call = new MethodCall(PROJECT_REMOVE,timestamp,  instance.getId(), project.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectRemove(" + instance + ", " + project + ") failed", e);
        }
    }

    public void projectSetSettings(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable ProjectSettings settings)
    {
        LOG.trace("ReplicatedData#projectSetSettings({}, {}, {}, {})", timestamp, instance, project, settings);

        if (timestamp < 0 || instance == null || project == null || settings == null)
            return;

        this._projectSetSettings(timestamp, instance.getId(), project.getId(), settings);

        try
        {
            MethodCall call = new MethodCall(PROJECT_SET_SETTINGS, timestamp, instance.getId(), project.getId(), settings);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectSetSettings(" + timestamp + ", " + instance + ", " + project + ", " + settings + ") failed", e);
        }
    }

    public void projectUpdate(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project)
    {
        LOG.trace("ReplicatedData#projectUpdate({}, {}, {})", timestamp, instance, project);

        if (timestamp < 0 || instance == null || project == null)
            return;

        this._projectUpdate(timestamp, instance.getId(), project.getId());

        try
        {
            MethodCall call = new MethodCall(PROJECT_UPDATE, timestamp, instance.getId(), project.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectUpdate(" + timestamp + ", " + instance + ", " + project + ") failed", e);
        }
    }

    public void fileAdd(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file)
    {
        LOG.trace("ReplicatedData#fileAdd({}, {}, {})", instance, project, file);

        if (instance == null || project == null || file == null)
            return;

        this._fileAdd(timestamp, instance.getId(), project.getId(), file);

        try
        {
            MethodCall call = new MethodCall(FILE_ADD, timestamp, instance.getId(), project.getId(), file);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileAdd(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void fileRemove(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file)
    {
        LOG.trace("ReplicatedData#fileRemove({}, {}, {})", instance, project, file);

        if (instance == null || project == null || file == null)
            return;

        this._fileRemove(timestamp, instance.getId(), project.getId(), file.getId());

        try
        {
            MethodCall call = new MethodCall(FILE_REMOVE, timestamp, instance.getId(), project.getId(), file.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileRemove(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void fileUpdate(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file)
    {
        LOG.trace("ReplicatedData#fileUpdate({}, {}, {}, {})", timestamp, instance, project, file);

        if (timestamp < 0 || instance == null || project == null || file == null)
            return;

        this._fileUpdate(timestamp, instance.getId(), project.getId(), file.getId());

        try
        {
            MethodCall call = new MethodCall(FILE_UPDATE, timestamp, instance.getId(), project.getId(), file.getId());
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileUpdate(" + timestamp + ", " + timestamp + ", " + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void fileSetReadOnly(long timestamp, @Nullable InstanceInfo instance, @Nullable ProjectInfo project, @Nullable FileInfo file, boolean readOnly)
    {
        LOG.trace("ReplicatedData#fileSetReadOnly({}, {}, {}, {}, {})", timestamp, instance, project, file, readOnly);

        if (timestamp < 0 || instance == null || project == null || file == null)
            return;

        this._fileSetReadOnly(timestamp, instance.getId(), project.getId(), file.getId(), readOnly);

        try
        {
            MethodCall call = new MethodCall(FILE_SET_READ_ONLY, timestamp, instance.getId(), project.getId(), file.getId(), readOnly);
            this.dispatcher.callRemoteMethods(getTargets(), call, this.call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileSetReadOnly(" + timestamp + ", " + instance + ", " + project + ", " + file + ", " + readOnly + ") failed", e);
        }
    }

    /*--------------- Time accessed update METHODS -------------*/

    private InstanceInfo updateInstance(@NotNull String instanceId, long timeAccessed)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            // @formatter:off
            if (instance.getSettings().isHideAfterPeriodOfInactivity()
                    && instance.getSettings().isResetOpenTimeAfterInactivity()
                    && instance.getTimeAccessed() + instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS)
                        < System.currentTimeMillis())
                // @formatter:on
                instance.setTimeOpened(timeAccessed);

            instance.setTimeAccessed(timeAccessed);

            return instance;
        }

        return null;
    }

    private Pair<InstanceInfo, ProjectInfo> updateProject(@NotNull String instanceId, @NotNull String projectId, long timeAccessed)
    {
        InstanceInfo instance = updateInstance(instanceId, timeAccessed);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
            {
                // @formatter:off
                if (instance.getSettings().isHideAfterPeriodOfInactivity()
                        && instance.getSettings().isResetOpenTimeAfterInactivity()
                        && project.getTimeAccessed() + instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS)
                            < System.currentTimeMillis())
                    // @formatter:on
                    project.setTimeOpened(timeAccessed);

                project.setTimeAccessed(timeAccessed);

                return Pair.create(instance, project);
            }
        }

        return null;
    }

    private void updateFile(@NotNull String instanceId, @NotNull String projectId, @NotNull String fileId, long timeAccessed)
    {
        Pair<InstanceInfo, ProjectInfo> pair = updateProject(instanceId, projectId, timeAccessed);

        if (pair != null)
        {
            InstanceInfo instance = pair.getFirst();
            ProjectInfo project = pair.getSecond();

            FileInfo file = project.getFiles().get(fileId);

            if (file != null)
            {
                // @formatter:off
                if (instance.getSettings().isHideAfterPeriodOfInactivity()
                        && instance.getSettings().isResetOpenTimeAfterInactivity()
                        && file.getTimeAccessed() + instance.getSettings().getInactivityTimeout(TimeUnit.MILLISECONDS)
                            < System.currentTimeMillis())
                    // @formatter:on
                    file.setTimeOpened(timeAccessed);

                file.setTimeAccessed(timeAccessed);
            }
        }
    }

    /*------------------------ Callbacks -----------------------*/

    protected void _instanceAdd(long timestamp, @NotNull InstanceInfo instance)
    {
        LOG.trace("ReplicatedData#_instanceAdd({})", instance);

        this.instances.put(instance.getId(), instance);

        notifyListeners(Notifier.Type.INSTANCE_ADD);
    }

    protected void _instanceRemove(long timestamp, @NotNull String instanceId)
    {
        LOG.trace("ReplicatedData#_instanceRemove({})", instanceId);

        this.instances.remove(instanceId);

        notifyListeners(Notifier.Type.INSTANCE_REMOVE);
    }

    protected void _instanceSetSettings(long timestamp, @NotNull String instanceId, @NotNull ApplicationSettings settings)
    {
        LOG.trace("ReplicatedData#_instanceRemove({}, {}, {})", timestamp, instanceId, settings);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.setSettings(settings);

        updateInstance(instanceId, timestamp);

        notifyListeners(Notifier.Type.INSTANCE_SET_SETTINGS);
    }

    protected void _instanceSetHasRpcConnection(long timestamp, @NotNull String instanceId, boolean hasRpcConnection)
    {
        LOG.trace("ReplicatedData#_instanceSetHasRpcConnection({}, {})", instanceId, hasRpcConnection);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.setHasRpcConnection(hasRpcConnection);

        notifyListeners(Notifier.Type.INSTANCE_SET_HAS_RPC_CONNECTION);
    }

    protected void _instanceUpdate(long timestamp, @NotNull String instanceId)
    {
        LOG.trace("ReplicatedData#_instanceUpdate({}, {})", timestamp, instanceId);

        updateInstance(instanceId, timestamp);

        notifyListeners(Notifier.Type.INSTANCE_UPDATE);
    }

    protected void _projectAdd(long timestamp, @NotNull String instanceId, @NotNull ProjectInfo project)
    {
        LOG.trace("ReplicatedData#_projectAdd({}, {})", instanceId, project);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.addProject(project);

        updateInstance(instanceId, timestamp);

        notifyListeners(Notifier.Type.PROJECT_ADD);
    }

    protected void _projectRemove(long timestamp, @NotNull String instanceId, @NotNull String projectId)
    {
        LOG.trace("ReplicatedData#_projectRemove({}, {})", instanceId, projectId);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.removeProject(projectId);

        updateInstance(instanceId, timestamp);

        notifyListeners(Notifier.Type.PROJECT_REMOVE);
    }

    protected void _projectSetSettings(long timestamp, @NotNull String instanceId, @NotNull String projectId, @NotNull ProjectSettings settings)
    {
        LOG.trace("ReplicatedData#_projectSetSettings({}, {}, {}, {})", timestamp, instanceId, projectId, settings);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.setSettings(settings);
        }

        updateInstance(instanceId, timestamp);

        notifyListeners(Notifier.Type.PROJECT_SET_SETTINGS);
    }

    protected void _projectUpdate(long timestamp, @NotNull String instanceId, @NotNull String projectId)
    {
        LOG.trace("ReplicatedData#_projectUpdate({}, {}, {})", timestamp, instanceId, projectId);

        updateProject(instanceId, projectId, timestamp);

        notifyListeners(Notifier.Type.PROJECT_UPDATE);
    }

    protected void _fileAdd(long timestamp, @NotNull String instanceId, @NotNull String projectId, @NotNull FileInfo file)
    {
        LOG.trace("ReplicatedData#_fileAdd({}, {}, {})", instanceId, projectId, file);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.addFile(file);
        }

        updateProject(instanceId, projectId, timestamp);

        notifyListeners(Notifier.Type.FILE_ADD);
    }

    protected void _fileRemove(long timestamp, @NotNull String instanceId, @NotNull String projectId, @NotNull String fileId)
    {
        LOG.trace("ReplicatedData#_fileRemove({}, {}, {})", instanceId, projectId, fileId);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.removeFile(fileId);
        }

        updateProject(instanceId, projectId, timestamp);

        notifyListeners(Notifier.Type.FILE_REMOVE);
    }

    protected void _fileUpdate(long timestamp, @NotNull String instanceId, @NotNull String projectId, @NotNull String fileId)
    {
        LOG.trace("ReplicatedData#_fileUpdate({}, {}, {}, {})", timestamp, instanceId, projectId, fileId);

        updateFile(instanceId, projectId, fileId, timestamp);

        notifyListeners(Notifier.Type.FILE_UPDATE);
    }

    protected void _fileSetReadOnly(long timestamp, @NotNull String instanceId, @NotNull String projectId, @NotNull String fileId, boolean readOnly)
    {
        LOG.trace("ReplicatedData#_fileSetReadOnly({}, {}, {}, {}, {})", timestamp, instanceId, projectId, fileId, readOnly);

        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
            {
                FileInfo file = project.getFiles().get(fileId);

                if (file != null)
                    file.setReadOnly(readOnly);
            }
        }

        updateFile(instanceId, projectId, fileId, timestamp);

        notifyListeners(Notifier.Type.FILE_SET_READ_ONLY);
    }

    /*-------------------- State Exchange ----------------------*/

    public void getState(@NotNull OutputStream outputStream) throws Exception
    {
        LOG.trace("ReplicatedData#setState()");
        LOG.trace("ReplicatedData#setState()#this.instances = {}", this.instances);

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(outputStream, 1024)))
        {
            oos.writeObject(instances);
        }

        LOG.trace("ReplicatedData#setState() end");
    }

    @SuppressWarnings("unchecked")
    public void setState(@NotNull InputStream inputStream) throws Exception
    {
        LOG.trace("ReplicatedData#setState()");
        LOG.trace("ReplicatedData#setState()#this.instances (before) = {}", this.instances);

        try (ObjectInputStream ois = new ObjectInputStream(inputStream))
        {
            this.instances.clear();
            this.instances.putAll((Map<String, InstanceInfo>) ois.readObject());
        }

        LOG.trace("ReplicatedData#setState()#this.instances (after) = {}", this.instances);
        LOG.trace("ReplicatedData#setState() end");
    }

    /*------------------- Membership Changes ----------------------*/

    public void viewAccepted(@NotNull View view)
    {
        LOG.trace("ReplicatedData#viewAccepted({})", view);

        // @formatter:off
        List<String> ids = view.getMembers().stream()
                .map(Address::toString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // @formatter:on

        boolean modified = instances.entrySet().removeIf(entry -> !ids.contains(entry.getKey()));

        if (modified)
            notifyListeners(Notifier.Type.INSTANCE_REMOVE);
    }

    protected void notifyListeners(@NotNull Notifier.Type type)
    {
        LOG.trace("ReplicatedData#notifyListeners({})", type);

        this.modified.set(type);
    }

    @NotNull
    public Collection<Address> getTargets()
    {
        // @formatter:off
        Collection<Address> channels = channel.getView().getMembers().stream().
                filter(a -> !Objects.equals(a, channel.getAddress()))
                .collect(Collectors.toList());
        // @formatter:on

        LOG.trace("ReplicatedData#getTargets() -> {}", channels.size());

        return channels;
    }

    public interface Notifier
    {
        void dataUpdated(@NotNull Type type);

        enum Type
        {
            INSTANCE_ADD(10),
            INSTANCE_REMOVE(1),
            INSTANCE_UPDATE(2),
            INSTANCE_SET_SETTINGS(1),
            INSTANCE_SET_HAS_RPC_CONNECTION(2),
            PROJECT_ADD(5),
            PROJECT_REMOVE(1),
            PROJECT_UPDATE(2),
            PROJECT_SET_SETTINGS(1),
            FILE_ADD(2),
            FILE_REMOVE(1),
            FILE_UPDATE(2),
            FILE_SET_READ_ONLY(2);

            private final int delay;

            Type(int delay)
            {
                this.delay = delay;
            }

            public int getDelay()
            {
                return delay;
            }
        }
    }
}
