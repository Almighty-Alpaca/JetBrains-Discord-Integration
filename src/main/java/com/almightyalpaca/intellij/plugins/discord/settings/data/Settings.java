package com.almightyalpaca.intellij.plugins.discord.settings.data;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;

import java.io.Serializable;

public interface Settings<T extends Settings<T>> extends Serializable, ReallyCloneable<T>
{
    boolean isEnabled();
}
