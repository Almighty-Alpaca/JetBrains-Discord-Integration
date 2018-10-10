package com.almightyalpaca.jetbrains.plugins.discord.utils;

import com.almightyalpaca.jetbrains.plugins.discord.notifications.ErrorNotification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil
{
    @NotNull
    public static String readFirstLine(@NotNull VirtualFile file)
    {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getPath()), StandardCharsets.UTF_8))
        {
            String line = reader.readLine();

            if (line != null)
                if (line.length() > 256)
                    return line.substring(0, 256);
                else
                    return line;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            Notifications.Bus.notify(new ErrorNotification(e.getMessage()));
        }

        return "";
    }
}
