package com.almightyalpaca.intellij.plugins.discord.collections;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class UniqueLinkedDeque<E> implements UniqueDeque<E>, Serializable
{
    private final LinkedList<E> list;

    public UniqueLinkedDeque()
    {
        this.list = new LinkedList<>();
    }

    public UniqueLinkedDeque(UniqueDeque<E> deque)
    {
        this.list = new LinkedList<>(deque);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(E e)
    {
        this.list.remove(e);

        this.list.addFirst(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLast(E e)
    {
        this.list.remove(e);

        this.list.addLast(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offerFirst(E e)
    {
        this.list.remove(e);

        return this.list.offerFirst(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offerLast(E e)
    {
        this.list.remove(e);

        return this.list.offerLast(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeFirst()
    {
        return this.list.removeFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeLast()
    {
        return this.list.removeLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pollFirst()
    {
        return this.list.pollFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pollLast()
    {
        return this.list.pollLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getFirst()
    {
        return this.list.getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getLast()
    {
        return this.list.getLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekFirst()
    {
        return this.list.peekFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekLast()
    {
        return this.list.peekLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFirstOccurrence(Object o)
    {
        return this.list.removeFirstOccurrence(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeLastOccurrence(Object o)
    {
        return this.list.removeLastOccurrence(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e)
    {
        this.list.remove(e);

        return this.list.add(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offer(E e)
    {
        this.list.remove(e);

        return this.list.offer(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove()
    {
        return this.list.remove();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E poll()
    {
        return this.list.poll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E element()
    {
        return this.list.element();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peek()
    {
        return this.list.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends E> c)
    {
        this.list.removeAll(c);

        return this.list.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void push(E e)
    {
        this.list.remove(e);

        this.list.push(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pop()
    {
        return this.list.pop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o)
    {
        return this.list.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o)
    {
        return this.list.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return this.list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Iterator<E> iterator()
    {
        return this.list.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<E> descendingIterator()
    {
        return this.list.descendingIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Object[] toArray()
    {
        return this.list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SuspiciousToArrayCall")
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a)
    {
        return this.list.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(@NotNull Collection<?> c)
    {
        return this.list.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> c)
    {
        return this.list.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(@NotNull Collection<?> c)
    {
        return this.list.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        this.list.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o)
    {
        return o instanceof UniqueLinkedDeque && this.list.equals(((UniqueLinkedDeque) o).list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.list.hashCode();
    }

    @Override
    public String toString()
    {
        return list.toString();
    }
}
