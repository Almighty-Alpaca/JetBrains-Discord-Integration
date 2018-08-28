package com.almightyalpaca.jetbrains.plugins.discord.themes;

import com.almightyalpaca.jetbrains.plugins.discord.utils.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

interface Matcher
{
    @NotNull
    static Matcher fromJson(@NotNull JsonObject object)
    {
        final String type = object.get("type").getAsString();

        switch (type.toLowerCase())
        {
            case "string":
                return StringMatcher.fromJson(object);
            case "regex":
                return RegexMatcher.fromJson(object);
            default:
                throw new RuntimeException("Unsupported matcher type error: " + type);
        }
    }

    boolean matches(@NotNull String value);

    class StringMatcher implements Matcher
    {
        @NotNull
        private final Set<String> patterns;
        private final boolean ignoreCase;
        @NotNull
        private final Variant variant;

        public StringMatcher(@NotNull Set<String> patterns, boolean ignoreCase, @NotNull Variant variant)
        {
            this.patterns = Collections.unmodifiableSet(ignoreCase ? patterns
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet()) : patterns);
            this.ignoreCase = ignoreCase;
            this.variant = variant;
        }

        @NotNull
        static StringMatcher fromJson(@NotNull JsonObject object)
        {
            final JsonArray matchArray = object.get("match").getAsJsonArray();
            final Set<String> patterns = StreamSupport.stream(matchArray.spliterator(), false)
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toSet());

            final boolean ignoreCase = GsonUtil.optBoolean(object, "ignoreCase", true); // default = true

            final Variant variant; // default = Variant.EQUALS
            JsonElement variantElement = object.get("variant");
            if (variantElement != null && variantElement.isJsonPrimitive())
            {
                JsonPrimitive primitive = variantElement.getAsJsonPrimitive();

                if (primitive.isString())
                    variant = Variant.get(primitive.getAsString());
                else
                    variant = Variant.EQUALS;
            }
            else
                variant = Variant.EQUALS;

            return new StringMatcher(patterns, ignoreCase, variant);
        }

        @NotNull
        public Set<String> getPatterns()
        {
            return patterns;
        }

        public boolean isIgnoreCase()
        {
            return ignoreCase;
        }

        @NotNull
        public Variant getVariant()
        {
            return variant;
        }

        @Override
        public boolean matches(@NotNull String value)
        {
            final String finalValue;
            if (this.ignoreCase)
                finalValue = value.toLowerCase();
            else
                finalValue = value;

            return this.patterns.stream()
                    .anyMatch(s -> this.variant.match(s, finalValue));
        }

        public enum Variant
        {
            EQUALS("equals")
                    {
                        @Override
                        public boolean match(@NotNull String pattern, @NotNull String string)
                        {
                            return string.equals(pattern);
                        }
                    },
            CONTAINS("contains")
                    {
                        @Override
                        public boolean match(@NotNull String pattern, @NotNull String string)
                        {
                            return string.contains(pattern);
                        }
                    },
            STARTS_WITH("startsWith")
                    {
                        @Override
                        public boolean match(@NotNull String pattern, @NotNull String string)
                        {
                            return string.startsWith(pattern);
                        }
                    },
            ENDS_WITH("endsWith")
                    {
                        @Override
                        public boolean match(@NotNull String pattern, @NotNull String string)
                        {
                            return string.endsWith(pattern);
                        }
                    };
            @NotNull
            private final String key;

            Variant(@NotNull String key)
            {
                this.key = key;
            }

            @NotNull
            public static Variant get(@NotNull String key)
            {
                return Arrays.stream(Variant.values())
                        .filter(v -> v.getKey().equalsIgnoreCase(key))
                        .findFirst()
                        .orElseThrow(RuntimeException::new);
            }

            @NotNull
            public String getKey()
            {
                return key;
            }

            public abstract boolean match(@NotNull String pattern, @NotNull String string);
        }
    }

    class RegexMatcher implements Matcher
    {
        @NotNull
        private final Set<Pattern> patterns;
        private final boolean matchPartly;

        public RegexMatcher(@NotNull Set<Pattern> patterns, boolean matchPartly)
        {
            this.patterns = Collections.unmodifiableSet(patterns);
            this.matchPartly = matchPartly;
        }

        @NotNull
        static RegexMatcher fromJson(@NotNull JsonObject object)
        {
            final JsonArray matchArray = object.get("match").getAsJsonArray();
            final Set<Pattern> patterns = StreamSupport.stream(matchArray.spliterator(), false)
                    .map(JsonElement::getAsString)
                    .map(s -> Pattern.compile(s, 0))
                    .collect(Collectors.toSet());

            final boolean matchPartly = GsonUtil.optBoolean(object, "matchPartly", true); // default = true

            return new RegexMatcher(patterns, matchPartly);
        }

        @NotNull
        public Set<Pattern> getPatterns()
        {
            return patterns;
        }

        public boolean isMatchPartly()
        {
            return matchPartly;
        }

        @Override
        public boolean matches(@NotNull String value)
        {
            return this.patterns.stream()
                    .anyMatch(p -> this.matchPartly ? p.matcher(value).find() : p.matcher(value).matches());
        }
    }
}
