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
import com.almightyalpaca.jetbrains.plugins.discord.themes.Icon;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Theme;
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

        if (context.getInstance() == null)
            return null;

        InstanceInfo instance = context.getInstance();
        ProjectInfo project = context.getProject();
        FileInfo file = context.getFile();

        DiscordRichPresence presence = new DiscordRichPresence();

        if (instance != null)
        {
            InstanceInfo.DistributionInfo distribution = instance.getDistribution();

            Theme theme = instance.getSettings().getTheme();

            if (project != null)
            {
                presence.details = "Working on " + project.getName();

                if (instance.getSettings().isShowElapsedTime())
                    presence.startTimestamp = project.getTimeOpened() / 1000;

                if (!project.getSettings().getDescription().isEmpty())
                {
                    presence.state = project.getSettings().getDescription();
                }
                else if (file != null && instance.getSettings().isShowFiles())
                {
                    presence.state =
                            (file.isReadOnly() && instance.getSettings().isShowReadingInsteadOfWriting() ? "Reading " : "Editing ") +
                            (instance.getSettings().isShowFileExtensions() ? file.getName() : file.getBaseName());

                    Icon language = theme.matchLanguage(file.getName(), file.getContent());

                    if (language == null)
                        if (instance.getSettings().isShowUnknownImageFile())
                            language = Icon.UNKNOWN;
                        else
                            language = Icon.EMPTY;


                    Icon ide = theme.matchApplication(distribution.getCode());

                    if (ide == null)
                        if (instance.getSettings().isShowUnknownImageIDE())
                            ide = Icon.UNKNOWN;
                        else
                            ide = Icon.EMPTY;

                    if (language.equals(Icon.EMPTY) || instance.getSettings().isForceBigIDEIcon())
                    {
                        presence.largeImageKey = ide.getAssetKey();
                        presence.largeImageText = ide.getName();

                        presence.smallImageKey = language.getAssetKey();
                        presence.smallImageText = language.getName();
                    }
                    else
                    {
                        presence.largeImageKey = language.getAssetKey();
                        presence.largeImageText = language.getName();

                        presence.smallImageKey = ide.getAssetKey();
                        presence.smallImageText = ide.getName();
                    }
                }
            }

            if (presence.largeImageKey == null || presence.largeImageKey.isEmpty())
            {
                Icon ide = theme.matchApplication(distribution.getCode());

                if (ide == null)
                    if (instance.getSettings().isShowUnknownImageIDE())
                        ide = Icon.UNKNOWN;
                    else
                        ide = Icon.EMPTY;

                presence.largeImageKey = ide.getAssetKey();
                presence.largeImageText = ide.getName();

                presence.smallImageKey = null;
                presence.smallImageText = null;
            }
        }

        return presence;
    }
}
