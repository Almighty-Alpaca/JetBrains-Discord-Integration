package com.almightyalpaca.jetbrains.plugins.discord.debug;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoggerFactory
{
    private static final HashMap<String, Logger> LOGGERS = new HashMap<>();

    public static Logger getLogger(@NotNull Class<?> clazz)
    {
        return getLogger(clazz.getName());
    }

    private static Logger getLogger(@NotNull String name)
    {
        return LOGGERS.computeIfAbsent(name, Logger::new);
    }
}
