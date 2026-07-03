import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class SearchPanel extends JPanel {
    private final UserData userData;
    private final TvMazeService api;

    private JTextField searchField;
    private JButton searchBtn;
    private JLabel statusLabel;
    private JList<Show> resultsList;
    private DefaultListModel<Show> listModel;

    private JLabel detailImage;
    private JLabel detailName;
    private JLabel detailLanguage;
    private JLabel detailGenres;
    private JLabel detailRating;
    private JLabel detailStatus;
    private JLabel detailPremiered;
    private JLabel detailEnded;
    private JLabel detailNetwork;
    private JTextArea detailSummary;

    private JButton btnFavorite;
    private JButton btnWatched;
    private JButton btnWatchlist;

    public SearchPanel(UserData userData, TvMazeService api) {
        this.userData = userData;
        this.api = api;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);
        add(buildSearchBar(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(Theme.BG_DARK);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(14, 20, 14, 20)
        ));

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setBackground(Theme.BG_LIGHT);
        searchField.setForeground(Theme.FG_PRIMARY);
        searchField.setCaretColor(Theme.ACCENT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doSearch();
            }
        });

        searchBtn = createAccentButton("Buscar");
        searchBtn.setPreferredSize(new Dimension(100, 36));
        searchBtn.addActionListener(e -> doSearch());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.FG_MUTED);

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setBackground(Theme.BG_DARK);
        right.add(searchBtn, BorderLayout.WEST);
        right.add(statusLabel, BorderLayout.CENTER);

        bar.add(searchField, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildResultsList(), buildDetails());
        split.setDividerLocation(260);
        split.setDividerSize(1);
        split.setBackground(Theme.BORDER);
        split.setBorder(null);
        split.setContinuousLayout(true);
        return split;
    }

    private JPanel buildResultsList() {
        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setBackground(Theme.BG_MEDIUM);
        resultsList.setForeground(Theme.FG_PRIMARY);
        resultsList.setFont(Theme.FONT_BODY);
        resultsList.setSelectionBackground(Theme.ACCENT_DARK);
        resultsList.setSelectionForeground(Color.WHITE);
        resultsList.setFixedCellHeight(44);
        resultsList.setCellRenderer(new ShowListCellRenderer());
        resultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onShowSelected(resultsList.getSelectedValue());
        });

        JScrollPane scroll = new JScrollPane(resultsList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_MEDIUM);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_MEDIUM);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDetails() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setBackground(Theme.BG_DARK);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));

        detailImage = new JLabel("Sem imagem", SwingConstants.CENTER);
        detailImage.setPreferredSize(new Dimension(120, 168));
        detailImage.setFont(Theme.FONT_SMALL);
        detailImage.setForeground(Theme.FG_MUTED);
        detailImage.setBackground(Theme.BG_MEDIUM);
        detailImage.setOpaque(true);
        detailImage.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        top.add(detailImage, BorderLayout.WEST);

        JPanel headerInfo = new JPanel(new GridBagLayout());
        headerInfo.setBackground(Theme.BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        detailName = new JLabel("Selecione uma serie");
        detailName.setFont(Theme.FONT_TITLE);
        detailName.setForeground(Theme.FG_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        headerInfo.add(detailName, gbc);

        detailLanguage = makeDetailValue();
        detailGenres   = makeDetailValue();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridy = 1; headerInfo.add(makeFieldRow("Idioma:", detailLanguage), gbc);
        gbc.gridy = 2; headerInfo.add(makeFieldRow("Generos:", detailGenres), gbc);

        gbc.gridy = 3; gbc.weighty = 1.0;
        headerInfo.add(new JLabel(), gbc);

        top.add(headerInfo, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        JPanel middle = new JPanel(new GridBagLayout());
        middle.setBackground(Theme.BG_DARK);

        GridBagConstraints m = new GridBagConstraints();
        m.fill = GridBagConstraints.HORIZONTAL;
        m.weightx = 1.0;
        m.anchor = GridBagConstraints.NORTHWEST;
        m.insets = new Insets(2, 0, 2, 0);

        detailRating    = makeDetailValue();
        detailStatus    = makeDetailValue();
        detailPremiered = makeDetailValue();
        detailEnded     = makeDetailValue();
        detailNetwork   = makeDetailValue();

        m.gridy = 0; middle.add(makeFieldRow("Nota:",     detailRating),    m);
        m.gridy = 1; middle.add(makeFieldRow("Estado:",   detailStatus),    m);
        m.gridy = 2; middle.add(makeFieldRow("Estreia:",  detailPremiered), m);
        m.gridy = 3; middle.add(makeFieldRow("Termino:",  detailEnded),     m);
        m.gridy = 4; middle.add(makeFieldRow("Emissora:", detailNetwork),   m);

        detailSummary = new JTextArea();
        detailSummary.setFont(Theme.FONT_SMALL);
        detailSummary.setForeground(Theme.FG_MUTED);
        detailSummary.setBackground(Theme.BG_DARK);
        detailSummary.setLineWrap(true);
        detailSummary.setWrapStyleWord(true);
        detailSummary.setEditable(false);
        detailSummary.setBorder(new EmptyBorder(10, 0, 0, 0));

        m.gridy = 5;
        m.weighty = 1.0;
        m.fill = GridBagConstraints.BOTH;
        m.insets = new Insets(8, 0, 0, 0);
        middle.add(detailSummary, m);

        panel.add(middle, BorderLayout.CENTER);
        panel.add(buildActionButtons(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnFavorite  = createToggleButton("+ Favorito");
        btnWatched   = createToggleButton("+ Ja Assisti");
        btnWatchlist = createToggleButton("+ Quero Ver");

        btnFavorite.addActionListener(e -> toggle(
            userData::isFavorite, userData::addFavorite, userData::removeFavorite));
        btnWatched.addActionListener(e -> toggle(
            userData::isWatched, userData::addWatched, userData::removeWatched));
        btnWatchlist.addActionListener(e -> toggle(
            userData::isInWatchlist, userData::addToWatchlist, userData::removeFromWatchlist));

        panel.add(btnFavorite);
        panel.add(btnWatched);
        panel.add(btnWatchlist);
        setButtonsEnabled(false);
        return panel;
    }

    private void toggle(java.util.function.Predicate<Show> check,
                        java.util.function.Consumer<Show> add,
                        java.util.function.Consumer<Show> remove) {
        Show show = resultsList.getSelectedValue();
        if (show == null) return;
        if (check.test(show)) remove.accept(show);
        else add.accept(show);
        updateButtons(show);
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        searchBtn.setEnabled(false);
        statusLabel.setText("Buscando...");
        listModel.clear();
        clearDetails();
        setButtonsEnabled(false);

        SwingWorker<List<Show>, Void> worker = new SwingWorker<>() {
            protected List<Show> doInBackground() throws Exception {
                return api.searchShows(query);
            }
            protected void done() {
                searchBtn.setEnabled(true);
                try {
                    List<Show> results = get();
                    if (results.isEmpty()) {
                        statusLabel.setText("Nenhum resultado encontrado.");
                    } else {
                        statusLabel.setText(results.size() + " resultado(s).");
                        for (Show s : results) listModel.addElement(s);
                        resultsList.setSelectedIndex(0);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Erro na busca.");
                    JOptionPane.showMessageDialog(SearchPanel.this,
                        "Erro ao buscar series:\n" + ex.getMessage(),
                        "Erro de conexão", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void onShowSelected(Show show) {
        if (show == null) { clearDetails(); return; }
        detailName.setText(show.getName());
        detailLanguage.setText(orNA(show.getLanguage()));
        detailGenres.setText(show.getGenresAsString());
        detailRating.setText(show.getRatingAsString());
        detailStatus.setText(translateStatus(show.getStatus()));
        detailPremiered.setText(orNA(show.getPremiered()));
        detailEnded.setText(orNA(show.getEnded()));
        detailNetwork.setText(orNA(show.getNetwork()));
        detailSummary.setText(show.getSummary() != null ? show.getSummary() : "");
        detailSummary.setCaretPosition(0);
        setButtonsEnabled(true);
        updateButtons(show);
        loadImage(show.getImageUrl());
    }

    private void clearDetails() {
        detailName.setText("Selecione uma serie");
        detailLanguage.setText("—");
        detailGenres.setText("—");
        detailRating.setText("—");
        detailStatus.setText("—");
        detailPremiered.setText("—");
        detailEnded.setText("—");
        detailNetwork.setText("—");
        detailSummary.setText("");
        detailImage.setIcon(null);
        detailImage.setText("Sem imagem");
        setButtonsEnabled(false);
    }

    private void updateButtons(Show show) {
        setToggleState(btnFavorite,  userData.isFavorite(show),    "Favorito");
        setToggleState(btnWatched,   userData.isWatched(show),     "Ja Assisti");
        setToggleState(btnWatchlist, userData.isInWatchlist(show), "Quero Ver");
    }

    private void setToggleState(JButton btn, boolean active, String label) {
        if (active) {
            btn.setText("✓ " + label);
            btn.setBackground(Theme.ACCENT_DARK);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setText("+ " + label);
            btn.setBackground(Theme.BG_LIGHT);
            btn.setForeground(Theme.FG_PRIMARY);
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnFavorite.setEnabled(enabled);
        btnWatched.setEnabled(enabled);
        btnWatchlist.setEnabled(enabled);
    }

    private static final HttpClient IMAGE_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .build();

    private void loadImage(String imageUrl) {
        detailImage.setIcon(null);
        detailImage.setText("Carregando...");
        if (imageUrl == null || imageUrl.isEmpty()) {
            detailImage.setText("Sem imagem");
            return;
        }
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            protected ImageIcon doInBackground() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .header("User-Agent", "TVTracker-Java/1.0")
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
                HttpResponse<InputStream> response = IMAGE_CLIENT.send(
                    request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() != 200) return null;
                BufferedImage img = ImageIO.read(response.body());
                if (img == null) return null;
                int w = 120;
                int h = (int)(img.getHeight() * (w / (double) img.getWidth()));
                Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) { detailImage.setIcon(icon); detailImage.setText(""); }
                    else detailImage.setText("Sem imagem");
                } catch (Exception ex) {
                    detailImage.setText("Sem imagem");
                }
            }
        };
        worker.execute();
    }

    private JPanel makeFieldRow(String labelText, JLabel value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Theme.BG_DARK);
        JLabel lbl = new JLabel(labelText + " ");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.FG_MUTED);
        row.add(lbl);
        row.add(value);
        return row;
    }

    private JLabel makeDetailValue() {
        JLabel l = new JLabel("—");
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.FG_PRIMARY);
        return l;
    }

    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY);
        btn.setBackground(Theme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_DARK),
            new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createToggleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_SMALL);
        btn.setBackground(Theme.BG_LIGHT);
        btn.setForeground(Theme.FG_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(7, 8, 7, 8)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String orNA(String val) {
        return (val == null || val.isEmpty()) ? "N/A" : val;
    }

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status) {
            case "Running":            return "Em exibição";
            case "Ended":              return "Encerrada";
            case "To Be Determined":   return "Indefinido";
            case "In Development":     return "Em desenvolvimento";
            default:                   return status;
        }
    }

    private static class ShowListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            if (value instanceof Show) {
                Show show = (Show) value;
                String rating = show.getRating() > 0
                    ? String.format(" %.1f★", show.getRating()) : "";
                label.setText("<html><b>" + show.getName() + "</b>"
                    + "<font color='#9E9E9E'>" + rating + "</font></html>");
            }
            label.setBorder(new EmptyBorder(8, 12, 8, 12));
            if (!isSelected)
                label.setBackground(index % 2 == 0 ? Theme.BG_MEDIUM : Theme.BG_DARK);
            return label;
        }
    }
}
