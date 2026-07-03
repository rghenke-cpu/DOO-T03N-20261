package br.escola.ui;

import br.escola.model.Serie;
import br.escola.service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Tela que exibe as 3 listas do usuário com opções de remoção e ordenação
public class TelaListas extends JFrame {

    private final UsuarioService usuarioService;

    private JTabbedPane abas;
    private DefaultListModel<Serie> modeloFavoritos;
    private DefaultListModel<Serie> modeloAssistidas;
    private DefaultListModel<Serie> modeloQueroAssistir;

    public TelaListas(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        configurarJanela();
        construirLayout();
        carregarListas(); // Preenche as listas com os dados do usuário
    }

    private void configurarJanela() {
        setTitle("Minhas Listas");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirLayout() {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Minhas Listas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.BOLD, 13));
        abas.addTab("⭐ Favoritos",      criarPainelLista("favoritos"));
        abas.addTab("✅ Já Assisti",     criarPainelLista("assistidas"));
        abas.addTab("📋 Quero Assistir", criarPainelLista("queroAssistir"));

        JPanel painelInferior = new JPanel(new BorderLayout(0, 8));

        JPanel painelOrdenacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        painelOrdenacao.add(new JLabel("Ordenar por:"));

        JButton btnNome   = new JButton("Nome");
        JButton btnNota   = new JButton("Nota");
        JButton btnStatus = new JButton("Status");
        JButton btnData   = new JButton("Data de Estreia");

        btnNome.addActionListener(e -> ordenarListaAtual("nome"));
        btnNota.addActionListener(e -> ordenarListaAtual("nota"));
        btnStatus.addActionListener(e -> ordenarListaAtual("status"));
        btnData.addActionListener(e -> ordenarListaAtual("data"));

        painelOrdenacao.add(btnNome);
        painelOrdenacao.add(btnNota);
        painelOrdenacao.add(btnStatus);
        painelOrdenacao.add(btnData);

        JButton btnVoltar = new JButton("← Voltar ao Menu");
        btnVoltar.addActionListener(e -> dispose());

        painelInferior.add(painelOrdenacao, BorderLayout.CENTER);
        painelInferior.add(btnVoltar, BorderLayout.SOUTH);

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(abas, BorderLayout.CENTER);
        painel.add(painelInferior, BorderLayout.SOUTH);

        add(painel);
    }

    // Cria o painel de cada aba com a lista e botão de remover
    // Reutiliza o mesmo método para as 3 abas
    private JPanel criarPainelLista(String tipoLista) {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        DefaultListModel<Serie> modelo = new DefaultListModel<>();

        switch (tipoLista) {
            case "favoritos"     -> modeloFavoritos = modelo;
            case "assistidas"    -> modeloAssistidas = modelo;
            case "queroAssistir" -> modeloQueroAssistir = modelo;
        }

        JList<Serie> jList = new JList<>(modelo);
        jList.setFont(new Font("Arial", Font.PLAIN, 13));
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Duplo clique exibe os detalhes
        jList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) exibirDetalhes(jList.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(jList);

        JButton btnRemover = new JButton("Remover da lista");
        btnRemover.setFont(new Font("Arial", Font.BOLD, 13));
        btnRemover.addActionListener(e -> removerDaLista(tipoLista, jList, modelo));

        painel.add(scroll, BorderLayout.CENTER);
        painel.add(btnRemover, BorderLayout.SOUTH);
        return painel;
    }

    private void carregarListas() {
        carregarModelo(modeloFavoritos,     usuarioService.getUsuario().getFavoritos());
        carregarModelo(modeloAssistidas,    usuarioService.getUsuario().getAssistidas());
        carregarModelo(modeloQueroAssistir, usuarioService.getUsuario().getQueroAssistir());
    }

    private void carregarModelo(DefaultListModel<Serie> modelo, List<Serie> series) {
        modelo.clear();
        for (Serie s : series) modelo.addElement(s);
    }

    private void removerDaLista(String tipoLista, JList<Serie> jList, DefaultListModel<Serie> modelo) {
        Serie serie = jList.getSelectedValue();
        if (serie == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série para remover.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (tipoLista) {
            case "favoritos"     -> usuarioService.removerFavorito(serie);
            case "assistidas"    -> usuarioService.removerAssistida(serie);
            case "queroAssistir" -> usuarioService.removerQueroAssistir(serie);
        }

        modelo.removeElement(serie);
        JOptionPane.showMessageDialog(this, "\"" + serie.getNome() + "\" removida da lista.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ordenarListaAtual(String criterio) {
        // Identifica qual aba está aberta (0 = favoritos, 1 = assistidas, 2 = queroAssistir)
        int abaSelecionada = abas.getSelectedIndex();
        List<Serie> lista = switch (abaSelecionada) {
            case 0 -> usuarioService.getUsuario().getFavoritos();
            case 1 -> usuarioService.getUsuario().getAssistidas();
            case 2 -> usuarioService.getUsuario().getQueroAssistir();
            default -> null;
        };

        if (lista == null) return;

        switch (criterio) {
            case "nome"   -> usuarioService.ordenarPorNome(lista);
            case "nota"   -> usuarioService.ordenarPorNota(lista);
            case "status" -> usuarioService.ordenarPorStatus(lista);
            case "data"   -> usuarioService.ordenarPorDataEstreia(lista);
        }

        carregarListas();
    }

    private void exibirDetalhes(Serie serie) {
        if (serie == null) return;

        String detalhes = String.format(
                "Nome: %s\nIdioma: %s\nGêneros: %s\nNota: %.1f\nStatus: %s\n" +
                "Estreia: %s\nTérmino: %s\nEmissora: %s\n\nSinopse:\n%s",
                serie.getNome(), serie.getIdioma(), serie.getGeneros(),
                serie.getNota(), serie.getStatus(), serie.getDataEstreia(),
                serie.getDataTermino(), serie.getEmissora(), serie.getSumario()
        );

        JTextArea textArea = new JTextArea(detalhes);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(420, 300));

        JOptionPane.showMessageDialog(this, scroll, "Detalhes: " + serie.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
}
