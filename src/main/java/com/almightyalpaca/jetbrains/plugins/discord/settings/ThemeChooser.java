package com.almightyalpaca.jetbrains.plugins.discord.settings;

import com.almightyalpaca.jetbrains.plugins.discord.swing.ModifiedFlowLayout;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Icon;
import com.almightyalpaca.jetbrains.plugins.discord.themes.Theme;
import com.almightyalpaca.jetbrains.plugins.discord.themes.ThemeLoader;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.panels.StatelessCardLayout;
import com.intellij.util.ui.JBImageIcon;
import com.intellij.util.ui.JBUI;
import com.petebevin.markdown.MarkdownProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ThemeChooser extends DialogWrapper
{
    private static final int COLLUM_WIDTH = 64;
    @NotNull
    private final DiscordIntegrationSettingsPanel settingsPanel;

    public ThemeChooser(@NotNull DiscordIntegrationSettingsPanel settingsPanel)
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

    void openLink(String link)
    {
        try
        {
            Desktop.getDesktop().browse(URI.create(link));
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
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

            Path themeBasePath = ThemeLoader.getInstance().getIconBaseFolder().resolve(this.theme.getId());

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
                                    themeBasePath,
                                    entry.getKey(),
                                    entry.getValue().stream()
                                            .map(Icon::getName)
                                            .distinct()
                                            .sorted()
                                            .collect(Collectors.joining("\n"))
                            ));

            JBPanel<JBPanel> bottomPanel = new JBPanel<>();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
            add(bottomPanel, BorderLayout.SOUTH);

            JTextPane textPane = new JTextPane();
            textPane.setAlignmentX(JButton.LEFT_ALIGNMENT);
            textPane.setAlignmentY(JButton.TOP_ALIGNMENT);
            textPane.setBorder(JBUI.Borders.emptyTop(20));
            textPane.setEditable(false);
            textPane.setContentType("text/html");
            textPane.setText(new MarkdownProcessor().markdown(theme.getDescription()));
            textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
            bottomPanel.add(textPane);

            textPane.addHyperlinkListener(e -> openLink(e.getURL().toString()));

            JButton githubButton = new JButton("Missing an icon?");
            githubButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
            githubButton.setAlignmentY(JButton.BOTTOM_ALIGNMENT);
            githubButton.setPreferredSize(new Dimension(100, 50));
            bottomPanel.add(StatelessCardLayout.wrap(githubButton));

            githubButton.addActionListener(e -> openLink("https://github.com/Almighty-Alpaca/JetBrains-Discord-Integration-Icons/issues/new"));

            JButton selectButton = new JButton("Select");
            selectButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
            selectButton.setAlignmentY(JButton.BOTTOM_ALIGNMENT);
            selectButton.setPreferredSize(new Dimension(100, 50));
            bottomPanel.add(StatelessCardLayout.wrap(selectButton));

            selectButton.addActionListener(e -> {
                ThemeChooser.this.settingsPanel.setTheme(this.theme);

                ThemeChooser.this.close(DialogWrapper.OK_EXIT_CODE);
            });
        }

        private void addIcon(JPanel parent, Path themeBasePath, String assetKey, String tooltip)
        {
            try
            {
                Path iconFile = themeBasePath.resolve(assetKey + "_low.png");
                BufferedImage image = ImageIO.read(Files.newInputStream(iconFile));

                @SuppressWarnings("SuspiciousNameCombination") // icons are known to be a square
                Image scaledImage = image.getScaledInstance(COLLUM_WIDTH, COLLUM_WIDTH, Image.SCALE_FAST);

                JBLabel label = new JBLabel(new JBImageIcon(scaledImage));
                label.setBorder(JBUI.Borders.empty(10));
                label.setToolTipText(tooltip);
                parent.add(label);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
