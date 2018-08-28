package com.almightyalpaca.jetbrains.plugins.discord.themes;

import com.almightyalpaca.jetbrains.plugins.discord.utils.ThrowingFunction;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThemeLoader
{
    private static final String GIT_REMOTE = "https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration-Icons.git";

    @NotNull
    private final SortedMap<String, Theme> themes;
    private Path iconBaseFolder;

    private ThemeLoader() throws IOException, GitAPIException
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
    public Path getIconBaseFolder()
    {
        return this.iconBaseFolder;
    }

    @NotNull
    private SortedMap<String, Theme> loadThemes() throws IOException, GitAPIException
    {
        return loadThemes(Paths.get(System.getProperty("java.io.tmpdir")).resolve("JetBrains-Discord-Integration-Icons/"));
    }

    @NotNull
    private SortedMap<String, Theme> loadThemes(@NotNull Path folder) throws IOException, GitAPIException
    {
        this.getOrCloneRepo(folder);

        JsonParser parser = new JsonParser();

        Path icons = folder.resolve("icons");

        this.iconBaseFolder = icons;

        return Files.list(icons)
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(Path::toString))
                .map(path -> Pair.of(path, Paths.get(path.toAbsolutePath().toString() + ".json")))
                .filter(pair -> Files.exists(pair.getRight()))
                .map((ThrowingFunction<Pair<Path, Path>, Theme>) pair -> {
                    JsonObject themeObject = parser
                            .parse(Files.newBufferedReader(pair.getRight(), Charset.forName("UTF-8")))
                            .getAsJsonObject();

                    return Theme.fromJson(FilenameUtils.getBaseName(pair.getRight().getFileName().toString()), themeObject);
                })
                .collect(Collectors.toMap(Theme::getId,
                        Function.identity(),
                        (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                        TreeMap::new));
    }

    private void getOrCloneRepo(@NotNull Path folder) throws IOException, GitAPIException
    {
        if (Files.exists(folder) && Files.list(folder).count() > 0)
        {
            Git git = Git.open(folder.toFile());

            git.reset().setMode(ResetCommand.ResetType.HARD).call();
            git.pull().call();
        }
        else
        {
            Git.cloneRepository()
                    .setDirectory(folder.toFile())
                    .setURI(GIT_REMOTE)
                    .setBranch("master")
                    .setBranchesToClone(Collections.singleton("refs/head/master"))
                    .call();
        }
    }

    public static class Singleton
    {
        @NotNull
        private static final ThemeLoader instance;

        static
        {
            try
            {
                instance = new ThemeLoader();
            }
            catch (GitAPIException | IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        protected Singleton() { }

        @NotNull
        public static ThemeLoader getInstance()
        {
            return instance;
        }
    }
}
