package tvmanager.ui;

import tvmanager.api.TVMazeClient;
import tvmanager.model.Elenco;
import tvmanager.model.Episodio;
import tvmanager.model.Serie;
import tvmanager.util.GerenciadorSeries;
import tvmanager.util.GerenciadorSeries.TipoLista;
import tvmanager.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Diálogo de detalhes de uma série.
 * Exibe todos os campos obrigatórios (req. 12) e botões para as 3 listas.
 */
public class PainelDetalhes extends JPanel {

    private final TVMazeClient client;
    private final Serie serie;
    private final GerenciadorSeries gerenciador;
    private Runnable onListasAlteradas;

    public PainelDetalhes(TVMazeClient client, Serie serie, GerenciadorSeries gerenciador) {
        this.client = client;
        this.serie = serie;
        this.gerenciador = gerenciador;
        setLayout(new BorderLayout());
        setBackground(Tema.BG_PRIMARIO);
        construir();
    }

    public void setOnListasAlteradas(Runnable r) { this.onListasAlteradas = r; }

    private void construir() {
        add(criarHeader(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setBackground(Tema.BG_PRIMARIO);
        abas.setForeground(Tema.TEXTO_PRIMARIO);
        abas.addTab("Sinopse", criarAbaSinopse());
        abas.addTab("Episódios", criarAbaEpisodios());
        abas.addTab("Elenco", criarAbaElenco());
        add(abas, BorderLayout.CENTER);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Tema.BG_SECUNDARIO);
        header.setBorder(new EmptyBorder(18, 20, 14, 20));

        // Capa
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(80, 112));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Tema.BG_PRIMARIO);
        header.add(imgLabel, BorderLayout.WEST);
        ImageLoader.carregar(serie.getImagemUrl(), 80, 112, imgLabel::setIcon);

        // Info com todos os campos obrigatórios (req. 12)
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Tema.BG_SECUNDARIO);

        info.add(label(serie.getNome(), Tema.TEXTO_PRIMARIO, Tema.FONTE_TITULO));
        info.add(Box.createVerticalStrut(4));
        info.add(label(PainelLista.join(" • ",
            serie.getGeneros(),
            serie.getIdioma(),
            serie.getEmissora()), Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(3));
        info.add(label(PainelLista.join("   ",
            "Estado: " + serie.getStatusExibicao(),
            serie.getPremiada() != null ? "Estreia: " + serie.getPremiada() : null,
            serie.getDataTermino() != null ? "Término: " + serie.getDataTermino() : null
        ), Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        info.add(Box.createVerticalStrut(3));
        info.add(label(serie.getRating() > 0
            ? "★ " + String.format("%.1f", serie.getRating()) + " / 10"
            : "★ Sem avaliação", Tema.ESTRELA, Tema.FONTE_CORPO));
        info.add(Box.createVerticalStrut(12));

        // Botões das 3 listas
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setBackground(Tema.BG_SECUNDARIO);
        botoes.add(criarBotaoLista(TipoLista.FAVORITOS,      "★ Favorito",       "☆ Favoritar"));
        botoes.add(criarBotaoLista(TipoLista.ASSISTIDAS,     "✔ Assistida",      "◻ Já assisti"));
        botoes.add(criarBotaoLista(TipoLista.QUERO_ASSISTIR, "⊕ Na lista",       "⊕ Quero assistir"));
        info.add(botoes);

        header.add(info, BorderLayout.CENTER);
        return header;
    }

    private JButton criarBotaoLista(TipoLista tipo, String textoAtivo, String textoInativo) {
        boolean ativo = gerenciador.contem(tipo, serie.getId());
        JButton btn = Tema.botaoSecundario(ativo ? textoAtivo : textoInativo);
        btn.setForeground(ativo ? Tema.VERDE : Tema.TEXTO_MUTED);

        btn.addActionListener(e -> {
            if (gerenciador.contem(tipo, serie.getId())) {
                gerenciador.remover(tipo, serie.getId());
                btn.setText(textoInativo);
                btn.setForeground(Tema.TEXTO_MUTED);
            } else {
                gerenciador.adicionar(tipo, serie);
                btn.setText(textoAtivo);
                btn.setForeground(Tema.VERDE);
            }
            if (onListasAlteradas != null) onListasAlteradas.run();
        });
        return btn;
    }

    private JLabel label(String texto, Color cor, Font fonte) {
        JLabel l = new JLabel(texto != null && !texto.isBlank() ? texto : " ");
        l.setFont(fonte);
        l.setForeground(cor);
        return l;
    }

    // --- Aba Sinopse ---

    private JPanel criarAbaSinopse() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.BG_PRIMARIO);
        p.setBorder(new EmptyBorder(14, 20, 14, 20));

        JTextArea area = new JTextArea(serie.getSinopse() != null ? serie.getSinopse() : "Sem sinopse.");
        area.setFont(Tema.FONTE_CORPO);
        area.setForeground(Tema.TEXTO_PRIMARIO);
        area.setBackground(Tema.BG_PRIMARIO);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.BG_PRIMARIO);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // --- Aba Episódios ---

    private JPanel criarAbaEpisodios() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.BG_PRIMARIO);

        JLabel carregando = new JLabel("Carregando episódios...");
        carregando.setForeground(Tema.TEXTO_MUTED);
        carregando.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(carregando, BorderLayout.CENTER);

        new SwingWorker<List<Episodio>, Void>() {
            @Override protected List<Episodio> doInBackground() throws Exception {
                return client.buscarEpisodios(serie.getId());
            }
            @Override protected void done() {
                try {
                    p.remove(carregando);
                    p.add(montarEpisodios(get()), BorderLayout.CENTER);
                    p.revalidate(); p.repaint();
                } catch (Exception ex) {
                    carregando.setText("Erro ao carregar episódios.");
                }
            }
        }.execute();
        return p;
    }

    private JPanel montarEpisodios(List<Episodio> eps) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Tema.BG_PRIMARIO);

        Map<Integer, List<Episodio>> porTemporada = new LinkedHashMap<>();
        for (Episodio e : eps)
            porTemporada.computeIfAbsent(e.getTemporada(), k -> new ArrayList<>()).add(e);

        JComboBox<Integer> combo = new JComboBox<>(porTemporada.keySet().toArray(new Integer[0]));
        combo.setBackground(Tema.BG_CARD);
        combo.setForeground(Tema.TEXTO_PRIMARIO);
        combo.setFont(Tema.FONTE_CORPO);

        JPanel topEp = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topEp.setBackground(Tema.BG_SECUNDARIO);
        JLabel lbl = new JLabel("Temporada:");
        lbl.setForeground(Tema.TEXTO_MUTED);
        lbl.setFont(Tema.FONTE_CORPO);
        topEp.add(lbl); topEp.add(combo);
        container.add(topEp, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Tema.BG_PRIMARIO);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.BG_PRIMARIO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        container.add(scroll, BorderLayout.CENTER);

        Runnable atualizar = () -> {
            lista.removeAll();
            List<Episodio> epLista = porTemporada.getOrDefault((Integer) combo.getSelectedItem(), Collections.emptyList());
            for (Episodio ep : epLista) { lista.add(criarCardEpisodio(ep)); lista.add(Box.createVerticalStrut(4)); }
            lista.revalidate(); lista.repaint();
        };
        combo.addActionListener(e -> atualizar.run());
        atualizar.run();
        return container;
    }

    private JPanel criarCardEpisodio(Episodio ep) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Tema.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.BORDA),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel esq = new JPanel(); esq.setLayout(new BoxLayout(esq, BoxLayout.Y_AXIS)); esq.setBackground(Tema.BG_CARD);
        esq.add(label(String.format("E%02d — %s", ep.getNumero(), ep.getNome() != null ? ep.getNome() : "Sem título"), Tema.TEXTO_PRIMARIO, Tema.FONTE_CORPO));
        esq.add(Box.createVerticalStrut(3));
        esq.add(label((ep.getDataAr() != null ? ep.getDataAr() : "Data desconhecida") + (ep.getDuracao() > 0 ? "  •  " + ep.getDuracao() + " min" : ""), Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA));
        card.add(esq, BorderLayout.CENTER);

        if (ep.getRating() > 0)
            card.add(label("★ " + String.format("%.1f", ep.getRating()), Tema.ESTRELA, Tema.FONTE_PEQUENA), BorderLayout.EAST);
        return card;
    }

    // --- Aba Elenco ---

    private JPanel criarAbaElenco() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.BG_PRIMARIO);

        JLabel carregando = new JLabel("Carregando elenco...");
        carregando.setForeground(Tema.TEXTO_MUTED);
        carregando.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(carregando, BorderLayout.CENTER);

        new SwingWorker<List<Elenco>, Void>() {
            @Override protected List<Elenco> doInBackground() throws Exception { return client.buscarElenco(serie.getId()); }
            @Override protected void done() {
                try {
                    p.remove(carregando);
                    p.add(montarElenco(get()), BorderLayout.CENTER);
                    p.revalidate(); p.repaint();
                } catch (Exception ex) { carregando.setText("Erro ao carregar elenco."); }
            }
        }.execute();
        return p;
    }

    private JScrollPane montarElenco(List<Elenco> elenco) {
        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
        grid.setBackground(Tema.BG_PRIMARIO);
        grid.setBorder(new EmptyBorder(12, 12, 12, 12));

        for (Elenco e : elenco) {
            JPanel card = new JPanel(new BorderLayout(0, 6));
            card.setBackground(Tema.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

            JLabel img = new JLabel();
            img.setHorizontalAlignment(SwingConstants.CENTER);
            img.setPreferredSize(new Dimension(60, 80));
            ImageLoader.carregar(e.getImagemUrl(), 60, 80, img::setIcon);

            JPanel texto = new JPanel(); texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS)); texto.setBackground(Tema.BG_CARD);
            JLabel ator = label(e.getNomeAtor() != null ? e.getNomeAtor() : "—", Tema.TEXTO_PRIMARIO, Tema.FONTE_CORPO);
            ator.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel personagem = label(e.getNomePersonagem() != null ? e.getNomePersonagem() : "—", Tema.TEXTO_MUTED, Tema.FONTE_PEQUENA);
            personagem.setHorizontalAlignment(SwingConstants.CENTER);
            texto.add(ator); texto.add(personagem);

            card.add(img, BorderLayout.CENTER);
            card.add(texto, BorderLayout.SOUTH);
            grid.add(card);
        }
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.BG_PRIMARIO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }
}
