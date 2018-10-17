package com.almightyalpaca.jetbrains.plugins.discord.themes;

import com.almightyalpaca.jetbrains.plugins.discord.utils.FilenameUtils;
import com.almightyalpaca.jetbrains.plugins.discord.utils.Predicates;
import com.almightyalpaca.jetbrains.plugins.discord.utils.ThrowingFunction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThemeLoader
{
    @NotNull
    private static final URL URL_LATEST_GITHUB_RELEASE;
    @NotNull
    private static final Path TEMP_FOLDER = Paths.get(System.getProperty("java.io.tmpdir")).resolve("JetBrains-Discord-Integration-Icons/");
    @NotNull
    private static final String FILE_NAME = "icons.zip";
    @NotNull
    private static final Path ICON_FILE = TEMP_FOLDER.resolve(FILE_NAME);
    @NotNull
    private static final Path VERSION_FILE = TEMP_FOLDER.resolve("version.txt");
    @Nullable
    private static ZipFile ICON_ZIP_FILE;
    @Nullable
    private static JsonObject LATEST_GITHUB_RELEASE;

    static
    {
        try
        {
            URL_LATEST_GITHUB_RELEASE = new URL("https://api.github.com/repos/Almighty-Alpaca/JetBrains-Discord-Integration-Icons/releases/latest");
            Files.createDirectories(TEMP_FOLDER);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private final SortedMap<String, Theme> themes;

    private ThemeLoader()
    {
        this.themes = Collections.unmodifiableSortedMap(loadThemes());
    }

    @NotNull
    @Contract(pure = true)
    public static ThemeLoader getInstance()
    {
        return ThemeLoader.Singleton.getInstance();
    }

    @NotNull
    public SortedMap<String, Theme> getThemes()
    {
        return this.themes;
    }

    @NotNull
    private SortedMap<String, Theme> loadThemes()
    {
        ZipFile zip = this.getIconZip();

        JsonParser parser = new JsonParser();

        return zip.stream()
                .filter(Predicates.negate(ZipEntry::isDirectory))
                .filter(entry -> entry.getName().toLowerCase().endsWith(".json"))
                .map((ThrowingFunction<ZipEntry, Theme>) entry -> Theme.fromJson(
                        FilenameUtils.getBaseName(entry.getName()),
                        parser.parse(new InputStreamReader(zip.getInputStream(entry)))
                                .getAsJsonObject()))
                .collect(Collectors.toMap(Theme::getId,
                        Function.identity(),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        TreeMap::new));
    }

    @NotNull
    public InputStream getIcon(String theme, String asset) throws IOException
    {
        ZipFile zip = getIconZip();

        return zip.getInputStream(zip.getEntry("icons/" + theme + "/" + asset + "_low.png"));
    }

    @NotNull
    private synchronized ZipFile getIconZip()
    {
        if (ICON_ZIP_FILE == null)
        {
            try
            {
                String localVersion;
                if (Files.exists(VERSION_FILE))
                    localVersion = new String(Files.readAllBytes(VERSION_FILE), StandardCharsets.UTF_8).trim();
                else
                    localVersion = "";

                if (LATEST_GITHUB_RELEASE == null)
                    LATEST_GITHUB_RELEASE = new JsonParser()
                            .parse(new InputStreamReader(URL_LATEST_GITHUB_RELEASE.openStream()))
                            .getAsJsonObject();

                String currentVersion = LATEST_GITHUB_RELEASE.get("tag_name").getAsString().trim();

                if (!Files.exists(ICON_FILE) || !Objects.equals(localVersion.toLowerCase(), currentVersion.toLowerCase()))
                {
                    JsonArray assets = LATEST_GITHUB_RELEASE.get("assets").getAsJsonArray();

                    String url = StreamSupport.stream(assets.spliterator(), false)
                            .map(JsonElement::getAsJsonObject)
                            .filter(o -> o.get("name").getAsString().equalsIgnoreCase(FILE_NAME))
                            .findAny()
                            .map(o -> o.get("browser_download_url").getAsString())
                            .orElseThrow(IllegalStateException::new);

                    try (InputStream in = new URL(url).openStream())
                    {
                        Files.copy(in, ICON_FILE, StandardCopyOption.REPLACE_EXISTING);
                    }

                    Files.write(VERSION_FILE, currentVersion.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }

                ICON_ZIP_FILE = new ZipFile(ICON_FILE.toFile());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        return ICON_ZIP_FILE;
    }

    public static class Singleton
    {
        @NotNull
        private static final ThemeLoader INSTANCE;

        static
        {
            INSTANCE = new ThemeLoader();
        }

        protected Singleton() {}

        @NotNull
        public static ThemeLoader getInstance()
        {
            return INSTANCE;
        }
    }
}
