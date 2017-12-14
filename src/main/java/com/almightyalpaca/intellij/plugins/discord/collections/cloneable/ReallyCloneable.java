package com.almightyalpaca.intellij.plugins.discord.collections.cloneable;

import org.jetbrains.annotations.Nullable;

public interface ReallyCloneable<T> extends Cloneable
{
    @Nullable
    static <T extends ReallyCloneable<T>> T clone(@Nullable T t)
    {
        return t == null ? null : t.clone();
    }

    T clone();
}
