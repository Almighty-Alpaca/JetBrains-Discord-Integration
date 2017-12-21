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
package com.almightyalpaca.intellij.plugins.discord.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings("ConstantConditions")
public class RPC
{
    @Nullable
    private static volatile Thread callbackRunner;
    @Nullable
    private static volatile Thread delayedPresenceRunner;
    private static volatile long nextPresenceUpdate = Long.MAX_VALUE;
    private static volatile long presenceDelay = 0;

    @Nullable
    private static volatile DiscordRichPresence lastPresence;
    @Nullable
    private static volatile DiscordRichPresence nextPresence;
    private static volatile boolean initialized = false;

    private RPC() {}

    public static synchronized void init(@NotNull DiscordEventHandlers handlers, @NotNull String clientId)
    {
        if (!RPC.initialized)
        {
            RPC.initialized = true;

            DiscordRPC.INSTANCE.Discord_Initialize(clientId, handlers, true, null);

            callbackRunner = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted())
                {
                    try
                    {
                        DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException ignored) {}
                }
            }, "RPC-Callback-Handler");

            callbackRunner.start();

            delayedPresenceRunner = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted())
                {
                    try
                    {
                        long timeout = RPC.nextPresenceUpdate - System.nanoTime();

                        if (timeout > 0)
                        {
                            LockSupport.parkNanos(timeout);
                        } else
                        {
                            synchronized (RPC.class)
                            {
                                if (!Objects.equals(lastPresence, nextPresence))
                                    DiscordRPC.INSTANCE.Discord_UpdatePresence(nextPresence);

                                lastPresence = nextPresence;
                            }

                            LockSupport.park();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }, "RPC-Delayed-Presence-Handler");

            delayedPresenceRunner.start();
        }
    }

    public static synchronized void dispose()
    {
        if (RPC.initialized)
        {
            initialized = false;

            delayedPresenceRunner.interrupt();
            delayedPresenceRunner = null;

            DiscordRPC.INSTANCE.Discord_Shutdown();

            callbackRunner.interrupt();
            callbackRunner = null;
        }
    }

    public static synchronized void setRichPresence(@NotNull DiscordRichPresence presence)
    {
        RPC.nextPresence = presence;

        updatePresence();
    }

    private static synchronized void updatePresence()
    {
        nextPresenceUpdate = System.nanoTime() + presenceDelay;

        LockSupport.unpark(delayedPresenceRunner);
    }

    public static long getPresenceDelay(@NotNull TimeUnit unit)
    {
        return TimeUnit.NANOSECONDS.convert(presenceDelay, unit);
    }

    public static synchronized void setPresenceDelay(long presenceDelay, @NotNull TimeUnit unit)
    {
        RPC.presenceDelay = unit.toNanos(presenceDelay);
    }
}
