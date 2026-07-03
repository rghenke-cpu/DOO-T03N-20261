import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final UserData userData;
    private final PersistenceService persistence;

    private JLabel greetingLabel;
    private JPanel contentArea;
    private CardLayout cardLayout;

    private JButton activeNavBtn;

    public MainWindow(UserData userData, PersistenceService persistence) {
        this.userData = userData;
        this.persistence = persistence;
        build();
    }

    private void build() {
        setTitle("TV Tracker");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel logo = new JLabel("TV Tracker");
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.ACCENT);

        greetingLabel = new JLabel("Olá, " + userData.getProfile().getUsername());
        greetingLabel.setFont(Theme.FONT_BODY);
        greetingLabel.setForeground(Theme.FG_MUTED);

        header.add(logo, BorderLayout.WEST);
        header.add(greetingLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.BG_MEDIUM);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 1, Theme.BORDER),
            new EmptyBorder(16, 0, 16, 0)
        ));
        sidebar.setPreferredSize(new Dimension(180, 0));

        JButton[] navButtons = {
            createNavButton("Buscar Series",  "search"),
            createNavButton("Favoritos",      "favorites"),
            createNavButton("Ja Assistidas",  "watched"),
            createNavButton("Quero Assistir", "watchlist")
        };

        for (JButton btn : navButtons) {
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        setActiveNav(navButtons[0]);

        return sidebar;
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(Theme.FG_MUTED);
        btn.setBackground(Theme.BG_MEDIUM);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addActionListener(e -> {
            setActiveNav(btn);
            cardLayout.show(contentArea, card);
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setForeground(Theme.FG_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setForeground(Theme.FG_MUTED);
            }
        });

        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setForeground(Theme.FG_MUTED);
            activeNavBtn.setBackground(Theme.BG_MEDIUM);
            activeNavBtn.setBorderPainted(false);
        }
        activeNavBtn = btn;
        btn.setForeground(Theme.ACCENT);
        btn.setBackground(Theme.BG_LIGHT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 3, 0, 0, Theme.ACCENT),
            new EmptyBorder(10, 17, 10, 20)
        ));
    }

    private JPanel buildContent() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG_DARK);

        TvMazeService api = new TvMazeService();
        contentArea.add(new SearchPanel(userData, api),                                              "search");
        contentArea.add(new ShowListPanel(userData, ShowListPanel.ListType.FAVORITES, api),  "favorites");
        contentArea.add(new ShowListPanel(userData, ShowListPanel.ListType.WATCHED,   api),  "watched");
        contentArea.add(new ShowListPanel(userData, ShowListPanel.ListType.WATCHLIST, api),  "watchlist");

        return contentArea;
    }

    public UserData getUserData() { return userData; }

    private void onClose() {
        try {
            persistence.save(userData);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar dados: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
        System.exit(0);
    }
}
