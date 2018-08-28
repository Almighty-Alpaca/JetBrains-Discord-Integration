package com.almightyalpaca.jetbrains.plugins.discord.utils;

import com.almightyalpaca.jetbrains.plugins.discord.notifications.DiscordIntegrationErrorNotification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil
{

    @NotNull
    public static String readFirstLine(@NotNull VirtualFile file)
    {
        try
        {
            String line = Files.newBufferedReader(Paths.get(file.getPath()), StandardCharsets.UTF_8).readLine();

            if (line != null)
                if (line.length() > 512)
                    return line.substring(0, 512);
                else
                    return line;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            Notifications.Bus.notify(new DiscordIntegrationErrorNotification(e.getMessage()));
        }

        return "";
    }
}
