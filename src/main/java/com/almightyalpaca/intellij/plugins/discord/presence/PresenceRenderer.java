package com.almightyalpaca.intellij.plugins.discord.presence;

import club.minnced.discord.rpc.DiscordRichPresence;
import com.almightyalpaca.intellij.plugins.discord.data.FileInfo;
import com.almightyalpaca.intellij.plugins.discord.data.InstanceInfo;
import com.almightyalpaca.intellij.plugins.discord.data.ProjectInfo;

import java.util.function.Function;

public class PresenceRenderer implements Function<PresenceRenderContext, DiscordRichPresence>
{
    @Override
    public DiscordRichPresence apply(PresenceRenderContext context)
    {
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
