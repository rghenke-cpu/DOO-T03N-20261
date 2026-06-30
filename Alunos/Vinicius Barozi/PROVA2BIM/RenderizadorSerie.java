

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RenderizadorSerie extends JLabel implements ListCellRenderer<Show> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Show> list, Show value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value == null ? "" : value.getDisplayLabel());
        setOpaque(true);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
