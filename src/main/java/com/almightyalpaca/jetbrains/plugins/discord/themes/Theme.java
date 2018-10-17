package com.almightyalpaca.jetbrains.plugins.discord.themes;

import com.almightyalpaca.jetbrains.plugins.discord.utils.FileType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Theme implements Comparable<Theme>
{
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final String application;
    @NotNull
    private final Set<Icon> icons;

    public Theme(@NotNull String id, @NotNull String name, @NotNull String description, @NotNull String application, @NotNull Set<Icon> icons)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.application = application;
        this.icons = Collections.unmodifiableSet(icons);
    }

    public static Theme fromJson(@NotNull String id, @NotNull JsonObject themeObject)
    {
        final String name = themeObject.get("name").getAsString();
        final String description = themeObject.get("description").getAsString();
        final String application = themeObject.get("application").getAsString();

        final JsonArray iconArray = themeObject.get("icons").getAsJsonArray();

        Set<Icon> icons = StreamSupport.stream(iconArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(Icon::fromJson)
                .collect(Collectors.toSet());

        return new Theme(id, name, description, application, icons);
    }

    @NotNull
    public String getId()
    {
        return id;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getDescription()
    {
        return description;
    }

    @NotNull
    public String getApplication()
    {
        return application;
    }

    @NotNull
    public Set<Icon> getIcons()
    {
        return icons;
    }

    @Nullable
    public Icon matchApplication(@NotNull String code)
    {
        List<Pair<String, String>> values = Collections.singletonList(
                new Pair<>("code", code));

        return findMatch(this.getIcons(), values);
    }

    @Nullable
    public Icon matchLanguage(@NotNull String filename, @NotNull Pair<FileType, String> content)
    {
        List<Pair<String, String>> values = new ArrayList<>();

        values.add(new Pair<>("filename", filename));

        if (content.getFirst() == FileType.TEXT)
            new Pair<>("firstLine", content);
        else
            new Pair<>("magicNumber", content);

        return findMatch(this.getIcons(), values);
    }

    @Nullable
    private Icon findMatch(@NotNull Collection<Icon> entities, @NotNull List<Pair<String, String>> values)
    {
        return values.stream()
                .map(p -> entities.stream()
                        .filter(e -> e.matches(p.getFirst(), p.getSecond()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int compareTo(@NotNull Theme that)
    {
        if (this.getId().equals("classic"))
            return -1;
        if (that.getId().equals("classic"))
            return 1;
        else
            return this.getName().compareTo(that.getName());
    }
}
