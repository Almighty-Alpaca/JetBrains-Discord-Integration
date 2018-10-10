package com.almightyalpaca.jetbrains.plugins.discord.utils;

public class Pair<L, R>
{
    public final L left;
    public final R right;

    public Pair(final L left, final R right)
    {
        super();
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right)
    {
        return new Pair<>(left, right);
    }

    public L getLeft()
    {
        return left;
    }

    public R getRight()
    {
        return right;
    }
}
