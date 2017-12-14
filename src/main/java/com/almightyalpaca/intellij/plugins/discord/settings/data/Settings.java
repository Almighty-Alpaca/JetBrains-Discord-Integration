package com.almightyalpaca.intellij.plugins.discord.settings.data;

import java.io.Serializable;

public interface Settings<T extends Settings<T>> extends Serializable, Cloneable
{
    boolean isEnabled();

    T clone();
}
