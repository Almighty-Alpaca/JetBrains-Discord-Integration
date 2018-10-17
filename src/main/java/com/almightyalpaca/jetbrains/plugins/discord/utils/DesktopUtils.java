package com.almightyalpaca.jetbrains.plugins.discord.utils;

import com.almightyalpaca.jetbrains.plugins.discord.notifications.ErrorNotification;
import com.intellij.notification.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class DesktopUtils
{
    public static void openLinkInBrowser(String link)
    {
        try
        {
            Desktop.getDesktop().browse(URI.create(link));
        }
        catch (IOException e)
        {
            e.printStackTrace();

            Notifications.Bus.notify(new ErrorNotification("Failed to open link", e.getMessage()));
        }
    }
}
