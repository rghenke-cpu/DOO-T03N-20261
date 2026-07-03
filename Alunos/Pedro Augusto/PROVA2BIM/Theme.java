import java.awt.*;

public class Theme {
    public static final Color BG_DARK    = new Color(0x1E1E1E);
    public static final Color BG_MEDIUM  = new Color(0x2D2D2D);
    public static final Color BG_LIGHT   = new Color(0x3A3A3A);
    public static final Color FG_PRIMARY = new Color(0xE0E0E0);
    public static final Color FG_MUTED   = new Color(0x9E9E9E);
    public static final Color ACCENT     = new Color(0x4CAF50);
    public static final Color ACCENT_DARK  = new Color(0x388E3C);
    public static final Color ACCENT_HOVER = new Color(0x66BB6A);
    public static final Color DANGER     = new Color(0xEF5350);
    public static final Color BORDER     = new Color(0x424242);

    public static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);

    public static void apply() {
        javax.swing.UIManager.put("Panel.background",              BG_DARK);
        javax.swing.UIManager.put("OptionPane.background",         BG_MEDIUM);
        javax.swing.UIManager.put("OptionPane.messageForeground",  FG_PRIMARY);
        javax.swing.UIManager.put("Label.foreground",              FG_PRIMARY);
        javax.swing.UIManager.put("Label.background",              BG_DARK);
        javax.swing.UIManager.put("TextField.background",          BG_LIGHT);
        javax.swing.UIManager.put("TextField.foreground",          FG_PRIMARY);
        javax.swing.UIManager.put("TextField.caretForeground",     ACCENT);
        javax.swing.UIManager.put("TextField.border",
            javax.swing.BorderFactory.createLineBorder(BORDER));
        javax.swing.UIManager.put("TextArea.background",           BG_LIGHT);
        javax.swing.UIManager.put("TextArea.foreground",           FG_PRIMARY);
        javax.swing.UIManager.put("Button.background",             BG_LIGHT);
        javax.swing.UIManager.put("Button.foreground",             FG_PRIMARY);
        javax.swing.UIManager.put("Button.border",
            javax.swing.BorderFactory.createLineBorder(BORDER));
        javax.swing.UIManager.put("ScrollPane.background",         BG_DARK);
        javax.swing.UIManager.put("ScrollBar.background",          BG_MEDIUM);
        javax.swing.UIManager.put("ScrollBar.thumb",               BG_LIGHT);
        javax.swing.UIManager.put("List.background",               BG_MEDIUM);
        javax.swing.UIManager.put("List.foreground",               FG_PRIMARY);
        javax.swing.UIManager.put("List.selectionBackground",      ACCENT_DARK);
        javax.swing.UIManager.put("List.selectionForeground",      Color.WHITE);
        javax.swing.UIManager.put("ComboBox.background",           BG_LIGHT);
        javax.swing.UIManager.put("ComboBox.foreground",           FG_PRIMARY);
        javax.swing.UIManager.put("ComboBox.selectionBackground",  ACCENT_DARK);
        javax.swing.UIManager.put("ComboBox.selectionForeground",  Color.WHITE);
        javax.swing.UIManager.put("Table.background",              BG_MEDIUM);
        javax.swing.UIManager.put("Table.foreground",              FG_PRIMARY);
        javax.swing.UIManager.put("Table.selectionBackground",     ACCENT_DARK);
        javax.swing.UIManager.put("Table.selectionForeground",     Color.WHITE);
        javax.swing.UIManager.put("Table.gridColor",               BORDER);
        javax.swing.UIManager.put("TableHeader.background",        BG_LIGHT);
        javax.swing.UIManager.put("TableHeader.foreground",        ACCENT);
        javax.swing.UIManager.put("Separator.foreground",          BORDER);
        javax.swing.UIManager.put("ToolTip.background",            BG_LIGHT);
        javax.swing.UIManager.put("ToolTip.foreground",            FG_PRIMARY);
    }
}
