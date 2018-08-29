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
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("Duplicates")
public class DocumentListener implements com.intellij.openapi.editor.event.DocumentListener
{
    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(DocumentListener.class);

    @Override
    public void beforeDocumentChange(DocumentEvent event) {}

    @Override
    public void documentChanged(DocumentEvent event)
    {
        LOG.trace("DocumentListener#documentChanged({})", event.getDocument());

        Document document = event.getDocument();
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
}
