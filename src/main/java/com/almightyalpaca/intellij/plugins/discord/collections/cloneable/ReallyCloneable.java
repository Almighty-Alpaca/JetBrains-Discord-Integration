package com.almightyalpaca.intellij.plugins.discord.collections.cloneable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReallyCloneable<T> extends Cloneable
{
    @Nullable
    @Contract(value = "null -> null")
    static <T extends ReallyCloneable<T>> T clone(@Nullable T t)
    {
        return t == null ? null : t.clone();
    }

    @NotNull
    T clone();
}
