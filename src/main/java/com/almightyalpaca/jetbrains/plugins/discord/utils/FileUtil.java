package com.almightyalpaca.jetbrains.plugins.discord.utils;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class FileUtil
{
    @NotNull
    public static Pair<FileType, String> readFile(@NotNull VirtualFile file) // TODO: fix reading files
    {
//        Document document = FileDocumentManager.getInstance().getDocument(file);
//
//        boolean text = !FileTypeRegistry.getInstance().getFileTypeByFile(file).isBinary();
//
//        if (document != null)
//        {
//            if (file.get)
//        }
//
//        return new Pair<>(FileType.TEXT, "");
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream())))
//        {
//
//            String line = reader.readLine();
//
//            if (line == null)
//            {
//                return Pair.of(FileType.TEXT, "");
//            }
//            else
//            {
//                if (line.length() > 256)
//                    return line.substring(0, 256);
//                else
//                    return line;
//            }
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//
//            Notifications.Bus.notify(new ErrorNotification(e.getMessage()));
//        }
//
//        return "";

        return new Pair<>(FileType.TEXT, "");
    }
}
