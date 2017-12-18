package com.almightyalpaca.intellij.plugins.discord.data;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableCollections;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableHashMap;
import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.CloneableMap;
import com.almightyalpaca.intellij.plugins.discord.settings.data.ApplicationSettings;
import com.almightyalpaca.intellij.plugins.discord.settings.data.ProjectSettings;
import gnu.trove.TIntObjectHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jgroups.*;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ReplicatedData implements MembershipListener, StateListener, Closeable
{
    public static final long STATE_TIMEOUT = 1000L;

    @NotNull
    private static final TIntObjectHashMap<Method> methods;
    private static final short INSTANCE_ADD = 1;
    private static final short INSTANCE_REMOVE = 2;
    private static final short INSTANCE_SET_SETTINGS = 3;
    private static final short PROJECT_ADD = 101;
    private static final short PROJECT_REMOVE = 102;
    private static final short PROJECT_SET_SETTINGS = 103;
    private static final short FILE_ADD = 201;
    private static final short FILE_REMOVE = 202;
    private static final short FILE_SET_TIME_ACCESSED = 203;

    static
    {
        try
        {
            methods = new TIntObjectHashMap<>(6);

            methods.put(INSTANCE_ADD, ReplicatedData.class.getDeclaredMethod("_instanceAdd", InstanceInfo.class));
            methods.put(INSTANCE_REMOVE, ReplicatedData.class.getDeclaredMethod("_instanceRemove", int.class));
            methods.put(INSTANCE_SET_SETTINGS, ReplicatedData.class.getDeclaredMethod("_instanceSetSettings", int.class, ApplicationSettings.class));

            methods.put(PROJECT_ADD, ReplicatedData.class.getDeclaredMethod("_projectAdd", int.class, ProjectInfo.class));
            methods.put(PROJECT_REMOVE, ReplicatedData.class.getDeclaredMethod("_projectRemove", int.class, String.class));
            methods.put(PROJECT_SET_SETTINGS, ReplicatedData.class.getDeclaredMethod("_projectSetSettings", int.class, String.class, ProjectSettings.class));

            methods.put(FILE_ADD, ReplicatedData.class.getDeclaredMethod("_fileAdd", int.class, String.class, FileInfo.class));
            methods.put(FILE_REMOVE, ReplicatedData.class.getDeclaredMethod("_fileRemove", int.class, String.class, String.class));
            methods.put(FILE_SET_TIME_ACCESSED, ReplicatedData.class.getDeclaredMethod("_fileSetTimeAccessed", int.class, String.class, String.class, long.class));

            methods.forEachValue(m -> {
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
    protected final RequestOptions call_options = new RequestOptions(ResponseMode.GET_NONE, 5000);
    @NotNull
    protected final Set<Notifier> notifiers = new CopyOnWriteArraySet<>();
    @NotNull
    protected final List<Address> members = new ArrayList<>();
    @NotNull
    protected final JChannel channel;
    @NotNull
    protected CloneableMap<Integer, InstanceInfo> instances;
    @NotNull
    protected RpcDispatcher dispatcher;

    public ReplicatedData(JChannel channel, Notifier... notifiers) throws Exception
    {
        if (notifiers != null)
            this.notifiers.addAll(Arrays.asList(notifiers));

        this.instances = new CloneableHashMap<>();

        this.dispatcher = new RpcDispatcher(channel, this).setMethodLookup(methods::get);
        this.dispatcher.setMembershipListener(this).setStateListener(this);

        this.channel = channel.getState(null, STATE_TIMEOUT);
    }

    public boolean isBlockingUpdates()
    {
        return call_options.mode() == ResponseMode.GET_ALL;
    }

    /**
     * Whether updates across the cluster should be asynchronous (default) or synchronous)
     */
    public void setBlockingUpdates(boolean blocking_updates)
    {
        call_options.mode(blocking_updates ? ResponseMode.GET_ALL : ResponseMode.GET_NONE);
    }

    /**
     * The timeout (in milliseconds) for blocking updates
     */
    public long getTimeout()
    {
        return call_options.timeout();
    }

    /**
     * Sets the cluster call timeout (until all acks have been received)
     *
     * @param timeout The timeout (in milliseconds) for blocking updates
     */
    public void setTimeout(Integer timeout)
    {
        call_options.timeout(timeout);
    }

    public void addNotifier(Notifier n)
    {
        if (n != null)
            notifiers.add(n);
    }

    public void removeNotifier(Notifier n)
    {
        if (n != null)
            notifiers.remove(n);
    }

    @Override
    public void close()
    {
        this.dispatcher.stop();
        Util.close(channel);
    }

    @Override
    public String toString()
    {
        return "ReplicatedData{" + "instances=" + instances + '}';
    }

    @NotNull
    public CloneableMap<Integer, InstanceInfo> getInstances()
    {
        return CloneableCollections.unmodifiableCloneableMap(instances);
    }

    public RenderContext getRenderContext()
    {
        return new RenderContext(this);
    }

    @Nullable
    public InstanceInfo getNewestInstance()
    {
        return this.instances.values().stream().max(Comparator.naturalOrder()).orElse(null);
    }

    public void instanceAdd(@NotNull InstanceInfo instance)
    {
        try
        {
            MethodCall call = new MethodCall(INSTANCE_ADD, instance);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceAdd(" + instance + ") failed", e);
        }
    }

    public void instanceRemove(@NotNull InstanceInfo instance)
    {
        try
        {
            MethodCall call = new MethodCall(INSTANCE_REMOVE, instance.getId());
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceRemove(" + instance + ") failed", e);
        }
    }

    public void instanceSetSettings(@NotNull InstanceInfo instance, @NotNull ApplicationSettings settings)
    {
        try
        {
            MethodCall call = new MethodCall(INSTANCE_SET_SETTINGS, instance.getId(), settings);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("instanceRemove(" + instance + ", " + settings + ") failed", e);
        }
    }

    public void projectAdd(@NotNull InstanceInfo instance, @NotNull ProjectInfo project)
    {
        try
        {
            MethodCall call = new MethodCall(PROJECT_ADD, instance.getId(), project);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectAdd(" + instance + ", " + project + ") failed", e);
        }
    }

    public void projectRemove(@NotNull InstanceInfo instance, @NotNull ProjectInfo project)
    {
        try
        {
            MethodCall call = new MethodCall(PROJECT_REMOVE, instance.getId(), project.getId());
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectRemove(" + instance + ", " + project + ") failed", e);
        }
    }

    public void projectSetSettings(@NotNull InstanceInfo instance, @NotNull ProjectInfo project, @NotNull ProjectSettings settings)
    {
        try
        {
            MethodCall call = new MethodCall(PROJECT_SET_SETTINGS, instance.getId(), project.getId(), settings);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("projectSetSettings(" + instance + ", " + project + ", " + settings + ") failed", e);
        }
    }

    public void fileAdd(@NotNull InstanceInfo instance, @NotNull ProjectInfo project, @NotNull FileInfo file)
    {
        try
        {
            MethodCall call = new MethodCall(FILE_ADD, instance.getId(), project.getId(), file);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileAdd(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void fileRemove(@NotNull InstanceInfo instance, @NotNull ProjectInfo project, @NotNull FileInfo file)
    {
        try
        {
            MethodCall call = new MethodCall(FILE_REMOVE, instance.getId(), project.getId(), file.getId());
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileRemove(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void fileSetTimeAccessed(@NotNull InstanceInfo instance, @NotNull ProjectInfo project, @NotNull FileInfo file, long timeAccessed)
    {
        try
        {
            MethodCall call = new MethodCall(FILE_SET_TIME_ACCESSED, instance.getId(), project.getId(), file.getId(), timeAccessed);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("fileSetTimeAccessed(" + instance + ", " + project + ", " + file + ", " + timeAccessed + ") failed", e);
        }
    }

    /*------------------------ Callbacks -----------------------*/

    protected void _instanceAdd(@NotNull InstanceInfo instance)
    {
        this.instances.put(instance.getId(), instance);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.INSTANCE);
    }

    protected void _instanceRemove(int instanceId)
    {
        this.instances.remove(instanceId);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.INSTANCE);
    }

    protected void _instanceSetSettings(int instanceId, @NotNull ApplicationSettings settings)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.setSettings(settings);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.INSTANCE);
    }

    protected void _projectAdd(int instanceId, @NotNull ProjectInfo project)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.addProject(project);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.PROJECT);
    }

    protected void _projectRemove(int instanceId, @NotNull String projectId)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
            instance.removeProject(projectId);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.PROJECT);
    }

    protected void _projectSetSettings(int instanceId, @NotNull String projectId, @NotNull ProjectSettings<? extends ProjectSettings> settings)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.setSettings(settings);
        }

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.PROJECT);
    }

    protected void _fileAdd(int instanceId, @NotNull String projectId, @NotNull FileInfo file)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.addFile(file);
        }

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.FILE);
    }

    protected void _fileRemove(int instanceId, @NotNull String projectId, @NotNull String fileId)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
                project.removeFile(fileId);
        }

        for (Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.FILE);
    }

    protected void _fileSetTimeAccessed(int instanceId, @NotNull String projectId, @NotNull String fileId, long timeAccessed)
    {
        InstanceInfo instance = this.instances.get(instanceId);

        if (instance != null)
        {
            ProjectInfo project = instance.getProjects().get(projectId);

            if (project != null)
            {
                FileInfo file = project.getFiles().get(fileId);

                if (file != null)
                    file.setTimeAccessed(timeAccessed);
            }
        }

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.dataUpdated(Notifier.Level.FILE);
    }

    /*-------------------- State Exchange ----------------------*/

    public void getState(@NotNull OutputStream outputStream) throws Exception
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(outputStream, 1024)))
        {
            oos.writeObject(instances);
        }
    }

    /*------------------- Membership Changes ----------------------*/

    public void viewAccepted(@NotNull View view)
    {
        List<Integer> ids = view.getMembers().stream().map(Address::hashCode).collect(Collectors.toList());

        this.instances.keySet().stream().filter(i -> !ids.contains(i)).forEach(this::_instanceRemove);
    }

    @SuppressWarnings("unchecked")
    public void setState(@NotNull InputStream inputStream) throws Exception
    {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream))
        {
            this.instances = (CloneableMap<Integer, InstanceInfo>) ois.readObject();
        }
    }

    public interface Notifier
    {
        void dataUpdated(@NotNull Level level);

        enum Level
        {
            INSTANCE,
            PROJECT,
            FILE
        }
    }
}
