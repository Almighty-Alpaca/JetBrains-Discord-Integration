package com.almightyalpaca.jetbrains.plugins.discord.themes;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Icon
{
    @NotNull
    public static final Icon UNKNOWN = new Icon("Unknown", "unknown", Collections.emptyMap());
    @NotNull
    public static final Icon EMPTY = new Icon("", "", Collections.emptyMap());

    @NotNull
    private final String name;
    @NotNull
    private final String assetKey;
    @NotNull
    private final Map<String, Set<Matcher>> matchers;


    public Icon(@NotNull String name, @NotNull String assetKey, @NotNull Map<String, Set<Matcher>> matchers)
    {
        this.name = name;
        this.assetKey = assetKey;

        Map<String, Set<Matcher>> newMatchers = new HashMap<>(matchers);
        newMatchers.replaceAll((k, v) -> Collections.unmodifiableSet(v));
        this.matchers = Collections.unmodifiableMap(newMatchers);
    }

    @NotNull
    public static Icon fromJson(@NotNull JsonObject object)
    {
        final String name = object.get("name").getAsString();
        final String assetKey = object.get("asset").getAsString();

        final JsonObject matcherObject = object.get("matcher").getAsJsonObject();
        Map<String, Set<Matcher>> matchers = matcherObject.entrySet().stream()
                .flatMap(entry -> StreamSupport
                        .stream(entry.getValue().getAsJsonArray().spliterator(), false)
                        .map(jsonElement -> Pair.of(entry.getKey().toLowerCase(), Matcher.fromJson(jsonElement.getAsJsonObject()))))
                .collect(Collectors.groupingBy(
                        Pair::getKey,
                        Collectors.mapping(
                                Pair::getValue,
                                Collectors.toCollection(LinkedHashSet::new))));

        return new Icon(name, assetKey, matchers);
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public Map<String, Set<Matcher>> getMatchers()
    {
        return matchers;
    }

    @NotNull
    @Contract(pure = true)
    public Set<Matcher> getMatchers(@NotNull String key)
    {
        return Optional.ofNullable(getMatchers().get(key.toLowerCase()))
                .orElse(Collections.emptySet());
    }


    @NotNull
    public String getAssetKey()
    {
        return assetKey;
    }

    public boolean matches(@NotNull String key, @NotNull String value)
    {
        return this.getMatchers(key).stream()
                .anyMatch(p -> p.matches(value));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof Icon))
            return false;
        Icon icon = (Icon) o;
        return Objects.equals(getName(), icon.getName()) &&
               Objects.equals(getAssetKey(), icon.getAssetKey());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getName(), getAssetKey());
    }
}
