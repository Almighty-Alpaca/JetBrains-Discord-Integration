package com.almightyalpaca.jetbrains.plugins.discord.utils;

import java.util.Objects;
import java.util.function.Supplier;

public class LazyString
{
    private final Supplier<?> string;

    public LazyString(Supplier<?> string) {this.string = string;}

    public static LazyString of(Supplier<?> string)
    {
        return new LazyString(string);
    }

    @Override
    public String toString()
    {
        return Objects.toString(string);
    }
}
