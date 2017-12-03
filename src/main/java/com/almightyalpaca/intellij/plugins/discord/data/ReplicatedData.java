package com.almightyalpaca.intellij.plugins.discord.data;

import com.almightyalpaca.intellij.plugins.discord.collections.UniqueDeque;
import com.almightyalpaca.intellij.plugins.discord.collections.UniqueLinkedDeque;
import com.almightyalpaca.intellij.plugins.discord.rpc.RPC;
import org.jetbrains.annotations.NotNull;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReplicatedData implements MembershipListener, StateListener, Closeable
{
    public static final long STATE_TIMEOUT = 1000;
    protected static final Map<Short, Method> methods;
    private static final short ADD_INSTANCE = 1;
    private static final short REMOVE_INSTANCE = 2;
    private static final short ADD_PROJECT = 3;
    private static final short REMOVE_PROJECT = 4;
    private static final short ADD_FILE = 5;
    private static final short REMOVE_FILE = 6;

    static
    {
        try
        {
            methods = new HashMap<>(6);
            methods.put(ADD_INSTANCE, ReplicatedData.class.getDeclaredMethod("_addInstance", InstanceInfo.class));
            methods.put(REMOVE_INSTANCE, ReplicatedData.class.getDeclaredMethod("_removeInstance", InstanceInfo.class));
            methods.put(ADD_PROJECT, ReplicatedData.class.getDeclaredMethod("_addProject", InstanceInfo.class, ProjectInfo.class));
            methods.put(REMOVE_PROJECT, ReplicatedData.class.getDeclaredMethod("_removeProject", InstanceInfo.class, ProjectInfo.class));
            methods.put(ADD_FILE, ReplicatedData.class.getDeclaredMethod("_addFile", InstanceInfo.class, ProjectInfo.class, FileInfo.class));
            methods.put(REMOVE_FILE, ReplicatedData.class.getDeclaredMethod("_removeFile", InstanceInfo.class, ProjectInfo.class, FileInfo.class));
            for (Method method : methods.values())
                method.setAccessible(true);
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
    protected final UniqueDeque<InstanceInfo> instances;
    @NotNull
    protected RpcDispatcher dispatcher;

    public ReplicatedData(JChannel channel) throws Exception
    {
        this(channel, (Notifier[]) null);
    }

    public ReplicatedData(JChannel channel, Notifier... notifiers) throws Exception
    {
        if (notifiers != null)
            this.notifiers.addAll(Arrays.asList(notifiers));

        this.instances = new UniqueLinkedDeque<>();

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
    public void setTimeout(long timeout)
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
    public UniqueDeque<InstanceInfo> getInstances()
    {
        return new UniqueLinkedDeque<>(instances);
    }

    public void addInstance(InstanceInfo instance)
    {
        try
        {
            MethodCall call = new MethodCall(ADD_INSTANCE, instance);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("addInstance(" + instance + ") failed", e);
        }
    }

    public void removeInstance(InstanceInfo instance)
    {
        try
        {
            MethodCall call = new MethodCall(REMOVE_INSTANCE, instance);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("removeInstance(" + instance + ") failed", e);
        }
    }

    public void addProject(InstanceInfo instance, ProjectInfo project)
    {
        try
        {
            MethodCall call = new MethodCall(ADD_PROJECT, instance, project);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("addProject(" + instance + ", " + project + ") failed", e);
        }
    }

    public void removeProject(InstanceInfo instance, ProjectInfo project)
    {
        try
        {
            MethodCall call = new MethodCall(REMOVE_PROJECT, instance, project);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("removeProject(" + instance + ", " + project + ") failed", e);
        }
    }

    public void addFile(InstanceInfo instance, ProjectInfo project, FileInfo file)
    {
        try
        {
            MethodCall call = new MethodCall(ADD_FILE, instance, project, file);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("addFile(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    public void removeFile(InstanceInfo instance, ProjectInfo project, FileInfo file)
    {
        try
        {
            MethodCall call = new MethodCall(REMOVE_FILE, instance, project, file);
            this.dispatcher.callRemoteMethods(null, call, call_options);
        }
        catch (Exception e)
        {
            throw new RuntimeException("removeFile(" + instance + ", " + project + ", " + file + ") failed", e);
        }
    }

    /*------------------------ Callbacks -----------------------*/

    protected InstanceInfo _addInstance(InstanceInfo instance)
    {
        RPC.setPresenceDelay(10, TimeUnit.SECONDS);

        this.instances.addFirst(instance);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.instanceAdded(instance);

        return instance;
    }

    protected InstanceInfo _removeInstance(InstanceInfo instance)
    {
        this.instances.remove(instance);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.instanceRemoved(instance);

        return instance;
    }

    protected ProjectInfo _addProject(InstanceInfo instance, ProjectInfo project)
    {
        RPC.setPresenceDelay(5, TimeUnit.SECONDS);

        getLocalObject(instance).projects.addFirst(project);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.projectAdded(instance, project);

        return project;
    }

    protected ProjectInfo _removeProject(InstanceInfo instance, ProjectInfo project)
    {
        getLocalObject(instance).projects.remove(project);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.projectRemoved(instance, project);

        return project;
    }

    protected FileInfo _addFile(InstanceInfo instance, ProjectInfo project, FileInfo file)
    {
        RPC.setPresenceDelay(2, TimeUnit.SECONDS);

        getLocalObject(instance, project).files.addFirst(file);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.fileAdded(instance, project, file);

        return file;
    }

    protected FileInfo _removeFile(InstanceInfo instance, ProjectInfo project, FileInfo file)
    {
        getLocalObject(instance, project).files.remove(file);

        for (ReplicatedData.Notifier notifier : notifiers)
            notifier.fileRemoved(instance, project, file);

        return file;
    }

    /*------------- Conversion To Local Objects ---------------*/

    private InstanceInfo getLocalObject(InstanceInfo instance)
    {
        for (InstanceInfo info : instances)
        {
            if (instance.equals(info))
                return info;
        }
        return _addInstance(instance);
    }

    private ProjectInfo getLocalObject(InstanceInfo instance, ProjectInfo project)
    {
        instance = getLocalObject(instance);

        for (ProjectInfo info : instance.projects)
        {
            if (project.equals(info))
                return info;
        }
        return _addProject(instance, project);
    }

    /*-------------------- State Exchange ----------------------*/

    public void getState(OutputStream outputStream) throws Exception
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(outputStream, 1024)))
        {
            oos.writeObject(instances.stream().map(InstanceInfo::clone).collect(Collectors.toCollection(UniqueLinkedDeque::new)));
        }

    }

    /*------------------- Membership Changes ----------------------*/

    public void viewAccepted(View view)
    {
        List<Integer> ids = view.getMembers().stream().map(Address::hashCode).collect(Collectors.toList());

        instances.stream().filter(i -> !ids.contains(i.getId())).forEach(this::_removeInstance);
    }

    @SuppressWarnings("unchecked")
    public void setState(InputStream inputStream) throws Exception
    {
        UniqueDeque<InstanceInfo> newInstances;
        try (ObjectInputStream ois = new ObjectInputStream(inputStream))
        {
            newInstances = (UniqueDeque<InstanceInfo>) ois.readObject();
        }
        if (newInstances != null)
            for (InstanceInfo instance : newInstances)
                addInstance(instance);
    }

    public interface Notifier
    {
        default void instanceAdded(InstanceInfo instance) {}

        default void instanceRemoved(InstanceInfo instance) {}

        default void projectAdded(InstanceInfo instance, ProjectInfo project) {}

        default void projectRemoved(InstanceInfo instance, ProjectInfo project) {}

        default void fileAdded(InstanceInfo instance, ProjectInfo project, FileInfo file) {}

        default void fileRemoved(InstanceInfo instance, ProjectInfo project, FileInfo file) {}

    }

    public interface UpdateNotifier extends Notifier
    {
        default void instanceAdded(InstanceInfo instance)
        {
            this.dataUpdated();
        }

        default void instanceRemoved(InstanceInfo instance)
        {
            this.dataUpdated();
        }

        default void projectAdded(InstanceInfo instance, ProjectInfo project)
        {
            this.dataUpdated();
        }

        default void projectRemoved(InstanceInfo instance, ProjectInfo project)
        {
            this.dataUpdated();
        }

        default void fileAdded(InstanceInfo instance, ProjectInfo project, FileInfo file)
        {
            this.dataUpdated();
        }

        default void fileRemoved(InstanceInfo instance, ProjectInfo project, FileInfo file)
        {
            this.dataUpdated();
        }

        void dataUpdated();

    }
}
