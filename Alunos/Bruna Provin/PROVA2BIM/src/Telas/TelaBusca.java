package view;

import controller.SistemaController;
import model.Serie;
import util.MensagensUtil;
import util.Tradutor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Tela de busca de séries na API TVMaze.
 * Também contém, como métodos "static" (utilitários compartilhados),
 * a criação da tabela, do scroll pane e dos botões usados também pela
 * TelaListaBase — isso evita duplicar esse código de estilo visual em
 * cada tela que usa uma tabela de séries.
 */
public class TelaBusca extends JPanel {

    private final SistemaController controller;
    private final TelaPrincipal telaPrincipal;

    // Componentes principais da tela
    private JTextField campoBusca;
    private JTable tabelaResultados;
    private DefaultTableModel modeloTabela;
    // Guarda o resultado da última busca realizada, para poder abrir os
    // detalhes da série selecionada na tabela
    private List<Serie> seriesEncontradas;

    public TelaBusca(SistemaController controller, TelaPrincipal telaPrincipal) {
        this.controller = controller;
        this.telaPrincipal = telaPrincipal;
        setBackground(TelaPrincipal.COR_FUNDO);
        setLayout(new BorderLayout());
        construirInterface();
    }

    /** Atalho interno para buscar o texto já traduzido em cache (ver Tradutor.java) */
    private String t(String texto) {
        return Tradutor.getCached(texto);
    }

    /**
     * Monta toda a interface da tela: título + campo de busca no topo,
     * tabela de resultados no centro (com mensagem de instrução quando
     * ainda não há busca feita), e botão "Ver Detalhes" no rodapé.
     */
    private void construirInterface() {
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ---- Cabeçalho: título da tela ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TelaPrincipal.COR_FUNDO);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titulo = new JLabel(t("Buscar Series"));
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TelaPrincipal.COR_TEXTO);
        header.add(titulo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ---- Campo de busca + botão "Buscar" ----
        JPanel painelBusca = new JPanel(new BorderLayout(12, 0));
        painelBusca.setBackground(TelaPrincipal.COR_FUNDO);
        painelBusca.setBorder(new EmptyBorder(0, 0, 16, 0));

        campoBusca = new JTextField();
        campoBusca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusca.setForeground(TelaPrincipal.COR_TEXTO);
        campoBusca.setBackground(new Color(35, 35, 60));
        campoBusca.setCaretColor(TelaPrincipal.COR_TEXTO);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TelaPrincipal.COR_BORDA, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        // Pressionar Enter no campo também dispara a busca
        campoBusca.addActionListener(e -> realizarBusca());
        painelBusca.add(campoBusca, BorderLayout.CENTER);

        JButton btnBuscar = criarBotao(t("Buscar"), TelaPrincipal.COR_PRIMARIA);
        btnBuscar.setPreferredSize(new Dimension(110, 44));
        btnBuscar.addActionListener(e -> realizarBusca());
        painelBusca.add(btnBuscar, BorderLayout.EAST);

        add(painelBusca, BorderLayout.BEFORE_FIRST_LINE);

        // ---- Tabela de resultados ----
        String[] colunas = {t("Nome"), t("Idioma"), t("Nota"), t("Status"), t("Estreia")};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            // Impede edição direta das células pelo usuário
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelaResultados = criarTabela(modeloTabela);
        // Duplo clique em uma linha abre os detalhes da série correspondente
        tabelaResultados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirDetalhesSelecionada();
            }
        });

        JScrollPane scroll = criarScrollPane(tabelaResultados);

        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(TelaPrincipal.COR_FUNDO);

        // Mensagem de instrução mostrada acima da tabela (útil antes da
        // primeira busca, quando a tabela ainda está vazia)
        JLabel instrucao = new JLabel(t("Digite o nome de uma serie e pressione Buscar"), SwingConstants.CENTER);
        instrucao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instrucao.setForeground(TelaPrincipal.COR_SUBTEXTO);
        instrucao.setBorder(new EmptyBorder(60, 0, 0, 0));

        painelCentral.add(instrucao, BorderLayout.NORTH);
        painelCentral.add(scroll, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);

        // ---- Rodapé com o botão "Ver Detalhes" ----
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(TelaPrincipal.COR_FUNDO);
        rodape.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnDetalhes = criarBotao(t("Ver Detalhes"), TelaPrincipal.COR_CARD2);
        btnDetalhes.addActionListener(e -> abrirDetalhesSelecionada());
        rodape.add(btnDetalhes);

        add(rodape, BorderLayout.SOUTH);
    }

    /**
     * Executa a busca de séries na API TVMaze de forma assíncrona
     * (usando SwingWorker), para não travar a interface enquanto espera
     * a resposta da rede. Trata os principais casos de erro: campo
     * vazio, sem resultados, sem internet e falha genérica da API.
     */
    private void realizarBusca() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            MensagensUtil.erroCampoVazio(this, t("Pesquisar Serie"));
            return;
        }

        modeloTabela.setRowCount(0); // limpa resultados de uma busca anterior
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // cursor de "carregando"

        // SwingWorker: doInBackground roda em outra thread (não trava a tela),
        // e done() é chamado de volta na thread do Swing quando termina
        SwingWorker<List<Serie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Serie> doInBackground() throws Exception {
                return controller.buscarSeries(termo);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor()); // volta o cursor ao normal
                try {
                    seriesEncontradas = get(); // pega o resultado devolvido por doInBackground()
                    if (seriesEncontradas.isEmpty()) {
                        MensagensUtil.erroBuscaVazia(TelaBusca.this);
                    } else {
                        popularTabela(seriesEncontradas);
                    }
                } catch (Exception ex) {
                    // Diferencia erro de "sem internet" (não achou o host)
                    // de outros erros genéricos da API
                    Throwable causa = ex.getCause();
                    if (causa instanceof UnknownHostException) {
                        MensagensUtil.erroSemInternet(TelaBusca.this);
                    } else {
                        MensagensUtil.erroApi(TelaBusca.this);
                    }
                }
            }
        };
        worker.execute();
    }

    /** Limpa a tabela e a preenche novamente com os dados da lista de séries recebida */
    private void popularTabela(List<Serie> series) {
        modeloTabela.setRowCount(0);
        for (Serie s : series) {
            modeloTabela.addRow(new Object[]{
                s.getNome(),
                s.getIdioma() != null ? s.getIdioma() : "N/A",
                s.getNota() > 0 ? String.format("%.1f", s.getNota()) : "N/A",
                s.getStatus() != null ? traduzirStatus(s.getStatus()) : "N/A",
                s.getDataEstreia() != null ? s.getDataEstreia() : "N/A"
            });
        }
    }

    /**
     * Abre a tela de detalhes da série selecionada na tabela de resultados.
     * Mostra um aviso se nenhuma linha estiver selecionada.
     */
    private void abrirDetalhesSelecionada() {
        int linhaSelecionada = tabelaResultados.getSelectedRow();
        if (linhaSelecionada == -1 || seriesEncontradas == null) {
            MensagensUtil.aviso(this, t("Selecione uma serie para ver os detalhes."));
            return;
        }
        Serie serie = seriesEncontradas.get(linhaSelecionada);
        new TelaDetalhesSerie(controller, telaPrincipal, serie).setVisible(true);
    }

    /**
     * Traduz o status técnico da série (vindo em inglês da API TVMaze,
     * ex: "Running", "Ended") para um texto amigável no idioma atual da
     * interface. Diferente do restante do sistema, aqui a tradução é
     * feita "na mão" com um switch, em vez de usar a API MyMemory —
     * como são poucos valores possíveis, fica mais rápido e confiável
     * do que chamar a API de tradução toda vez.
     * Método "static" porque também é usado pela TelaListaBase.
     */
    static String traduzirStatus(String status) {
        if (status == null) return "N/A";
        String idioma = Tradutor.getIdioma();
        return switch (status) {
            case "Running" -> switch (idioma) {
                case Tradutor.PT -> "Em exibicao";
                case Tradutor.ES -> "En emision";
                default -> "Running";
            };
            case "Ended" -> switch (idioma) {
                case Tradutor.PT -> "Encerrada";
                case Tradutor.ES -> "Finalizada";
                default -> "Ended";
            };
            case "Canceled", "Cancelled" -> switch (idioma) {
                case Tradutor.PT -> "Cancelada";
                case Tradutor.ES -> "Cancelada";
                default -> "Canceled";
            };
            case "In Development" -> switch (idioma) {
                case Tradutor.PT -> "Em desenvolvimento";
                case Tradutor.ES -> "En desarrollo";
                default -> "In Development";
            };
            // Status desconhecido: devolve como veio da API, sem tradução
            default -> status;
        };
    }

    /**
     * Cria uma JTable already estilizada com o tema escuro do sistema:
     * linhas "zebradas" (cores alternadas), destaque de seleção, cabeçalho
     * customizado, sem grid visível. Método utilitário estático,
     * compartilhado entre TelaBusca e TelaListaBase para manter o mesmo
     * visual em todas as tabelas do sistema.
     */
    static JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo) {
            // prepareRenderer é chamado para cada célula antes de desenhá-la;
            // aqui customizamos a cor de fundo conforme seleção/linha par-ímpar
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(99, 102, 241, 80));
                    c.setForeground(Color.WHITE);
                } else if (row % 2 == 0) {
                    c.setBackground(TelaPrincipal.COR_CARD);
                    c.setForeground(TelaPrincipal.COR_TEXTO);
                } else {
                    // linha ímpar recebe um tom levemente diferente ("zebra")
                    c.setBackground(TelaPrincipal.COR_TABELA_ALT);
                    c.setForeground(TelaPrincipal.COR_TEXTO);
                }
                return c;
            }
        };
        tabela.setBackground(TelaPrincipal.COR_CARD);
        tabela.setForeground(TelaPrincipal.COR_TEXTO);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(36);
        tabela.setShowGrid(false); // sem linhas de grade entre as células
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // só uma linha por vez
        tabela.setFillsViewportHeight(true); // a tabela ocupa toda a altura disponível
        tabela.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Estiliza o cabeçalho da tabela (linha com os nomes das colunas)
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(new Color(20, 20, 40));
        header.setForeground(TelaPrincipal.COR_SUBTEXTO);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TelaPrincipal.COR_BORDA));
        header.setReorderingAllowed(false); // impede o usuário de arrastar/reordenar colunas

        return tabela;
    }

    /** Envolve a tabela em um JScrollPane já estilizado com as cores do tema */
    static JScrollPane criarScrollPane(JTable tabela) {
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBackground(TelaPrincipal.COR_CARD);
        scroll.getViewport().setBackground(TelaPrincipal.COR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(TelaPrincipal.COR_BORDA, 1));
        return scroll;
    }

    /**
     * Cria um botão retangular com cantos arredondados e cor de fundo
     * customizável, reutilizado em várias telas do sistema (Buscar,
     * Ver Detalhes, Remover, etc.) para manter a identidade visual.
     */
    static JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }
}
