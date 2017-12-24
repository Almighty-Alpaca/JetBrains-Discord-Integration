package com.almightyalpaca.intellij.plugins.discord.settings;

import org.jetbrains.annotations.NotNull;

public interface SettingsProvider<T>
{
    @NotNull
    T getSettings();
}
