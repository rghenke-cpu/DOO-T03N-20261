package fag.util;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

// tematizar a interface
public class EstiloUtil {

    public static final Color COR_FUNDO = new Color(18, 18, 24);
    public static final Color COR_PAINEL = new Color(30, 30, 40);
    public static final Color COR_CAMPO = new Color(42, 42, 54);
    public static final Color COR_TEXTO = new Color(235, 235, 240);
    public static final Color COR_TEXTO_SECUNDARIO = new Color(190, 190, 200);
    public static final Color COR_DESTAQUE = new Color(90, 130, 255);
    public static final Color COR_BOTAO = new Color(55, 75, 130);
    public static final Color COR_BOTAO_SAIR = new Color(130, 55, 55);

    // Configura cores globais usadas por caixas de diálogo
    public static void configurarTemaGlobal() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Não foi possível aplicar o tema visual: " + e.getMessage());
        }

        UIManager.put("OptionPane.background", COR_PAINEL);
        UIManager.put("Panel.background", COR_PAINEL);
        UIManager.put("OptionPane.messageForeground", COR_TEXTO);
        UIManager.put("Button.background", COR_BOTAO);
        UIManager.put("Button.foreground", Color.WHITE);
    }

    public static void estilizarPainel(JPanel painel) {
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    public static void estilizarPainelInterno(JPanel painel) {
        painel.setBackground(COR_PAINEL);
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public static void estilizarTitulo(JLabel label) {
        label.setForeground(COR_TEXTO);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setBorder(new EmptyBorder(15, 10, 15, 10));
    }

    public static void estilizarLabel(JLabel label) {
        label.setForeground(COR_TEXTO_SECUNDARIO);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    public static void estilizarBotao(JButton botao) {
        botao.setBackground(COR_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBorder(new EmptyBorder(10, 14, 10, 14));
    }

    public static void estilizarBotaoSair(JButton botao) {
        estilizarBotao(botao);
        botao.setBackground(COR_BOTAO_SAIR);
    }

    public static void estilizarCampoTexto(JTextField campo) {
        campo.setBackground(COR_CAMPO);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(Color.WHITE);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBorder(new EmptyBorder(8, 8, 8, 8));
    }

    public static void estilizarAreaTexto(JTextArea area) {
        area.setBackground(COR_CAMPO);
        area.setForeground(COR_TEXTO);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public static void estilizarLista(JList<?> lista) {
        lista.setBackground(COR_CAMPO);
        lista.setForeground(COR_TEXTO);
        lista.setSelectionBackground(COR_DESTAQUE);
        lista.setSelectionForeground(Color.WHITE);
        lista.setFont(new Font("Arial", Font.PLAIN, 14));
        lista.setFixedCellHeight(28);
    }

    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(COR_CAMPO);
        combo.setForeground(COR_TEXTO);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    public static void estilizarScroll(JScrollPane scroll) {
        scroll.getViewport().setBackground(COR_CAMPO);
        scroll.setBorder(null);
    }

    public static void aplicarFundo(Component componente) {
        if (componente instanceof JComponent) {
            ((JComponent) componente).setBackground(COR_FUNDO);
        }
    }

    // Confirmação pra fechar o programa
    public static boolean confirmarFechamento(Component componente) {
        int resposta = JOptionPane.showConfirmDialog(
                componente,
                "Deseja realmente fechar o sistema?",
                "Confirmar saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return resposta == JOptionPane.YES_OPTION;
    }
}