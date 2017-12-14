package com.almightyalpaca.intellij.plugins.discord.settings;

public interface SettingsProvider<T>
{
    T getSettings();
}
