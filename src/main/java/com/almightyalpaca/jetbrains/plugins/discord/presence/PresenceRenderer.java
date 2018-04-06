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
package com.almightyalpaca.jetbrains.plugins.discord.presence;

import club.minnced.discord.rpc.DiscordRichPresence;
import com.almightyalpaca.jetbrains.plugins.discord.data.FileInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.jetbrains.plugins.discord.data.ProjectInfo;
import com.almightyalpaca.jetbrains.plugins.discord.debug.Logger;
import com.almightyalpaca.jetbrains.plugins.discord.debug.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PresenceRenderer implements Function<PresenceRenderContext, DiscordRichPresence>
{
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(PresenceRenderContext.class);

    @Nullable
    @Override
    public DiscordRichPresence apply(@NotNull PresenceRenderContext context)
    {
        LOG.trace("DiscordRichPresence#apply({})", context);

        if (context.isEmpty())
            return null;

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

                if (project.getSettings().getDescription() != null)
                {
                    presence.state = project.getSettings().getDescription();
                }
                else if (file != null && instance.getSettings().isShowFiles())
                {
                    presence.state = (file.isReadOnly() && instance.getSettings().isShowReadingInsteadOfWriting() ? "Reading " : "Editing ") + (instance.getSettings().isShowFileExtensions() ? file.getName() : file.getBaseName());

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

        return presence;
    }
}
