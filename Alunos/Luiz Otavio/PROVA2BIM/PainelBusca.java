package tvmanager.ui;

import tvmanager.api.TVMazeClient;
import tvmanager.model.Serie;
import tvmanager.util.GerenciadorSeries;
import tvmanager.util.GerenciadorSeries.TipoLista;
import tvmanager.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Painel de busca de séries via TVMaze API.
 */
public class PainelBusca extends JPanel {

    private final TVMazeClient client;
    private final GerenciadorSeries gerenciador;
    private final JTextField campoBusca;
    private final JPanel painelResultados;
    private final JLabel labelStatus;
    private Runnable onListasAlteradas;

    public PainelBusca(TVMazeClient client, GerenciadorSeries gerenciador) {
        this.client = client;
        this.gerenciador = gerenciador;
        setLayout(new BorderLayout(0, 0));
        setBackground(Tema.BG_PRIMARIO);

        // Barra de busca
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(Tema.BG_SECUNDARIO);
        topBar.setBorder(new EmptyBorder(16, 20, 16, 20));

        campoBusca = new JTextField();
        campoBusca.setFont(Tema.FONTE_CORPO);
        campoBusca.setBackground(Tema.BG_CARD);
        campoBusca.setForeground(Tema.TEXTO_PRIMARIO);
        campoBusca.setCaretColor(Tema.TEXTO_PRIMARIO);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton btnBuscar = Tema.botaoPrimario("Buscar");

        topBar.add(campoBusca, BorderLayout.CENTER);
        topBar.add(btnBuscar, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Status + resultados
        labelStatus = new JLabel(" ");
        labelStatus.setFont(Tema.FONTE_PEQUENA);
        labelStatus.setForeground(Tema.TEXTO_MUTED);

        painelResultados = new JPanel();
        painelResultados.setLayout(new BoxLayout(painelResultados, BoxLayout.Y_AXIS));
        painelResultados.setBackground(Tema.BG_PRIMARIO);

        JScrollPane scroll = new JScrollPane(painelResultados);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.BG_PRIMARIO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Tema.BG_PRIMARIO);
        centro.add(labelStatus, BorderLayout.NORTH);
        centro.setBorder(new EmptyBorder(6, 20, 0, 20));
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        ActionListener buscar = e -> executarBusca();
        btnBuscar.addActionListener(buscar);
        campoBusca.addActionListener(buscar);
    }

    public void setOnListasAlteradas(Runnable r) { this.onListasAlteradas = r; }

    private void executarBusca() {
        String query = campoBusca.getText().trim();
        if (query.isEmpty()) return;

        labelStatus.setText("Buscando...");
        painelResultados.removeAll();
        painelResultados.revalidate();
        painelResultados.repaint();

        new SwingWorker<List<Serie>, Void>() {
            @Override protected List<Serie> doInBackground() throws Exception {
                return client.buscarSeries(query);
            }
            @Override protected void done() {
                try {
                    List<Serie> series = get();
                    painelResultados.removeAll();
                    if (series.isEmpty()) {
                        labelStatus.setText("Nenhum resultado para \"" + query + "\"");
                    } else {
                        labelStatus.setText(series.size() + " resultado(s) para \"" + query + "\"");
                        for (Serie s : series) {
                            painelResultados.add(criarCard(s));
                            painelResultados.add(Box.createVerticalStrut(8));
                        }
                    }
                    painelResultados.revalidate();
                    painelResultados.repaint();
                } catch (Exception ex) {
                    labelStatus.setText("Erro ao buscar: " + ex.getMessage());
                    JOptionPane.showMessageDialog(PainelBusca.this,
                        "Não foi possível realizar a busca.\nVerifique sua conexão com a internet.",
                        "Erro de conexão", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private JPanel criarCard(Serie serie) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Tema.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Capa
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(55, 78));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Tema.BG_PRIMARIO);
        card.add(imgLabel, BorderLayout.WEST);
        ImageLoader.carregar(serie.getImagemUrl(), 55, 78, imgLabel::setIcon);

        // Info central
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Tema.BG_CARD);

        info.add(lbl(serie.getNome(), Tema.TEXTO_PRIMARIO, Tema.FONTE_SUBTITULO));
        info.add(Box.createVerticalStrut(2));
        info.add(lbl(PainelLista.join(" • ", serie.getGeneros(), serie.getIdioma(), serie.getEmissora()), Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(2));
        info.add(lbl(PainelLista.join("  ", serie.getStatusExibicao(),
            serie.getPremiada() != null ? "Estreia: " + serie.getPremiada() : null), Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(2));
        info.add(lbl(serie.getRating() > 0 ? "★ " + String.format("%.1f", serie.getRating()) : "★ Sem nota", Tema.ESTRELA, Tema.FONTE_PEQUENA));
        card.add(info, BorderLayout.CENTER);

        // Botões das 3 listas
        JPanel botoes = new JPanel();
        botoes.setLayout(new BoxLayout(botoes, BoxLayout.Y_AXIS));
        botoes.setBackground(Tema.BG_CARD);
        botoes.setBorder(new EmptyBorder(0, 8, 0, 0));

        botoes.add(criarBotaoLista(serie, TipoLista.FAVORITOS,      "★", "☆", "Favoritar"));
        botoes.add(Box.createVerticalStrut(4));
        botoes.add(criarBotaoLista(serie, TipoLista.ASSISTIDAS,     "✔", "◻", "Assistida"));
        botoes.add(Box.createVerticalStrut(4));
        botoes.add(criarBotaoLista(serie, TipoLista.QUERO_ASSISTIR, "⊕", "⊕", "Quero assistir"));
        card.add(botoes, BorderLayout.EAST);

        // Clique abre detalhes
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!(e.getSource() instanceof JButton)) abrirDetalhes(serie);
            }
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(Tema.BG_HOVER); info.setBackground(Tema.BG_HOVER); botoes.setBackground(Tema.BG_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(Tema.BG_CARD); info.setBackground(Tema.BG_CARD); botoes.setBackground(Tema.BG_CARD);
            }
        };
        card.addMouseListener(ma);
        info.addMouseListener(ma);

        return card;
    }

    private JButton criarBotaoLista(Serie serie, TipoLista tipo, String simAtivo, String simInativo, String texto) {
        boolean ativo = gerenciador.contem(tipo, serie.getId());
        JButton btn = Tema.botaoSecundario((ativo ? simAtivo : simInativo) + " " + texto);
        btn.setForeground(ativo ? Tema.VERDE : Tema.TEXTO_MUTED);
        btn.setFont(Tema.FONTE_PEQUENA);
        btn.setMaximumSize(new Dimension(130, 26));

        btn.addActionListener(e -> {
            if (gerenciador.contem(tipo, serie.getId())) {
                gerenciador.remover(tipo, serie.getId());
                btn.setText(simInativo + " " + texto);
                btn.setForeground(Tema.TEXTO_MUTED);
            } else {
                gerenciador.adicionar(tipo, serie);
                btn.setText(simAtivo + " " + texto);
                btn.setForeground(Tema.VERDE);
            }
            if (onListasAlteradas != null) onListasAlteradas.run();
        });
        return btn;
    }

    private void abrirDetalhes(Serie serie) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) janela, serie.getNome(), true);
        dialog.setSize(720, 580);
        dialog.setLocationRelativeTo(janela);
        PainelDetalhes painel = new PainelDetalhes(client, serie, gerenciador);
        painel.setOnListasAlteradas(() -> { if (onListasAlteradas != null) onListasAlteradas.run(); });
        dialog.setContentPane(painel);
        dialog.setVisible(true);
    }

    private JLabel lbl(String texto, Color cor, Font fonte) {
        JLabel l = new JLabel(texto != null && !texto.isBlank() ? texto : " ");
        l.setFont(fonte); l.setForeground(cor);
        return l;
    }
}
