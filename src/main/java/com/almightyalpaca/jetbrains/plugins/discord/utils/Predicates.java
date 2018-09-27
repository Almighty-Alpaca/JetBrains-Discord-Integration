package com.almightyalpaca.jetbrains.plugins.discord.utils;

import java.util.function.Predicate;

public class Predicates
{
    public static <T> Predicate<T> negate(Predicate<T> predicate)
    {
        return predicate.negate();
    }
}
