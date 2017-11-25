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
package com.almightyalpaca.intellij.plugins.discord.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class DiscordIntegrationErrorNotification extends DiscordIntegrationNotification
{
    public static final String GROUP_DISPLAY_ID = "Discord Integration RPC error";
    public static final String TITLE = "Plugins received an unexpected RPC error";
    public static final NotificationType TYPE = NotificationType.ERROR;

    public DiscordIntegrationErrorNotification(int code, String text)
    {
        super(GROUP_DISPLAY_ID, TITLE, null, "The plugin has received an unexpected RPC error.\nCode: " + code + " / " + text, TYPE);
    }
}
