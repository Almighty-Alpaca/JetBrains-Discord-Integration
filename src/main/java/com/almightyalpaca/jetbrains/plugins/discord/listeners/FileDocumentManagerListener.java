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
import com.almightyalpaca.jetbrains.plugins.discord.debug.Logger;
import com.almightyalpaca.jetbrains.plugins.discord.debug.LoggerFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("Duplicates")
public class FileDocumentManagerListener implements com.intellij.openapi.fileEditor.FileDocumentManagerListener
{
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(FileDocumentManagerListener.class);

    @Override
    public void beforeAllDocumentsSaving() {}

    @Override
    public void beforeDocumentSaving(@NotNull Document document)
    {
        LOG.trace("FileDocumentManagerListener#beforeDocumentSaving({})", document);

        Editor[] editors = EditorFactory.getInstance().getEditors(document);
        FileDocumentManager documentManager = FileDocumentManager.getInstance();

        for (Editor editor : editors)
        {
            Project project = editor.getProject();
            ProjectComponent component = ProjectComponent.getInstance(project);

            if (component != null)
                component.updateTimeAccessed(documentManager.getFile(document));
        }
    }

    @Override
    public void beforeFileContentReload(VirtualFile file, @NotNull Document document) {}

    @Override
    public void fileWithNoDocumentChanged(@NotNull VirtualFile file) {}

    @Override
    public void fileContentReloaded(@NotNull VirtualFile file, @NotNull Document document) {}

    @Override
    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {}

    @Override
    public void unsavedDocumentsDropped() {}
}
