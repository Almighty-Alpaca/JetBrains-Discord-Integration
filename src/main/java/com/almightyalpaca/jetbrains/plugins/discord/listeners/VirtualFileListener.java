/*
 * Copyright 2017-2018 Aljoscha Grebe
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
package com.almightyalpaca.jetbrains.plugins.discord.listeners;

import com.almightyalpaca.jetbrains.plugins.discord.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

public class VirtualFileListener implements com.intellij.openapi.vfs.VirtualFileListener
{
    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event)
    {
        VirtualFile file = event.getFile();

        FileDocumentManager documentManager = FileDocumentManager.getInstance();

        Document document = documentManager.getDocument(file);

        if (document != null)
        {
            Editor[] editors = EditorFactory.getInstance().getEditors(document);

            for (Editor editor : editors)
            {
                Project project = editor.getProject();

                ProjectComponent component = ProjectComponent.getInstance(project);

                if (component != null && component.getFiles().containsKey(file))
                {
                    if ((event.getPropertyName().equals(VirtualFile.PROP_NAME)))
                    {
                        component.fileUpdateName(file);
                    }
                    else if ((event.getPropertyName().equals(VirtualFile.PROP_WRITABLE)))
                    {
                        component.fileUpdateReadOnly(event.getFile());
                    }
                }
            }
        }
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {}

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {}

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {}

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {}

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {}

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {}

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {}

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {}

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {}
}
