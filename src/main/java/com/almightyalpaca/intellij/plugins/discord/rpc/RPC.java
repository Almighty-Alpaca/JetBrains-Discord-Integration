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

@SuppressWarnings("ConstantConditions")
public class RPC
{
    @Nullable
    private static volatile Thread callbackRunner;
    @Nullable
    private static volatile DiscordRichPresence presence;
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
        }
    }

    public static synchronized void dispose()
    {

        if (RPC.initialized)
        {
            initialized = false;

            DiscordRPC.INSTANCE.Discord_Shutdown();
            callbackRunner.interrupt();
            callbackRunner = null;
        }
    }

    @NotNull
    public static DiscordRichPresence getRichPresence()
    {

        DiscordRichPresence presence = RPC.presence;

        return presence == null ? new DiscordRichPresence() : presence;
    }

    public static synchronized void setRichPresence(@NotNull DiscordRichPresence presence)
    {

        if (RPC.initialized)
        {

            DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
        }
        RPC.presence = presence;
    }
}
