package com.almightyalpaca.intellij.plugins.discord.settings.data;

public interface ApplicationSettings<T extends ApplicationSettings<T>> extends Settings<T>
{
    boolean isShowFileExtensions();

    boolean isShowUnknownImageIDE();

    boolean isShowUnknownImageFile();

    boolean isHideReadOnlyFiles();
}
