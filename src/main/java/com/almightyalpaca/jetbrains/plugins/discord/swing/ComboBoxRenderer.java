package com.almightyalpaca.jetbrains.plugins.discord.swing;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ComboBoxRenderer<E> extends ColoredListCellRenderer<E>
{
    private Color selectionBackgroundColor = null;

    @Override
    protected void customizeCellRenderer(@NotNull JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus)
    {
        if (this.selectionBackgroundColor == null)
            this.selectionBackgroundColor = list.getSelectionBackground();

        list.setSelectionBackground(JBColor.background());

        if (isSelected)
            setBackground(this.selectionBackgroundColor);
        else
            setBackground(JBColor.background());

        append(Objects.toString(value));
        setFont(list.getFont());
    }
}
