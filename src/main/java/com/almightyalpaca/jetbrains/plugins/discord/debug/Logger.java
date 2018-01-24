package com.almightyalpaca.jetbrains.plugins.discord.debug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

import java.util.Objects;
import java.util.function.Supplier;

public class Logger
{
    @NotNull
    private final String name;
    @NotNull
    private final com.intellij.openapi.diagnostic.Logger delegate;

    public Logger(@NotNull String name)
    {
        this.name = name;

        this.delegate = com.intellij.openapi.diagnostic.Logger.getInstance(name);
    }

    public Logger(@NotNull Class<?> clazz)
    {
        this(clazz.getName());
    }

    public void trace(@Nullable String s, @Nullable Object... props)
    {
        if (this.delegate.isTraceEnabled() || Debug.isEnabled())
        {
            String message = MessageFormatter.arrayFormat(s, props).getMessage();

            this.delegate.trace(message);
            Debug.log(name, Debug.Level.TRACE, message);
        }
    }

    public void debug(@Nullable String s, @Nullable Object... props)
    {
        if (this.delegate.isDebugEnabled() || Debug.isEnabled())
        {
            String message = MessageFormatter.arrayFormat(s, props).getMessage();

            this.delegate.debug(message);
            Debug.log(name, Debug.Level.DEBUG, message);
        }
    }

    public void warn(@Nullable String s, @Nullable Object... props)
    {
        String message = MessageFormatter.arrayFormat(s, props).getMessage();

        this.delegate.warn(message);
        Debug.log(name, Debug.Level.WARN, message);
    }

    public void error(@Nullable String s, @Nullable Object... props)
    {
        String message = MessageFormatter.arrayFormat(s, props).getMessage();

        this.delegate.error(message);
        Debug.log(name, Debug.Level.ERROR, message);
    }

    public void info(@Nullable String s, @Nullable Object... props)
    {
        String message = MessageFormatter.arrayFormat(s, props).getMessage();

        this.delegate.info(message);
        Debug.log(name, Debug.Level.INFO, message);
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    public static class LazyString
    {
        @Nullable
        private final Supplier<?> supplier;

        public LazyString(@Nullable Supplier<?> supplier) {this.supplier = supplier;}

        public static LazyString create(Supplier<?> supplier)
        {
            return new LazyString(supplier);
        }

        @NotNull
        @Override
        public String toString()
        {
            return Objects.toString(supplier == null ? null : supplier.get());
        }
    }
}


