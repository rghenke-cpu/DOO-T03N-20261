

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private static final String[] LIST_TYPES = {"Favoritos", "Já assistidas", "Desejo assistir"};
    private static final String[] SORT_OPTIONS = {"Nome", "Nota", "Estado", "Data de estreia"};

    private JTextField campoBusca;
    private JButton botaoBuscar;
    private DefaultListModel<Show> modeloBusca;
    private JList<Show> listaBusca;
    private JTextArea detalhesBusca;
    private JComboBox<String> comboTipoLista;
    private JComboBox<String> comboOrdenacao;
    private DefaultListModel<Show> modeloLista;
    private JList<Show> listaMinhas;
    private JLabel labelUsuario;

    private AppData dados;
    private final TvMazeService tvMazeService = new TvMazeService();
    private final DataService dataService = new DataService();

    public MainFrame() {
        setTitle("Rastreador de Séries de TV");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);

        dados = dataService.carregarDados();
        inicializarUsuarioPadrao();
        criarInterface();
        setVisible(true);
    }

    private void inicializarUsuarioPadrao() {
        if (dados.getCurrentUser() == null || dados.getCurrentUser().trim().isEmpty()) {
            dados.setCurrentUser("Usuário");
        }
        if (dados.getUsers().isEmpty()) {
            UserData usuario = new UserData();
            usuario.setName(dados.getCurrentUser());
            dados.getUsers().add(usuario);
        }
    }

    private void criarInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelPrincipal.add(criarPainelUsuario(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Buscar Séries", criarAbaBusca());
        abas.addTab("Minhas Listas", criarAbaListas());
        painelPrincipal.add(abas, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    private JPanel criarPainelUsuario() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBackground(new Color(70, 130, 180));

        labelUsuario = new JLabel("Usuário: " + dados.getCurrentUser());
        labelUsuario.setForeground(Color.WHITE);
        labelUsuario.setFont(new Font("Arial", Font.BOLD, 14));

        JButton botaoTrocar = new JButton("Trocar Usuário");
        botaoTrocar.addActionListener(e -> trocarUsuario());

        painel.add(labelUsuario);
        painel.add(botaoTrocar);
        return painel;
    }

    private JPanel criarAbaBusca() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topo.add(new JLabel("Nome da série:"));

        campoBusca = new JTextField(26);
        topo.add(campoBusca);

        botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> executarBusca());
        topo.add(botaoBuscar);
        painel.add(topo, BorderLayout.NORTH);

        modeloBusca = new DefaultListModel<>();
        listaBusca = new JList<>(modeloBusca);
        listaBusca.setCellRenderer(new RenderizadorSerie());
        listaBusca.addListSelectionListener(e -> atualizarDetalhesBusca());

        detalhesBusca = new JTextArea();
        detalhesBusca.setEditable(false);
        detalhesBusca.setLineWrap(true);
        detalhesBusca.setWrapStyleWord(true);
        detalhesBusca.setText("Selecione uma série para ver os detalhes.");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(listaBusca), new JScrollPane(detalhesBusca));
        split.setDividerLocation(360);
        painel.add(split, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        botoes.add(criarBotaoAdicionar("Favoritos", 0));
        botoes.add(criarBotaoAdicionar("Já assistidas", 1));
        botoes.add(criarBotaoAdicionar("Desejo assistir", 2));
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JButton criarBotaoAdicionar(String nome, int tipo) {
        JButton botao = new JButton(nome);
        botao.addActionListener(e -> adicionarSelecionada(tipo));
        return botao;
    }

    private JPanel criarAbaListas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controles.add(new JLabel("Lista:"));

        comboTipoLista = new JComboBox<>(LIST_TYPES);
        comboTipoLista.addActionListener(e -> atualizarLista());
        controles.add(comboTipoLista);

        controles.add(new JLabel("Ordenar por:"));
        comboOrdenacao = new JComboBox<>(SORT_OPTIONS);
        comboOrdenacao.addActionListener(e -> atualizarLista());
        controles.add(comboOrdenacao);

        JButton botaoRemover = new JButton("Remover Série");
        botaoRemover.addActionListener(e -> removerSelecionada());
        controles.add(botaoRemover);
        painel.add(controles, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaMinhas = new JList<>(modeloLista);
        listaMinhas.setCellRenderer(new RenderizadorSerie());
        listaMinhas.addListSelectionListener(e -> atualizarDetalhesLista());

        JTextArea detalhesLista = new JTextArea();
        detalhesLista.setEditable(false);
        detalhesLista.setLineWrap(true);
        detalhesLista.setWrapStyleWord(true);
        detalhesLista.setText("Selecione uma série da lista para ver mais informações.");
        listaMinhas.putClientProperty("detalhesArea", detalhesLista);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(listaMinhas), new JScrollPane(detalhesLista));
        split.setDividerLocation(360);
        painel.add(split, BorderLayout.CENTER);
        atualizarLista();
        return painel;
    }

    private void executarBusca() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome de série para buscar.");
            return;
        }

        botaoBuscar.setEnabled(false);
        modeloBusca.clear();
        detalhesBusca.setText("Buscando séries...");

        new Thread(() -> {
            List<Show> resultados;
            try {
                resultados = tvMazeService.buscarSeries(termo);
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    detalhesBusca.setText("Erro ao buscar séries: " + ex.getMessage());
                    botaoBuscar.setEnabled(true);
                });
                return;
            }

            SwingUtilities.invokeLater(() -> {
                if (resultados.isEmpty()) detalhesBusca.setText("Nenhuma série encontrada.");
                else for (Show s : resultados) modeloBusca.addElement(s);
                botaoBuscar.setEnabled(true);
            });
        }).start();
    }

    private void atualizarDetalhesBusca() {
        Show selecionado = listaBusca.getSelectedValue();
        detalhesBusca.setText(selecionado == null ? "Selecione uma série para ver os detalhes." : selecionado.getFullDescription());
    }

    private void adicionarSelecionada(int tipoLista) {
        Show selecionada = listaBusca.getSelectedValue();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série nos resultados de busca.");
            return;
        }

        UserData usuario = getCurrentUser();
        List<Show> lista = getListByType(usuario, tipoLista);
        for (Show s : lista) {
            if (s.getId() == selecionada.getId()) {
                JOptionPane.showMessageDialog(this, "A série já está nessa lista.");
                return;
            }
        }

        lista.add(selecionada);
        salvarDados();
        atualizarLista();
        JOptionPane.showMessageDialog(this, "Série adicionada com sucesso.");
    }

    private void atualizarLista() {
        if (modeloLista == null || comboTipoLista == null || comboOrdenacao == null) return;
        modeloLista.clear();
        UserData usuario = getCurrentUser();
        int tipo = comboTipoLista.getSelectedIndex();
        if (tipo < 0) tipo = 0;
        List<Show> lista = getListByType(usuario, tipo);
        ordenarShows(lista, (String) comboOrdenacao.getSelectedItem());
        for (Show s : lista) modeloLista.addElement(s);
    }

    private void atualizarDetalhesLista() {
        Show selecionado = listaMinhas.getSelectedValue();
        JTextArea area = (JTextArea) listaMinhas.getClientProperty("detalhesArea");
        if (area != null) area.setText(selecionado == null ? "Selecione uma série da lista para ver mais informações." : selecionado.getFullDescription());
    }

    private void removerSelecionada() {
        Show selecionada = listaMinhas.getSelectedValue();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série para remover.");
            return;
        }
        UserData usuario = getCurrentUser();
        int tipo = comboTipoLista.getSelectedIndex();
        if (tipo < 0) tipo = 0;
        List<Show> lista = getListByType(usuario, tipo);
        lista.removeIf(s -> s.getId() == selecionada.getId());
        salvarDados();
        atualizarLista();
    }

    private UserData getCurrentUser() {
        for (UserData u : dados.getUsers()) {
            if (u.getName().equals(dados.getCurrentUser())) return u;
        }
        UserData novo = new UserData();
        novo.setName(dados.getCurrentUser());
        dados.getUsers().add(novo);
        return novo;
    }

    private List<Show> getListByType(UserData user, int tipo) {
        if (tipo == 0) return user.getFavorites();
        if (tipo == 1) return user.getWatched();
        return user.getWantToWatch();
    }

    private void ordenarShows(List<Show> shows, String criterio) {
        if (criterio == null) criterio = "Nome";
        switch (criterio) {
            case "Nome":
                shows.sort(Comparator.comparing(s -> s.getName() == null ? "" : s.getName().toLowerCase()));
                break;
            case "Nota":
                shows.sort(Comparator.comparingDouble(Show::getRating).reversed());
                break;
            case "Estado":
                shows.sort(Comparator.comparingInt((Show s) -> statusOrder(s.getStatus())).thenComparing(s -> s.getName() == null ? "" : s.getName()));
                break;
            case "Data de estreia":
                shows.sort(Comparator.comparing(s -> s.getPremiered() == null ? "" : s.getPremiered()));
                break;
        }
    }

    private int statusOrder(String status) {
        if (status == null) return 10;
        String normalized = status.trim().toLowerCase();
        if (normalized.equals("ended") || normalized.equals("concluded") || normalized.equals("finalizado")) return 0;
        if (normalized.equals("running") || normalized.equals("em exibição") || normalized.equals("ainda transmitindo")) return 1;
        if (normalized.equals("cancelled") || normalized.equals("canceled") || normalized.equals("cancelada")) return 2;
        return 3;
    }

    private void trocarUsuario() {
        String novo = JOptionPane.showInputDialog(this, "Digite o nome do usuário:", dados.getCurrentUser());
        if (novo != null && !novo.trim().isEmpty()) {
            dados.setCurrentUser(novo.trim());
            getCurrentUser();
            labelUsuario.setText("Usuário: " + dados.getCurrentUser());
            salvarDados();
            atualizarLista();
        }
    }

    private void salvarDados() {
        try {
            dataService.salvarDados(dados);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar dados: " + e.getMessage());
        }
    }
}
