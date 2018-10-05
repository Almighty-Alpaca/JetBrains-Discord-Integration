package com.almightyalpaca.jetbrains.plugins.discord.settings.gui.themes;

import com.almightyalpaca.jetbrains.plugins.discord.settings.gui.SettingsPanel;
import com.almightyalpaca.jetbrains.plugins.discord.swing.ModifiedFlowLayout;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Icon;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Theme;
import com.almightyalpaca.jetbrains.plugins.discord.themes.ThemeLoader;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBImageIcon;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ThemeChooser extends DialogWrapper
{
    private static final int ICON_SIZE = 64;
    private static final int BORDER_SIZE = 10;
    private static final ImageIcon DEFAULT_ICON;

    static
    {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(JBColor.foreground());

        DEFAULT_ICON = new ImageIcon(image);
    }

    @NotNull
    private final SettingsPanel settingsPanel;

    public ThemeChooser(@NotNull SettingsPanel settingsPanel)
    {
        super(settingsPanel.getRootPanel(), false);

        this.settingsPanel = settingsPanel;

        init();

        setTitle("Select Theme");

        getRootPane().setMinimumSize(new Dimension(1920 / 2, 1080 / 2));
        getRootPane().setPreferredSize(new Dimension(1920 / 2, 1080 / 2));
    }

    @Override
    protected JComponent createSouthPanel()
    {
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel()
    {
        JBTabbedPane tabbedPane = new JBTabbedPane();

        SortedMap<String, Theme> themes = ThemeLoader.getInstance().getThemes();

        for (Theme theme : themes.values())
            tabbedPane.addTab(theme.getName(), new ThemePanel(theme));

        return tabbedPane;
    }

    private class ThemePanel extends JBPanel<ThemePanel>
    {
        private final Theme theme;

        public ThemePanel(@NotNull Theme theme)
        {
            this.theme = theme;

            setLayout(new BorderLayout());

            JBScrollPane scrollPane = new JBScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scrollPane, BorderLayout.CENTER);

            JBPanel<JBPanel> filePanel = new JBPanel<>();
            FlowLayout langPanelLayout = new ModifiedFlowLayout();
            langPanelLayout.setAlignment(FlowLayout.LEFT);
            filePanel.setLayout(langPanelLayout);
            scrollPane.getViewport().add(filePanel);

            ApplicationManager.getApplication().executeOnPooledThread(() ->
                    this.theme.getIcons().stream()
                            .collect(Collectors.groupingBy(Icon::getAssetKey, TreeMap::new, Collectors.toSet())).entrySet().stream()
                            .sorted(Comparator.comparing((Map.Entry<String, Set<Icon>> entry) ->
                                    entry.getValue().stream()
                                            .findAny()
                                            .orElseThrow(IllegalStateException::new)
                                            .getMatchers("code")
                                            .isEmpty())
                                    .reversed()
                                    .thenComparing(Map.Entry::getKey))
                            .forEach(entry ->
                                    addIcon(filePanel,
                                            entry.getKey(),
                                            entry.getValue().stream()
                                                    .map(Icon::getName)
                                                    .distinct()
                                                    .sorted()
                                                    .collect(Collectors.joining("\n"))
                                    )));

            JBPanel<JBPanel> bottomPanel = new JBPanel<>();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            add(new BottomPanel(theme.getDescription(), settingsPanel.getTheme().equals(this.theme), () -> {
                ThemeChooser.this.settingsPanel.setTheme(this.theme);
                ThemeChooser.this.close(DialogWrapper.OK_EXIT_CODE);
            }).getRootPanel(), BorderLayout.SOUTH);
        }

        private void addIcon(JPanel parent, String assetKey, String tooltip)
        {
            JBLabel label = new JBLabel();
            label.setIcon(DEFAULT_ICON);
            label.setPreferredSize(new Dimension(ICON_SIZE + BORDER_SIZE * 2, ICON_SIZE + BORDER_SIZE * 2));

            label.setBorder(JBUI.Borders.empty(BORDER_SIZE));
            label.setToolTipText(tooltip);
            parent.add(label);

            ApplicationManager.getApplication().executeOnPooledThread(() -> setIcon(label, assetKey));
        }

        private void setIcon(JBLabel label, String assetKey)
        {
            try
            {
                BufferedImage image = ImageIO.read(ThemeLoader.getInstance().getIcon(theme.getId(), assetKey));

                Image scaledImage = image.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);

                label.setIcon(new JBImageIcon(scaledImage));
            }
            catch (IOException ignored)
            {
            }
        }
    }
}
