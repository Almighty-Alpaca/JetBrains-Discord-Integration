/*
 * Copyright 2017 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.almightyalpaca.intellij.plugins.discord.data;

import com.almightyalpaca.intellij.plugins.discord.collections.cloneable.ReallyCloneable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileInfo implements Serializable, ReallyCloneable<FileInfo>, Comparable<FileInfo>
{
    @NotNull
    private final String id;
    @NotNull
    private final String baseName;
    @Nullable
    private final String extension;
    private final long timeOpened;
    private long timeAccessed;
    @NotNull
    private transient FileInfo.Language language;

    public FileInfo(@NotNull VirtualFile file)
    {
        this(file.getPath(), file.getNameWithoutExtension(), file.getExtension(), System.currentTimeMillis());
    }

    public FileInfo(@NotNull String id, @NotNull String baseName, @Nullable String extension, long timeOpened)
    {
        this(id, baseName, extension, timeOpened, timeOpened);
    }

    public FileInfo(@NotNull String id, @NotNull String baseName, @Nullable String extension, long timeOpened, long timeAccessed)
    {
        this.id = id;
        this.baseName = baseName;
        this.extension = extension;
        this.timeOpened = timeOpened;
        this.timeAccessed = timeAccessed;

        this.language = FileInfo.Language.get(getName());
    }

    @NotNull
    public String getId()
    {
        return id;
    }

    public long getTimeOpened()
    {
        return timeOpened;
    }

    public long getTimeAccessed()
    {
        return timeAccessed;
    }

    void setTimeAccessed(long timeAccessed)
    {
        this.timeAccessed = timeAccessed;
    }

    @NotNull
    public Language getLanguage()
    {
        return language;
    }

    @NotNull
    public String getBaseName()
    {
        return baseName;
    }

    @Nullable
    public String getExtension()
    {
        return extension;
    }

    @NotNull
    public String getName()
    {
        if (extension == null)
            return baseName;
        else
            return baseName + '.' + extension;
    }

    @NotNull
    public String getAssetName(boolean showUnknown)
    {
        return this.language.getAssetName(showUnknown);
    }

    @Override
    public int compareTo(@NotNull FileInfo file)
    {
        return Long.compare(this.timeAccessed, file.timeAccessed);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof FileInfo && baseName.equals(((FileInfo) o).baseName) && Objects.equals(extension, ((FileInfo) o).extension);
    }

    @Override
    public String toString()
    {
        return "FileInfo{" + "baseName='" + baseName + '\'' + ", extension='" + extension + '\'' + '}';
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public FileInfo clone()
    {
        return new FileInfo(id, baseName, extension, timeOpened, timeAccessed);
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();

        this.language = FileInfo.Language.get(getName());
    }

    public String getLanguageName()
    {
        if (this.language == FileInfo.Language.UNKNOWN)
            if (extension == null)
                return this.language.getName();
            else
                return this.language.getName() + " ." + extension + "";
        else
            return this.language.getName();
    }

    public enum Language
    {
        C("C", "c", "c", "h"),
        CMAKE("CMake", "cmake", "CMakeLists.txt"),
        CPP("C++", "cpp", "cpp", "hpp"),
        CSS("CSS", "css", "css"),
        C_SHARP("C#", "csharp", "cs"),
        GO("Go", "go", "go"),
        GRADLE("Gradle", "gradle", "gradle", "gradle.kts"),
        GROOVY("Groovy", "groovy", "groovy"),
        HTML("HTML", "html", "html", "htm"),
        JAVA("Java", "java", "java"),
        JAVASCRIPT("JavaScript", "javascript", "js"),
        JSON("JSON", "json", "json"),
        KOTLIN("Kotlin", "kotlin", "kt", "kts"),
        LUA("Lua", "lua", "lua"),
        MARKDOWN("Markdown", "markdown", "md", "markdown"),
        PHP("PHP", "php", "php"),
        PYTHON("Python", "python", "py"),
        RUBY("Ruby", "ruby", "rb"),
        RUST("Rust", "rust", "rs"),
        SCALA("Scala", "scala", "scala", "sc"),
        SQL("SQL", "sql", "sql"),
        SWIFT("Swift", "swift", "swift"),
        TYPESCRIPT("TypeScript", "typescript", "ts", "tsx"),
        VUE("Vue.js", "vue", "vue"),
        XML("XML", "xml", "xml"),

        UNKNOWN("Unknown file type", "unknown")
                {
                    @NotNull
                    @Override
                    public String getAssetName(boolean showUnknown)
                    {
                        return showUnknown ? super.getAssetName(true) : "none";
                    }
                };

        @NotNull
        private static final Map<String, Language> MAP;

        static
        {
            MAP = new HashMap<>();

            for (FileInfo.Language language : FileInfo.Language.values())
                for (String extension : language.extensions)
                    if (MAP.put(extension, language) != null)
                        throw new ExceptionInInitializerError("Two language cannot have the same extension");
        }

        @NotNull
        private final String name;
        @NotNull
        private final String assetName;
        @NotNull
        private final String[] extensions;

        Language(@NotNull String name, @NotNull String assetName, @NotNull String... extensions)
        {
            this.name = name;
            this.assetName = assetName;
            this.extensions = extensions;
        }

        @NotNull
        public static FileInfo.Language get(@NotNull String fileName)
        {
            int index = 0;
            do
            {
                Language language = MAP.get(fileName.substring(index));

                if (language != null)
                    return language;
            }
            while ((index = fileName.indexOf('.', index) + 1) != 0);

            return UNKNOWN;
        }

        @NotNull
        public String getName()
        {
            return name;
        }

        @NotNull
        public String[] getExtensions()
        {
            String[] newExtensions = new String[extensions.length];
            System.arraycopy(extensions, 0, newExtensions, 0, extensions.length);
            return newExtensions;
        }

        @NotNull
        public String getAssetName(boolean showUnknown)
        {
            return assetName;
        }
    }
}
