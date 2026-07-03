
package br.escola.ui;

import br.escola.model.Serie;
import br.escola.service.TVMazeService;
import br.escola.service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Tela de busca de séries — permite pesquisar pelo nome e adicionar às listas
public class TelaBusca extends JFrame {

    private final UsuarioService usuarioService;
    private final TVMazeService tvMazeService;

    private JTextField campoBusca;
    private JList<Serie> listaSeries;
    private DefaultListModel<Serie> modeloLista;

    public TelaBusca(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.tvMazeService = new TVMazeService();
        configurarJanela();
        construirLayout();
    }

    private void configurarJanela() {
        setTitle("Buscar Séries");
        setSize(680, 520);
        // DISPOSE_ON_CLOSE: fecha apenas esta janela, sem encerrar o programa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirLayout() {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelBusca = new JPanel(new BorderLayout(8, 0));
        campoBusca = new JTextField();
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 13));

        btnBuscar.addActionListener(e -> realizarBusca());
        campoBusca.addActionListener(e -> realizarBusca());

        painelBusca.add(new JLabel("Nome da série:  "), BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(btnBuscar, BorderLayout.EAST);

        modeloLista = new DefaultListModel<>();
        listaSeries = new JList<>(modeloLista);
        listaSeries.setFont(new Font("Arial", Font.PLAIN, 13));
        listaSeries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listaSeries.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) exibirDetalhes();
            }
        });

        JScrollPane scroll = new JScrollPane(listaSeries);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 4, 8, 0));

        JButton btnFavorito = new JButton("+ Favoritos");
        JButton btnAssistida = new JButton("+ Já Assisti");
        JButton btnQueroAssistir = new JButton("+ Quero Assistir");
        JButton btnVoltar = new JButton("← Voltar");

        btnFavorito.addActionListener(e -> adicionarNaLista("favoritos"));
        btnAssistida.addActionListener(e -> adicionarNaLista("assistidas"));
        btnQueroAssistir.addActionListener(e -> adicionarNaLista("queroAssistir"));
        btnVoltar.addActionListener(e -> dispose());

        painelBotoes.add(btnFavorito);
        painelBotoes.add(btnAssistida);
        painelBotoes.add(btnQueroAssistir);
        painelBotoes.add(btnVoltar);

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        add(painel);
    }

    private void realizarBusca() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome de uma série para buscar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<List<Serie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Serie> doInBackground() throws Exception {
                return tvMazeService.buscarPorNome(termo);
            }

            @Override
            protected void done() {
                try {
                    List<Serie> resultados = get();
                    modeloLista.clear();
                    if (resultados.isEmpty()) {
                        JOptionPane.showMessageDialog(TelaBusca.this,
                                "Nenhuma série encontrada para: " + termo,
                                "Sem resultados", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        for (Serie s : resultados) modeloLista.addElement(s);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaBusca.this,
                            "Erro ao buscar séries: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void exibirDetalhes() {
        Serie serie = listaSeries.getSelectedValue();
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

    private void adicionarNaLista(String lista) {
        Serie serie = listaSeries.getSelectedValue();
        if (serie == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série primeiro.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (lista) {
            case "favoritos"     -> usuarioService.adicionarFavorito(serie);
            case "assistidas"    -> usuarioService.adicionarAssistida(serie);
            case "queroAssistir" -> usuarioService.adicionarQueroAssistir(serie);
        }

        JOptionPane.showMessageDialog(this,
                "\"" + serie.getNome() + "\" adicionada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}