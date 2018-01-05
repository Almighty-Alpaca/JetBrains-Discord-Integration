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
package com.almightyalpaca.jetbrains.plugins.discord.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.almightyalpaca.jetbrains.plugins.discord.presence.PresenceRenderContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class RPC
{
    @NotNull
    private static final Gson GSON;
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(RPC.class);
    @Nullable
    private static volatile Thread callbackRunner;
    @Nullable
    private static volatile Thread delayedPresenceRunner;
    @Nullable
    private static volatile DiscordRichPresence presence;
    private static volatile long nextPresenceUpdate = 0L;
    private static volatile boolean initialized = false;

    static
    {
        GSON = new GsonBuilder().registerTypeAdapter(DiscordRichPresence.class, new DiscordRichPresenceGsonTypeAdapter()).create();
    }

    private RPC() {}

    public static synchronized void init(@NotNull DiscordEventHandlers handlers, @NotNull String clientId, @NotNull Supplier<PresenceRenderContext> contextSupplier, @NotNull Function<PresenceRenderContext, DiscordRichPresence> renderer)
    {
        LOG.trace("RPC.init({}, {}, {}, {})", "DiscordEventHandler", clientId, contextSupplier, renderer);

        if (!RPC.initialized)
        {
            RPC.initialized = true;

            DiscordRPC.INSTANCE.Discord_Initialize(clientId, handlers, true, null);

            RPC.updatePresence(1, TimeUnit.SECONDS);

            if (callbackRunner == null)
            {
                RPC.callbackRunner = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted())
                    {
                        try
                        {
                            DiscordRPC.INSTANCE.Discord_RunCallbacks();

                            LockSupport.parkNanos(2_000_000);
                        }
                        catch (Exception e)
                        {
                            if (!(e instanceof InterruptedException))
                                LOG.warn("An error occurred in RPC.callbackRunner", e);
                        }
                    }
                }, "RPC-Callback-Handler");

                RPC.callbackRunner.start();
            }

            if (delayedPresenceRunner == null)
            {
                RPC.delayedPresenceRunner = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted())
                    {
                        try
                        {
                            long timeout = RPC.nextPresenceUpdate - System.nanoTime();

                            LOG.trace("RPC.delayedPresenceRunner$run#timeout = {}", timeout);

                            if (timeout > 0)
                            {
                                LockSupport.parkNanos(timeout);
                            }
                            else
                            {
                                DiscordRichPresence newPresence = renderer.apply(contextSupplier.get());

                                LOG.trace("RPC.delayedPresenceRunner$run#newPresence = {}", GSON.toJson(newPresence));

                                if (!Objects.equals(RPC.presence, newPresence))
                                {
                                    DiscordRPC.INSTANCE.Discord_UpdatePresence(newPresence);
                                    RPC.presence = newPresence;
                                }
                            }

                            LockSupport.park();
                        }
                        catch (Exception e)
                        {
                            if (!(e instanceof InterruptedException))
                                LOG.warn("An error occurred in RPC.delayedPresenceRunner", e);
                        }
                    }
                }, "RPC-Delayed-Presence-Handler");

                RPC.delayedPresenceRunner.start();
            }
        }
    }

    public static synchronized void dispose()
    {
        LOG.trace("RPC.dispose()");

        RPC.delayedPresenceRunner.interrupt();
        RPC.delayedPresenceRunner = null;

        DiscordRPC.INSTANCE.Discord_UpdatePresence(new DiscordRichPresence());

        DiscordRPC.INSTANCE.Discord_Shutdown();

        RPC.callbackRunner.interrupt();
        RPC.callbackRunner = null;
    }

    public static synchronized void updatePresence(long delay, @NotNull TimeUnit unit)
    {
        LOG.trace("RPC.updatePresence({}, {})", delay, unit);

        RPC.nextPresenceUpdate = System.nanoTime() + unit.convert(delay, TimeUnit.NANOSECONDS);

        LockSupport.unpark(RPC.delayedPresenceRunner);
    }
}
