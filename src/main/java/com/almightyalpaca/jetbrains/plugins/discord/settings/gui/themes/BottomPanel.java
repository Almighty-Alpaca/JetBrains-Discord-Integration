package com.almightyalpaca.jetbrains.plugins.discord.settings.gui.themes;

import com.almightyalpaca.jetbrains.plugins.discord.utils.DesktopUtils;
import com.petebevin.markdown.MarkdownProcessor;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class BottomPanel
{
    private JTextPane descriptionPane;
    private JButton missingAnIconButton;
    private JButton selectButton;
    private JPanel rootPanel;

    public BottomPanel(String description, boolean isSelected, Runnable selectCallback)
    {
        descriptionPane.setText(new MarkdownProcessor().markdown(description));
        descriptionPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        selectButton.setEnabled(!isSelected);

        missingAnIconButton.addActionListener(e -> DesktopUtils.openLinkInBrowser("https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration-Icons/issues/new"));
        selectButton.addActionListener(e -> selectCallback.run());
        descriptionPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                DesktopUtils.openLinkInBrowser(e.getURL().toString());
        });
    }

    public JPanel getRootPanel()
    {
        return rootPanel;
    }
}
