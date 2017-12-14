package com.almightyalpaca.intellij.plugins.discord.collections.cloneable;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "SuspiciousToArrayCall", "SuspiciousSystemArraycopy", "unused"})
public class CloneableCollections
{

    public static <K, V> CloneableMap<K, V> unmodifiableCloneableMap(CloneableMap<K, V> m)
    {
        return new CloneableCollections.UnmodifiableCloneableMap<>(m);
    }

    private static class UnmodifiableCloneableMap<K, V> implements CloneableMap<K, V>, Serializable
    {
        private static final long serialVersionUID = -1034234728574286014L;

        private final CloneableMap<K, V> m;
        private transient Set<K> keySet;
        private transient Set<Map.Entry<K, V>> entrySet;
        private transient Collection<V> values;

        UnmodifiableCloneableMap(CloneableMap<K, V> m)
        {
            if (m == null)
                throw new NullPointerException();
            this.m = m;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @NotNull
        @Override
        public CloneableMap<K, V> clone()
        {
            return new UnmodifiableCloneableMap<>(m.clone());
        }

        public int size() {return m.size();}

        public boolean isEmpty() {return m.isEmpty();}

        public boolean containsKey(Object key) {return m.containsKey(key);}

        public boolean containsValue(Object val) {return m.containsValue(val);}

        public V get(Object key) {return m.get(key);}

        public V put(K key, V value)
        {
            throw new UnsupportedOperationException();
        }

        public V remove(Object key)
        {
            throw new UnsupportedOperationException();
        }

        public void putAll(@NotNull Map<? extends K, ? extends V> m)
        {
            throw new UnsupportedOperationException();
        }

        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        @NotNull
        public Set<K> keySet()
        {
            if (keySet == null)
                keySet = new UnmodifiableSet<>(m.keySet());
            return keySet;
        }

        @NotNull
        public Set<Map.Entry<K, V>> entrySet()
        {
            if (entrySet == null)
                entrySet = new UnmodifiableSet<>(m.entrySet());
            return entrySet;
        }

        @NotNull
        public Collection<V> values()
        {
            if (values == null)
                values = new UnmodifiableCollection<>(m.values());
            return values;
        }

        public boolean equals(Object o) {return o == this || m.equals(o);}

        public int hashCode() {return m.hashCode();}

        public String toString() {return m.toString();}

        // Override default methods in Map
        @Override
        @SuppressWarnings("unchecked")
        public V getOrDefault(Object k, V defaultValue)
        {
            // Safe cast as we don't change the value
            return m.getOrDefault(k, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action)
        {
            m.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(K key, V value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, Object value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(K key, V value)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
        {
            throw new UnsupportedOperationException();
        }

        /**
         * We need this class in addition to UnmodifiableSet as
         * Map.Entries themselves permit modification of the backing Map
         * via their setValue operation.  This class is subtle: there are
         * many possible attacks that must be thwarted.
         *
         * @serial include
         */
        static class UnmodifiableEntrySet<K, V> extends CloneableCollections.UnmodifiableSet<Entry<K, V>>
        {
            private static final long serialVersionUID = 7854390611657943733L;

            @SuppressWarnings({"unchecked", "rawtypes"})
            UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s)
            {
                // Need to cast to raw in order to work around a limitation in the type system
                super((Set) s);
            }

            static <K, V> Consumer<Entry<K, V>> entryConsumer(Consumer<? super Entry<K, V>> action)
            {
                return e -> action.accept(new UnmodifiableEntry<>(e));
            }

            public void forEach(Consumer<? super Entry<K, V>> action)
            {
                Objects.requireNonNull(action);
                c.forEach(entryConsumer(action));
            }

            @SuppressWarnings("unchecked")
            public Spliterator<Entry<K, V>> spliterator()
            {
                return new UnmodifiableEntrySetSpliterator<>((Spliterator<Map.Entry<K, V>>) c.spliterator());
            }

            @Override
            public Stream<Entry<K, V>> stream()
            {
                return StreamSupport.stream(spliterator(), false);
            }

            @Override
            public Stream<Entry<K, V>> parallelStream()
            {
                return StreamSupport.stream(spliterator(), true);
            }

            @NotNull
            public Iterator<Map.Entry<K, V>> iterator()
            {
                return new Iterator<Map.Entry<K, V>>()
                {
                    private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();

                    public boolean hasNext()
                    {
                        return i.hasNext();
                    }

                    public Map.Entry<K, V> next()
                    {
                        return new UnmodifiableEntry<>(i.next());
                    }

                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @NotNull
            @SuppressWarnings("unchecked")
            public Object[] toArray()
            {
                Object[] a = c.toArray();
                for (int i = 0; i < a.length; i++)
                    a[i] = new UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) a[i]);
                return a;
            }

            @NotNull
            @SuppressWarnings("unchecked")
            public <T> T[] toArray(@NotNull T[] a)
            {
                // We don't pass a to c.toArray, to avoid window of
                // vulnerability wherein an unscrupulous multithreaded client
                // could get his hands on raw (unwrapped) Entries from c.
                Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));

                for (int i = 0; i < arr.length; i++)
                    arr[i] = new UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) arr[i]);

                if (arr.length > a.length)
                    return (T[]) arr;

                System.arraycopy(arr, 0, a, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
                return a;
            }

            /**
             * This method is overridden to protect the backing set against
             * an object with a nefarious equals function that senses
             * that the equality-candidate is Map.Entry and calls its
             * setValue method.
             */
            public boolean contains(Object o)
            {
                return o instanceof Map.Entry && c.contains(new UnmodifiableEntry<>((Entry<?, ?>) o));
            }

            /**
             * The next two methods are overridden to protect against
             * an unscrupulous List whose contains(Object o) method senses
             * when o is a Map.Entry, and calls o.setValue.
             */
            public boolean containsAll(@NotNull Collection<?> coll)
            {
                for (Object e : coll)
                {
                    if (!contains(e)) // Invokes safe contains() above
                        return false;
                }
                return true;
            }

            public boolean equals(Object o)
            {
                if (o == this)
                    return true;

                if (!(o instanceof Set))
                    return false;
                Set<?> s = (Set<?>) o;
                return s.size() == c.size() && containsAll(s);
            }

            static final class UnmodifiableEntrySetSpliterator<K, V> implements Spliterator<Entry<K, V>>
            {
                final Spliterator<Map.Entry<K, V>> s;

                UnmodifiableEntrySetSpliterator(Spliterator<Entry<K, V>> s)
                {
                    this.s = s;
                }

                @Override
                public boolean tryAdvance(Consumer<? super Entry<K, V>> action)
                {
                    Objects.requireNonNull(action);
                    return s.tryAdvance(entryConsumer(action));
                }

                @Override
                public void forEachRemaining(Consumer<? super Entry<K, V>> action)
                {
                    Objects.requireNonNull(action);
                    s.forEachRemaining(entryConsumer(action));
                }

                @Override
                public Spliterator<Entry<K, V>> trySplit()
                {
                    Spliterator<Entry<K, V>> split = s.trySplit();
                    return split == null ? null : new UnmodifiableEntrySetSpliterator<>(split);
                }

                @Override
                public long estimateSize()
                {
                    return s.estimateSize();
                }

                @Override
                public long getExactSizeIfKnown()
                {
                    return s.getExactSizeIfKnown();
                }

                @Override
                public int characteristics()
                {
                    return s.characteristics();
                }

                @Override
                public boolean hasCharacteristics(int characteristics)
                {
                    return s.hasCharacteristics(characteristics);
                }

                @Override
                public Comparator<? super Entry<K, V>> getComparator()
                {
                    return s.getComparator();
                }
            }

            /**
             * This "wrapper class" serves two purposes: it prevents
             * the client from modifying the backing Map, by short-circuiting
             * the setValue method, and it protects the backing Map against
             * an ill-behaved Map.Entry that attempts to modify another
             * Map Entry when asked to perform an equality check.
             */
            private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V>
            {
                private final Map.Entry<? extends K, ? extends V> e;

                UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e)
                {this.e = Objects.requireNonNull(e);}

                public K getKey() {return e.getKey();}

                public V getValue() {return e.getValue();}

                public V setValue(V value)
                {
                    throw new UnsupportedOperationException();
                }

                public int hashCode() {return e.hashCode();}

                public boolean equals(Object o)
                {
                    if (this == o)
                        return true;
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<?, ?> t = (Map.Entry<?, ?>) o;
                    return Objects.equals(e.getKey(), t.getKey()) && Objects.equals(e.getValue(), t.getValue());
                }

                public String toString() {return e.toString();}
            }
        }
    }

    /**
     * @serial include
     */
    static class UnmodifiableSet<E> extends CloneableCollections.UnmodifiableCollection<E> implements Set<E>, Serializable
    {
        private static final long serialVersionUID = -9215047833775013803L;

        UnmodifiableSet(Set<? extends E> s) {super(s);}

        public boolean equals(Object o) {return o == this || c.equals(o);}

        public int hashCode() {return c.hashCode();}
    }

    /**
     * @serial include
     */
    static class UnmodifiableCollection<E> implements Collection<E>, Serializable
    {
        private static final long serialVersionUID = 1820017752578914078L;

        final Collection<? extends E> c;

        UnmodifiableCollection(Collection<? extends E> c)
        {
            if (c == null)
                throw new NullPointerException();
            this.c = c;
        }

        public int size() {return c.size();}

        public boolean isEmpty() {return c.isEmpty();}

        public boolean contains(Object o) {return c.contains(o);}

        @NotNull
        public Object[] toArray() {return c.toArray();}

        @NotNull
        public <T> T[] toArray(@NotNull T[] a) {return c.toArray(a);}

        public String toString() {return c.toString();}

        @NotNull
        public Iterator<E> iterator()
        {
            return new Iterator<E>()
            {
                private final Iterator<? extends E> i = c.iterator();

                public boolean hasNext() {return i.hasNext();}

                public E next() {return i.next();}

                public void remove()
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action)
                {
                    // Use backing collection version
                    i.forEachRemaining(action);
                }
            };
        }

        public boolean add(E e)
        {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(@NotNull Collection<?> coll)
        {
            return c.containsAll(coll);
        }

        public boolean addAll(@NotNull Collection<? extends E> coll)
        {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(@NotNull Collection<?> coll)
        {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(@NotNull Collection<?> coll)
        {
            throw new UnsupportedOperationException();
        }

        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action)
        {
            c.forEach(action);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter)
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Spliterator<E> spliterator()
        {
            return (Spliterator<E>) c.spliterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> stream()
        {
            return (Stream<E>) c.stream();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> parallelStream()
        {
            return (Stream<E>) c.parallelStream();
        }
    }

}
