package view;

import util.Tradutor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Componente reutilizável de seleção de idioma (bandeirinhas PT/EN/ES),
 * pensado para ser adicionado em qualquer tela que precise desse seletor.
 *
 * OBSERVAÇÃO: atualmente a TelaPrincipal não usa esta classe — ela tem seu
 * próprio método privado "criarSeletorIdioma()" com lógica praticamente
 * idêntica a esta. Esta classe fica disponível como componente genérico
 * caso outra tela precise de um seletor de idioma independente no futuro.
 */
public class SeletorIdioma extends JPanel {

    /**
     * Interface de callback usada para avisar quem criou o componente
     * de que o idioma foi trocado, para que a tela possa se reconstruir
     * (traduzir os textos novamente).
     */
    public interface OnIdiomaChanged {
        void onChange(String novoIdioma);
    }

    /**
     * @param corFundo cor de fundo (atualmente não utilizada no desenho,
     *                 já que o painel é transparente/opaque=false, mas
     *                 mantida no construtor para uso futuro)
     * @param listener função chamada quando o usuário troca o idioma
     */
    public SeletorIdioma(Color corFundo, OnIdiomaChanged listener) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));

        JLabel label = new JLabel("🌐");
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        label.setForeground(new Color(140, 140, 170));
        add(label);

        // ButtonGroup garante que só uma bandeira fique "pressionada" por vez
        ButtonGroup grupo = new ButtonGroup();

        // Cada linha: {emoji da bandeira, código do idioma}
        String[][] opcoes = {{"🇧🇷", "pt"}, {"🇺🇸", "en"}, {"🇪🇸", "es"}};

        for (String[] op : opcoes) {
            JToggleButton btn = new JToggleButton(op[0]);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            btn.setBackground(new Color(45, 45, 75));
            btn.setForeground(new Color(220, 220, 240));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 120), 1, true),
                new EmptyBorder(3, 8, 3, 8)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final String codigoIdioma = op[1];
            // Marca como selecionado o botão do idioma atualmente ativo
            if (Tradutor.getIdioma().equals(codigoIdioma)) btn.setSelected(true);

            // Ao clicar: limpa o cache de traduções antigas, define o novo
            // idioma no Tradutor e avisa quem estiver "ouvindo" (listener)
            // para que a tela se reconstrua com o novo idioma
            btn.addActionListener(e -> {
                Tradutor.limparCache();
                Tradutor.setIdioma(codigoIdioma);
                listener.onChange(codigoIdioma);
            });

            grupo.add(btn);
            add(btn);
        }
    }
}
