package tvmanager.ui;

import tvmanager.api.TVMazeClient;
import tvmanager.model.Serie;
import tvmanager.util.GerenciadorSeries;
import tvmanager.util.GerenciadorSeries.OrdemLista;
import tvmanager.util.GerenciadorSeries.TipoLista;
import tvmanager.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Painel genérico reutilizável para exibir qualquer lista de séries
 * (favoritos, assistidas, quero assistir) com ordenação e remoção.
 * Elimina a duplicação que existia entre os painéis de lista.
 */
public class PainelLista extends JPanel {

    private final TVMazeClient client;
    private final GerenciadorSeries gerenciador;
    private final TipoLista tipo;
    private final String tituloExibicao;
    private final JPanel painelCards;
    private final JComboBox<OrdemLista> comboOrdem;

    public PainelLista(TVMazeClient client, GerenciadorSeries gerenciador,
                       TipoLista tipo, String tituloExibicao) {
        this.client = client;
        this.gerenciador = gerenciador;
        this.tipo = tipo;
        this.tituloExibicao = tituloExibicao;

        setLayout(new BorderLayout());
        setBackground(Tema.BG_PRIMARIO);

        // --- Cabeçalho com título + ordenação ---
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Tema.BG_SECUNDARIO);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel titulo = new JLabel(tituloExibicao);
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO_PRIMARIO);

        JPanel ordemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ordemPanel.setBackground(Tema.BG_SECUNDARIO);
        JLabel lblOrdem = new JLabel("Ordenar por:");
        lblOrdem.setFont(Tema.FONTE_PEQUENA);
        lblOrdem.setForeground(Tema.TEXTO_MUTED);

        comboOrdem = new JComboBox<>(OrdemLista.values());
        comboOrdem.setBackground(Tema.BG_CARD);
        comboOrdem.setForeground(Tema.TEXTO_PRIMARIO);
        comboOrdem.setFont(Tema.FONTE_CORPO);
        comboOrdem.addActionListener(e -> atualizar());

        ordemPanel.add(lblOrdem);
        ordemPanel.add(comboOrdem);

        header.add(titulo, BorderLayout.WEST);
        header.add(ordemPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Lista de cards ---
        painelCards = new JPanel();
        painelCards.setLayout(new BoxLayout(painelCards, BoxLayout.Y_AXIS));
        painelCards.setBackground(Tema.BG_PRIMARIO);
        painelCards.setBorder(new EmptyBorder(8, 16, 8, 16));

        JScrollPane scroll = new JScrollPane(painelCards);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.BG_PRIMARIO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        atualizar();
    }

    /** Recarrega e redesenha a lista com a ordenação atual. */
    public void atualizar() {
        painelCards.removeAll();
        OrdemLista ordem = (OrdemLista) comboOrdem.getSelectedItem();
        List<Serie> lista = gerenciador.listar(tipo, ordem);

        if (lista.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma série aqui ainda.");
            vazio.setFont(Tema.FONTE_CORPO);
            vazio.setForeground(Tema.TEXTO_MUTED);
            vazio.setHorizontalAlignment(SwingConstants.CENTER);
            vazio.setBorder(new EmptyBorder(40, 0, 0, 0));
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelCards.add(vazio);
        } else {
            for (Serie s : lista) {
                painelCards.add(criarCard(s));
                painelCards.add(Box.createVerticalStrut(6));
            }
        }

        painelCards.revalidate();
        painelCards.repaint();
    }

    private JPanel criarCard(Serie serie) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Tema.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Capa
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(50, 70));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Tema.BG_PRIMARIO);
        card.add(imgLabel, BorderLayout.WEST);
        ImageLoader.carregar(serie.getImagemUrl(), 50, 70, imgLabel::setIcon);

        // Informações obrigatórias pelo requisito 12
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Tema.BG_CARD);

        info.add(labelInfo(serie.getNome(), Tema.TEXTO_PRIMARIO, Tema.FONTE_SUBTITULO));
        info.add(Box.createVerticalStrut(2));
        info.add(labelInfo(
            join(" • ", serie.getGeneros(), serie.getIdioma(), serie.getEmissora()),
            Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(2));
        info.add(labelInfo(
            join("  ", serie.getStatusExibicao(),
                 serie.getPremiada() != null ? "Estreia: " + serie.getPremiada() : null,
                 serie.getDataTermino() != null ? "Término: " + serie.getDataTermino() : null),
            Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(2));

        String ratingTxt = serie.getRating() > 0
            ? "★ " + String.format("%.1f", serie.getRating())
            : "★ Sem nota";
        info.add(labelInfo(ratingTxt, Tema.ESTRELA, Tema.FONTE_PEQUENA));
        card.add(info, BorderLayout.CENTER);

        // Botão remover
        JButton btnRemover = Tema.botaoSecundario("✕");
        btnRemover.setForeground(Tema.ACENTO);
        btnRemover.setPreferredSize(new Dimension(36, 36));
        JPanel leste = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        leste.setBackground(Tema.BG_CARD);
        leste.add(btnRemover);
        card.add(leste, BorderLayout.EAST);

        btnRemover.addActionListener(e -> {
            gerenciador.remover(tipo, serie.getId());
            atualizar();
        });

        // Hover
        Color[] bg = {Tema.BG_CARD};
        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(Tema.BG_HOVER);
                info.setBackground(Tema.BG_HOVER);
                leste.setBackground(Tema.BG_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(Tema.BG_CARD);
                info.setBackground(Tema.BG_CARD);
                leste.setBackground(Tema.BG_CARD);
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getSource() != btnRemover) abrirDetalhes(serie);
            }
        };
        card.addMouseListener(hover);
        info.addMouseListener(hover);

        return card;
    }

    private void abrirDetalhes(Serie serie) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) janela, serie.getNome(), true);
        dialog.setSize(720, 580);
        dialog.setLocationRelativeTo(janela);
        PainelDetalhes painel = new PainelDetalhes(client, serie, gerenciador);
        painel.setOnListasAlteradas(this::atualizar);
        dialog.setContentPane(painel);
        dialog.setVisible(true);
    }

    private JLabel labelInfo(String texto, Color cor, Font fonte) {
        JLabel l = new JLabel(texto != null && !texto.isBlank() ? texto : " ");
        l.setFont(fonte);
        l.setForeground(cor);
        return l;
    }

    /** Une partes não nulas/vazias com separador. */
    public static String join(String sep, String... partes) {
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (p != null && !p.isBlank()) {
                if (sb.length() > 0) sb.append(sep);
                sb.append(p);
            }
        }
        return sb.length() > 0 ? sb.toString() : " ";
    }
}
