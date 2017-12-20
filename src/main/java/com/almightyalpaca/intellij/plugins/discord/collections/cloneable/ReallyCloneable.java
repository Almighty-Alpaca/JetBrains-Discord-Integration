package com.almightyalpaca.intellij.plugins.discord.collections.cloneable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ReallyCloneable<T> extends Cloneable
{
    @Contract(value = "null -> null; !null -> !null")
    static <T extends ReallyCloneable<T>> T clone(T t)
    {
        return t == null ? null : t.clone();
    }

    @NotNull
    T clone();
}
