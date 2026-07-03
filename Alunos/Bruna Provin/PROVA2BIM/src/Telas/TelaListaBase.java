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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe base ABSTRATA para as três telas de lista do sistema:
 * TelaFavoritos, TelaAssistidas e TelaDesejoAssistir. Como as três telas
 * são praticamente idênticas (mesma tabela, mesma busca, mesma ordenação,
 * mesmos botões "Ver Detalhes"/"Remover"), toda a lógica comum fica
 * centralizada aqui, e cada subclasse só precisa dizer:
 *  - de qual lista do Usuario ela lê os dados (getListaSeries)
 *  - qual é o nome/título da tela (getTituloBase)
 *  - como remover uma série dessa lista específica (removerSerie)
 * Esse é um exemplo do padrão de projeto "Template Method".
 */
public abstract class TelaListaBase extends JPanel {

    // Controller e referência da tela principal, usados para navegar e
    // executar ações (remover série, abrir tela de detalhes, etc.)
    protected final SistemaController controller;
    protected final TelaPrincipal telaPrincipal;

    // Componentes visuais principais desta tela
    private JTextField campoPesquisa;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    // Lista completa (sem filtro de busca) das séries desta tela
    protected List<Serie> listaAtual;
    // Lista atualmente exibida na tabela (pode estar filtrada pela busca)
    private List<Serie> listaFiltrada;

    public TelaListaBase(SistemaController controller, TelaPrincipal telaPrincipal) {
        this.controller = controller;
        this.telaPrincipal = telaPrincipal;
        // Copia a lista de séries (getListaSeries é implementado por cada subclasse)
        // para uma nova ArrayList, evitando alterar diretamente a lista original do Usuario
        this.listaAtual = new ArrayList<>(getListaSeries());
        this.listaFiltrada = new ArrayList<>(listaAtual);
        setBackground(TelaPrincipal.COR_FUNDO);
        setLayout(new BorderLayout());
        construirInterface();
    }

    /** Atalho interno para buscar o texto já traduzido em cache (ver Tradutor.java) */
    private String t(String texto) {
        return Tradutor.getCached(texto);
    }

    // ==================== MÉTODOS ABSTRATOS ====================
    // Cada subclasse (TelaFavoritos, TelaAssistidas, TelaDesejoAssistir)
    // precisa implementar estes três métodos com seu comportamento específico.

    /** Retorna a lista de séries do Usuario que esta tela deve exibir */
    protected abstract List<Serie> getListaSeries();

    /** Palavra-base do título da tela (ex: "Favoritos"), sem o contador numérico. */
    protected abstract String getTituloBase();

    /** Define como remover uma série especificamente desta lista (favoritos, assistidas ou desejo) */
    protected abstract void removerSerie(Serie serie);

    /**
     * Monta toda a interface da tela: cabeçalho (título + campo de busca),
     * tabela de séries no centro, e barra de ações (ordenação + botões) no rodapé.
     */
    private void construirInterface() {
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ---- Cabeçalho: título à esquerda, campo de pesquisa à direita ----
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(TelaPrincipal.COR_FUNDO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Título traduzido + contador de itens fora da tradução (ex: "Favoritos (9)")
        JLabel titulo = new JLabel(t(getTituloBase()) + " (" + listaAtual.size() + ")");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TelaPrincipal.COR_TEXTO);
        header.add(titulo, BorderLayout.WEST);

        campoPesquisa = new JTextField();
        campoPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoPesquisa.setForeground(TelaPrincipal.COR_TEXTO);
        campoPesquisa.setBackground(new Color(35, 35, 60));
        campoPesquisa.setCaretColor(TelaPrincipal.COR_TEXTO);
        campoPesquisa.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TelaPrincipal.COR_BORDA, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));

        // Simula um "placeholder" (texto de exemplo) no campo de busca, já que o
        // Swing não tem essa funcionalidade pronta: mostra "Pesquisar..." quando
        // vazio e sem foco, e some automaticamente ao clicar no campo.
        String placeholder = t("Pesquisar...");
        campoPesquisa.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                // Ao ganhar foco, se ainda estiver mostrando o placeholder, limpa o campo
                if (campoPesquisa.getText().equals(placeholder)) campoPesquisa.setText("");
            }
            @Override public void focusLost(FocusEvent e) {
                // Ao perder foco, se o usuário não digitou nada, volta o placeholder
                // e limpa qualquer filtro de busca ativo
                if (campoPesquisa.getText().isEmpty()) {
                    campoPesquisa.setText(placeholder);
                    filtrarLista("");
                }
            }
        });
        campoPesquisa.setText(placeholder);
        // A cada tecla digitada (soltar a tecla), filtra a lista em tempo real
        campoPesquisa.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String texto = campoPesquisa.getText();
                if (!texto.equals(placeholder)) filtrarLista(texto);
            }
        });

        JPanel pesquisaPanel = new JPanel(new BorderLayout());
        pesquisaPanel.setBackground(TelaPrincipal.COR_FUNDO);
        pesquisaPanel.setPreferredSize(new Dimension(240, 38));
        pesquisaPanel.add(campoPesquisa, BorderLayout.CENTER);
        header.add(pesquisaPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---- Tabela de séries no centro ----
        // Colunas traduzidas: Nome, Idioma, Nota, Status, Estreia
        String[] colunas = {t("Nome"), t("Idioma"), t("Nota"), t("Status"), t("Estreia")};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            // Impede que o usuário edite diretamente o conteúdo das células da tabela
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Reaproveita os métodos utilitários estáticos de TelaBusca para criar
        // a tabela e o scroll com o mesmo visual usado na tela de busca
        tabela = TelaBusca.criarTabela(modeloTabela);
        // Duplo clique em uma linha da tabela abre os detalhes da série
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirDetalhes();
            }
        });

        popularTabela(listaFiltrada);

        JScrollPane scroll = TelaBusca.criarScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);

        // ---- Barra de ações no rodapé (ordenação + botões) ----
        JPanel barraAcoes = criarBarraAcoes();
        add(barraAcoes, BorderLayout.SOUTH);
    }

    /**
     * Monta a barra inferior com os botões de ordenação (à esquerda) e os
     * botões de ação "Ver Detalhes"/"Remover" (à direita).
     */
    private JPanel criarBarraAcoes() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(TelaPrincipal.COR_FUNDO);
        barra.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel ordenacao = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        ordenacao.setBackground(TelaPrincipal.COR_FUNDO);

        JLabel lblOrdenar = new JLabel(t("Ordenar") + ":");
        lblOrdenar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOrdenar.setForeground(TelaPrincipal.COR_SUBTEXTO);
        ordenacao.add(lblOrdenar);

        // Cada botão de ordenação recebe um Comparator diferente, aplicado
        // sobre a lista de séries quando clicado. "A-Z" não é traduzido
        // (é uma sigla universal, igual em qualquer idioma).
        adicionarBotaoOrdenacao(ordenacao, "A-Z", () -> ordenarPor(Comparator.comparing(s -> s.getNome() != null ? s.getNome() : "")));
        adicionarBotaoOrdenacao(ordenacao, t("Melhor Nota"), () -> ordenarPor(Comparator.comparingDouble(Serie::getNota).reversed()));
        adicionarBotaoOrdenacao(ordenacao, t("Pior Nota"), () -> ordenarPor(Comparator.comparingDouble(Serie::getNota)));
        adicionarBotaoOrdenacao(ordenacao, t("Status"), () -> ordenarPor(Comparator.comparing(s -> statusOrdem(s.getStatus()))));
        adicionarBotaoOrdenacao(ordenacao, t("Estreia"), () -> ordenarPor(Comparator.comparing(
            s -> s.getDataEstreia() != null ? s.getDataEstreia() : "", Comparator.reverseOrder()
        )));

        barra.add(ordenacao, BorderLayout.WEST);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acoes.setBackground(TelaPrincipal.COR_FUNDO);

        JButton btnDetalhes = TelaBusca.criarBotao(t("Ver Detalhes"), TelaPrincipal.COR_CARD2);
        btnDetalhes.addActionListener(e -> abrirDetalhes());
        acoes.add(btnDetalhes);

        JButton btnRemover = TelaBusca.criarBotao(t("Remover"), TelaPrincipal.COR_PERIGO);
        btnRemover.addActionListener(e -> removerSelecionada());
        acoes.add(btnRemover);

        barra.add(acoes, BorderLayout.EAST);
        return barra;
    }

    /** Cria um pequeno botão de ordenação (estilo "chip") e adiciona ao painel informado */
    private void adicionarBotaoOrdenacao(JPanel painel, String texto, Runnable acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(TelaPrincipal.COR_SUBTEXTO);
        btn.setBackground(new Color(35, 35, 60));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TelaPrincipal.COR_BORDA, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Executa a ação (Runnable) recebida quando o botão é clicado
        btn.addActionListener(e -> acao.run());
        painel.add(btn);
    }

    /**
     * Limpa a tabela e a repreenche com os dados da lista de séries recebida.
     * Chamado sempre que a lista muda: ao montar a tela, filtrar, ordenar ou remover.
     */
    protected void popularTabela(List<Serie> series) {
        modeloTabela.setRowCount(0); // remove todas as linhas atuais
        for (Serie s : series) {
            modeloTabela.addRow(new Object[]{
                s.getNome(),
                s.getIdioma() != null ? s.getIdioma() : "N/A",
                s.getNota() > 0 ? String.format("%.1f", s.getNota()) : "N/A",
                TelaBusca.traduzirStatus(s.getStatus()), // status traduzido (Running/Ended/etc.)
                s.getDataEstreia() != null ? s.getDataEstreia() : "N/A"
            });
        }
    }

    /**
     * Filtra a listaAtual por nome de série (ignorando maiúsculas/minúsculas)
     * conforme o termo digitado no campo de pesquisa, e atualiza a tabela.
     */
    private void filtrarLista(String termo) {
        if (termo.isEmpty()) {
            // Sem termo de busca: mostra a lista completa
            listaFiltrada = new ArrayList<>(listaAtual);
        } else {
            String t = termo.toLowerCase();
            // Usa Stream para filtrar apenas as séries cujo nome contém o termo buscado
            listaFiltrada = listaAtual.stream()
                .filter(s -> s.getNome() != null && s.getNome().toLowerCase().contains(t))
                .collect(java.util.stream.Collectors.toList());
        }
        popularTabela(listaFiltrada);
    }

    /**
     * Ordena tanto a lista completa quanto a lista filtrada usando o
     * Comparator recebido, e atualiza a tabela com o novo resultado.
     */
    private void ordenarPor(Comparator<Serie> comparator) {
        listaAtual.sort(comparator);
        listaFiltrada.sort(comparator);
        popularTabela(listaFiltrada);
    }

    /**
     * Define uma ordem de prioridade numérica para os status, usada pelo
     * botão de ordenação "Status": séries em exibição (Running) aparecem
     * primeiro, depois encerradas (Ended), depois canceladas, e por
     * último qualquer outro status desconhecido.
     */
    private int statusOrdem(String status) {
        return switch (status != null ? status : "") {
            case "Running" -> 0;
            case "Ended" -> 1;
            case "Canceled", "Cancelled" -> 2;
            default -> 3;
        };
    }

    /**
     * Abre a janela de detalhes (TelaDetalhesSerie) da série selecionada
     * na tabela. Se nenhuma linha estiver selecionada, mostra um aviso.
     */
    private void abrirDetalhes() {
        int linha = tabela.getSelectedRow();
        if (linha == -1 || listaFiltrada.isEmpty()) {
            MensagensUtil.aviso(this, t("Selecione uma serie para ver os detalhes."));
            return;
        }
        new TelaDetalhesSerie(controller, telaPrincipal, listaFiltrada.get(linha)).setVisible(true);
    }

    /**
     * Remove a série selecionada na tabela desta lista específica, após
     * pedir confirmação ao usuário. Atualiza tanto a lista em memória
     * quanto a tabela na tela.
     */
    private void removerSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            MensagensUtil.aviso(this, t("Selecione uma serie para remover."));
            return;
        }
        Serie serie = listaFiltrada.get(linha);
        // Monta a pergunta de confirmação juntando trechos traduzidos com o
        // nome da série (que nunca é traduzido, por ser um nome próprio)
        if (MensagensUtil.confirmar(this, t("Remover") + " \"" + serie.getNome() + "\" " + t("desta lista?") + "?")) {
            removerSerie(serie); // chama a implementação específica da subclasse (controller.removerFavorito, etc.)
            listaAtual.remove(serie);
            listaFiltrada.remove(serie);
            popularTabela(listaFiltrada);
        }
    }
}
