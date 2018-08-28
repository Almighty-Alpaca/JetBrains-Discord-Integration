package com.almightyalpaca.jetbrains.plugins.discord.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R>
{
    default R apply(T t)
    {
        try
        {
            return applyThrowing(t);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @throws Exception abitrary Exception
     * @return the function result
     */
    R applyThrowing(T t) throws Exception;
}
