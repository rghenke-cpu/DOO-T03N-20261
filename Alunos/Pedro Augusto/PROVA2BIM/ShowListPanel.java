import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowListPanel extends JPanel {

    public enum ListType { FAVORITES, WATCHED, WATCHLIST }

    private static final String[] SORT_OPTIONS = {
        "Nome (A-Z)", "Nota (maior primeiro)", "Estado", "Data de estreia"
    };

    private final UserData userData;
    private final ListType listType;
    private final TvMazeService api;

    private DefaultListModel<Show> listModel;
    private JList<Show> showList;
    private JLabel statusLabel;
    private JComboBox<String> sortCombo;

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
    private JButton btnRemove;

    public ShowListPanel(UserData userData, ListType listType, TvMazeService api) {
        this.userData = userData;
        this.listType = listType;
        this.api = api;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshList();
            }
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Theme.BG_DARK);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel(getTitle());
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.ACCENT);

        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.FG_MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setBackground(Theme.BG_DARK);
        left.add(title);
        left.add(statusLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(Theme.BG_DARK);

        JLabel sortLabel = new JLabel("Ordenar:");
        sortLabel.setFont(Theme.FONT_SMALL);
        sortLabel.setForeground(Theme.FG_MUTED);

        sortCombo = new JComboBox<>(SORT_OPTIONS);
        sortCombo.setFont(Theme.FONT_SMALL);
        sortCombo.setBackground(Theme.BG_LIGHT);
        sortCombo.setForeground(Theme.FG_PRIMARY);
        sortCombo.setPreferredSize(new Dimension(200, 30));
        sortCombo.addActionListener(e -> refreshList());

        right.add(sortLabel);
        right.add(sortCombo);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildShowList(), buildDetails());
        split.setDividerLocation(260);
        split.setDividerSize(1);
        split.setBackground(Theme.BORDER);
        split.setBorder(null);
        split.setContinuousLayout(true);
        return split;
    }

    private JPanel buildShowList() {
        listModel = new DefaultListModel<>();
        showList = new JList<>(listModel);
        showList.setBackground(Theme.BG_MEDIUM);
        showList.setForeground(Theme.FG_PRIMARY);
        showList.setFont(Theme.FONT_BODY);
        showList.setSelectionBackground(Theme.ACCENT_DARK);
        showList.setSelectionForeground(Color.WHITE);
        showList.setFixedCellHeight(44);
        showList.setCellRenderer(new ShowListCellRenderer());
        showList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onShowSelected(showList.getSelectedValue());
        });

        JScrollPane scroll = new JScrollPane(showList);
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

        detailLanguage = makeValue();
        detailGenres   = makeValue();
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridy = 1; headerInfo.add(makeRow("Idioma:",   detailLanguage), gbc);
        gbc.gridy = 2; headerInfo.add(makeRow("Generos:",  detailGenres),   gbc);

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

        detailRating    = makeValue();
        detailStatus    = makeValue();
        detailPremiered = makeValue();
        detailEnded     = makeValue();
        detailNetwork   = makeValue();

        m.gridy = 0; middle.add(makeRow("Nota:",     detailRating),    m);
        m.gridy = 1; middle.add(makeRow("Estado:",   detailStatus),    m);
        m.gridy = 2; middle.add(makeRow("Estreia:",  detailPremiered), m);
        m.gridy = 3; middle.add(makeRow("Termino:",  detailEnded),     m);
        m.gridy = 4; middle.add(makeRow("Emissora:", detailNetwork),   m);

        detailSummary = new JTextArea();
        detailSummary.setFont(Theme.FONT_SMALL);
        detailSummary.setForeground(Theme.FG_MUTED);
        detailSummary.setBackground(Theme.BG_DARK);
        detailSummary.setLineWrap(true);
        detailSummary.setWrapStyleWord(true);
        detailSummary.setEditable(false);
        detailSummary.setBorder(new EmptyBorder(10, 0, 0, 0));

        m.gridy = 5; m.weighty = 1.0;
        m.fill = GridBagConstraints.BOTH;
        m.insets = new Insets(8, 0, 0, 0);
        middle.add(detailSummary, m);
        panel.add(middle, BorderLayout.CENTER);

        btnRemove = new JButton("Remover da lista");
        btnRemove.setFont(Theme.FONT_BODY);
        btnRemove.setBackground(Theme.DANGER);
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);
        btnRemove.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnRemove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemove.setEnabled(false);
        btnRemove.addActionListener(e -> removeSelected());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(Theme.BG_DARK);
        south.setBorder(new EmptyBorder(12, 0, 0, 0));
        south.add(btnRemove);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    public void refreshList() {
        Show selected = showList.getSelectedValue();
        listModel.clear();

        List<Show> sorted = sorted(new ArrayList<>(getList()));
        for (Show s : sorted) listModel.addElement(s);

        int count = listModel.getSize();
        statusLabel.setText(count == 0 ? "Nenhuma serie" : count + " serie(s)");

        if (selected != null) {
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getId() == selected.getId()) {
                    showList.setSelectedIndex(i);
                    return;
                }
            }
        }
        clearDetails();
    }

    private void removeSelected() {
        Show show = showList.getSelectedValue();
        if (show == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Remover \"" + show.getName() + "\" da lista?",
            "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        switch (listType) {
            case FAVORITES: userData.removeFavorite(show);     break;
            case WATCHED:   userData.removeWatched(show);      break;
            case WATCHLIST: userData.removeFromWatchlist(show); break;
        }
        refreshList();
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
        btnRemove.setEnabled(true);
        loadImage(show);
    }

    private void clearDetails() {
        detailName.setText("Selecione uma serie");
        detailLanguage.setText("—"); detailGenres.setText("—");
        detailRating.setText("—");   detailStatus.setText("—");
        detailPremiered.setText("—"); detailEnded.setText("—");
        detailNetwork.setText("—");  detailSummary.setText("");
        detailImage.setIcon(null);   detailImage.setText("Sem imagem");
        btnRemove.setEnabled(false);
    }

    private List<Show> getList() {
        switch (listType) {
            case FAVORITES: return userData.getFavorites();
            case WATCHED:   return userData.getWatched();
            case WATCHLIST: return userData.getWatchlist();
            default:        return new ArrayList<>();
        }
    }

    private List<Show> sorted(List<Show> list) {
        int idx = sortCombo != null ? sortCombo.getSelectedIndex() : 0;
        switch (idx) {
            case 0: list.sort(Comparator.comparing(s -> s.getName().toLowerCase())); break;
            case 1: list.sort((a, b) -> Double.compare(b.getRating(), a.getRating())); break;
            case 2: list.sort(Comparator.comparing(s -> translateStatus(s.getStatus()))); break;
            case 3: list.sort(Comparator.comparing(s -> orNA(s.getPremiered()))); break;
        }
        return list;
    }

    private String getTitle() {
        switch (listType) {
            case FAVORITES: return "Favoritos";
            case WATCHED:   return "Ja Assistidas";
            case WATCHLIST: return "Quero Assistir";
            default:        return "";
        }
    }

    private static final HttpClient IMAGE_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .build();

    private void loadImage(Show show) {
        detailImage.setIcon(null);
        detailImage.setText("Carregando...");
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            protected ImageIcon doInBackground() throws Exception {
                String url = show.getImageUrl();
                if (url == null || url.isEmpty()) {
                    url = api.fetchImageUrl(show.getId());
                    if (url != null) show.setImageUrl(url);
                }
                if (url == null || url.isEmpty()) return null;
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
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

    private JPanel makeRow(String labelText, JLabel value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Theme.BG_DARK);
        JLabel lbl = new JLabel(labelText + " ");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.FG_MUTED);
        row.add(lbl);
        row.add(value);
        return row;
    }

    private JLabel makeValue() {
        JLabel l = new JLabel("—");
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.FG_PRIMARY);
        return l;
    }

    private String orNA(String val) {
        return (val == null || val.isEmpty()) ? "N/A" : val;
    }

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status) {
            case "Running":          return "Em exibição";
            case "Ended":            return "Encerrada";
            case "To Be Determined": return "Indefinido";
            case "In Development":   return "Em desenvolvimento";
            default:                 return status;
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
