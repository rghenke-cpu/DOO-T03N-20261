package tvmanager.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Tema {
    // Paleta escura estilo streaming
    public static final Color BG_PRIMARIO    = new Color(13, 13, 20);
    public static final Color BG_SECUNDARIO  = new Color(22, 22, 35);
    public static final Color BG_CARD        = new Color(30, 30, 46);
    public static final Color BG_HOVER       = new Color(40, 40, 60);
    public static final Color ACENTO         = new Color(229, 57, 53);    // vermelho Netflix-ish
    public static final Color ACENTO_SUAVE   = new Color(183, 28, 28);
    public static final Color TEXTO_PRIMARIO = new Color(240, 240, 255);
    public static final Color TEXTO_MUTED    = new Color(140, 140, 160);
    public static final Color BORDA          = new Color(45, 45, 65);
    public static final Color ESTRELA        = new Color(255, 200, 50);
    public static final Color VERDE          = new Color(76, 175, 80);
    public static final Color CINZA_MEDIO    = new Color(80, 80, 100);

    public static final Font FONTE_TITULO    = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONTE_CORPO     = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONTE_PEQUENA   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONTE_LOGO      = new Font("SansSerif", Font.BOLD, 26);

    public static void aplicar() {
        UIManager.put("Panel.background", BG_PRIMARIO);
        UIManager.put("ScrollPane.background", BG_PRIMARIO);
        UIManager.put("Viewport.background", BG_PRIMARIO);
        UIManager.put("List.background", BG_SECUNDARIO);
        UIManager.put("List.foreground", TEXTO_PRIMARIO);
        UIManager.put("List.selectionBackground", ACENTO);
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("TextField.background", BG_CARD);
        UIManager.put("TextField.foreground", TEXTO_PRIMARIO);
        UIManager.put("TextField.caretForeground", TEXTO_PRIMARIO);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDA),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        UIManager.put("Button.background", ACENTO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("ScrollBar.background", BG_SECUNDARIO);
        UIManager.put("ScrollBar.thumb", CINZA_MEDIO);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("TabbedPane.background", BG_PRIMARIO);
        UIManager.put("TabbedPane.foreground", TEXTO_PRIMARIO);
        UIManager.put("TabbedPane.selected", BG_CARD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("Label.foreground", TEXTO_PRIMARIO);
        UIManager.put("TextArea.background", BG_CARD);
        UIManager.put("TextArea.foreground", TEXTO_PRIMARIO);
        UIManager.put("ComboBox.background", BG_CARD);
        UIManager.put("ComboBox.foreground", TEXTO_PRIMARIO);
        UIManager.put("ComboBox.selectionBackground", ACENTO);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("OptionPane.background", BG_SECUNDARIO);
        UIManager.put("OptionPane.messageForeground", TEXTO_PRIMARIO);
    }

    public static JButton botaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONTE_SUBTITULO);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton botaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXTO_PRIMARIO);
        btn.setFont(FONTE_CORPO);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDA),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static Border bordaCard() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDA),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        );
    }
}
