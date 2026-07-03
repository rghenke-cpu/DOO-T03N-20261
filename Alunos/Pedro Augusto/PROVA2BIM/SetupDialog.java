import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SetupDialog extends JDialog {
    private String username;
    private JTextField nameField;

    public SetupDialog(Frame parent) {
        super(parent, "Bem-vindo ao TV Tracker", true);
        build();
        setLocationRelativeTo(parent);
    }

    private void build() {
        setSize(420, 260);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_MEDIUM);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("TV Tracker");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        JLabel sub = new JLabel("Acompanhe suas séries favoritas");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.FG_MUTED);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(Theme.BG_DARK);
        titlePanel.add(title);
        titlePanel.add(sub);
        header.add(titlePanel, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_MEDIUM);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel label = new JLabel("Como posso te chamar?");
        label.setFont(Theme.FONT_HEADER);
        label.setForeground(Theme.FG_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(label, gbc);

        nameField = new JTextField();
        nameField.setFont(Theme.FONT_BODY);
        nameField.setBackground(Theme.BG_LIGHT);
        nameField.setForeground(Theme.FG_PRIMARY);
        nameField.setCaretColor(Theme.ACCENT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(nameField, gbc);

        JButton confirm = createAccentButton("Começar");
        confirm.addActionListener(e -> onConfirm());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(confirm, gbc);

        root.add(form, BorderLayout.CENTER);

        nameField.addActionListener(e -> onConfirm());

        add(root);
    }

    private void onConfirm() {
        String text = nameField.getText().trim();
        if (text.isEmpty()) {
            nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.DANGER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            nameField.requestFocus();
            return;
        }
        username = text;
        dispose();
    }

    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_HEADER);
        btn.setBackground(Theme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_DARK),
            new EmptyBorder(8, 0, 8, 0)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.ACCENT);
            }
        });
        return btn;
    }

    public String getUsername() {
        return username;
    }
}
