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
package com.almightyalpaca.jetbrains.plugins.discord.notifications;

import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Notification extends com.intellij.notification.Notification
{
    public static final Icon ICON = IconLoader.getIcon("/icons/discord/logo/blurple.png");

    public Notification(@NotNull String groupDisplayId, @Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type)
    {
        this(groupDisplayId, title, subtitle, content, type, null);
    }

    public Notification(@NotNull String groupDisplayId, @Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type, @Nullable NotificationListener listener)
    {
        super(groupDisplayId, ICON, title, subtitle, content, type, listener);
    }
}
