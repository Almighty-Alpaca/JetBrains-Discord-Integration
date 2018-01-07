package com.almightyalpaca.jetbrains.plugins.discord.utils;

@FunctionalInterface
public interface LazyString
{
    static LazyString of(LazyString string)
    {
        return string;
    }

    String getString() throws Exception;
}
