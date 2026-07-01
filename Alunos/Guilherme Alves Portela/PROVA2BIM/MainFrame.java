import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {

    private final IUsuarioService usuarioService;
    private final ITvApiClient tvApiClient;

    // Componentes da UI
    private JTextField txtBusca;
    private JTable tabelaBusca, tabelaFavoritos, tabelaAssistidas, tabelaDesejos;
    private JLabel lblStatus;

    public MainFrame(IUsuarioService usuarioService, ITvApiClient tvApiClient) {
        this.usuarioService = usuarioService;
        this.tvApiClient = tvApiClient;

        configurarJanela();
        inicializarComponentes();
        
        // Se não houver usuário, pede o nome logo ao abrir
        verificarUsuario();
        atualizarTodasTabelas();
    }

    private void configurarJanela() {
        setTitle("TV Maze Series Tracker - SOLID Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. BARRA DE BUSCA (NORTE) ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(30);
        JButton btnBuscar = new JButton("Pesquisar Série");
        painelBusca.add(new JLabel("Nome da Série:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        add(painelBusca, BorderLayout.NORTH);

        // --- 2. PAINEL DE ABAS (CENTRO) ---
        JTabbedPane abas = new JTabbedPane();

        // Inicializa as tabelas
        tabelaBusca = criarTabela();
        tabelaFavoritos = criarTabela();
        tabelaAssistidas = criarTabela();
        tabelaDesejos = criarTabela();

        abas.addTab("Resultados da Busca", criarPainelComBotoes(tabelaBusca, "Busca"));
        abas.addTab("Favoritos", criarPainelComLista(tabelaFavoritos, "Favoritos"));
        abas.addTab("Já Assistidas", criarPainelComLista(tabelaAssistidas, "Assistidas"));
        abas.addTab("Deseja Assistir", criarPainelComLista(tabelaDesejos, "Deseja Assistir"));

        add(abas, BorderLayout.CENTER);

        // --- 3. BARRA DE STATUS (SUL) ---
        lblStatus = new JLabel(" Sistema pronto.");
        lblStatus.setBorder(BorderFactory.createEtchedBorder());
        add(lblStatus, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> acaoBuscar());
    }

    // Método para criar uma tabela padrão
    private JTable criarTabela() {
        String[] colunas = {"Nome", "Emissora", "Nota", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        return new JTable(model);
    }

    // Painel para a aba de busca (com botões de adicionar)
    private JPanel criarPainelComBotoes(JTable tabela, String tipo) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        JButton btnFavorito = new JButton("❤ Favoritar");
        JButton btnAssistido = new JButton("✔ Já Assisti");
        JButton btnDesejo = new JButton("⭐ Quero Ver");
        JButton btnDetalhes = new JButton("ℹ Ver Detalhes");

        botoes.add(btnDetalhes);
        botoes.add(btnFavorito);
        botoes.add(btnAssistido);
        botoes.add(btnDesejo);

        painel.add(botoes, BorderLayout.SOUTH);

        // Eventos dos botões (Simplificado)
        btnDetalhes.addActionListener(e -> exibirDetalhes(tabela));
        btnFavorito.addActionListener(e -> adicionarSerie(tabela, 1));
        btnAssistido.addActionListener(e -> adicionarSerie(tabela, 2));
        btnDesejo.addActionListener(e -> adicionarSerie(tabela, 3));

        return painel;
    }

    // Painel para as abas de listas (com botões de ordenar e remover)
    private JPanel criarPainelComLista(JTable tabela, String tipoLista) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel();
        String[] opcoes = {"Ordenar: A-Z", "Ordenar: Nota", "Ordenar: Status", "Ordenar: Estreia"};
        JComboBox<String> comboSort = new JComboBox<>(opcoes);
        JButton btnDetalhes = new JButton("ℹ Ver Detalhes");
        JButton btnRemover = new JButton("Remover");

        acoes.add(comboSort);
        acoes.add(btnDetalhes);
        acoes.add(btnRemover);
        painel.add(acoes, BorderLayout.SOUTH);

        // Eventos
        btnDetalhes.addActionListener(e -> exibirDetalhes(tabela));
        btnRemover.addActionListener(e -> removerDaLista(tabela, tipoLista));
        comboSort.addActionListener(e -> ordenarLista(tipoLista, (String) comboSort.getSelectedItem()));

        return painel;
    }

    // --- LÓGICA DE INTERAÇÃO (A PONTE COM O SERVICE) ---

    private void acaoBuscar() {
        try {
            String termo = txtBusca.getText();
            List<Serie> resultados = tvApiClient.buscarSeriesPorNome(termo);
            popularTabela(tabelaBusca, resultados);
            lblStatus.setText(" Busca concluída para: " + termo);
        } catch (ExceptionManager e) {
            exibirErro(e.getMessage());
        }
    }

    private void popularTabela(JTable tabela, List<Serie> series) {
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        model.setRowCount(0); // Limpa a tabela
        
        for (Serie s : series) {
            // Traduz o estado vindo da API para o português
            String estadoTraduzido = traduzirEstado(s.getEstado());
            
            model.addRow(new Object[]{
                s.getNome(), 
                s.getEmissora(), 
                s.getNotaGeral(), 
                estadoTraduzido // Exibe o texto traduzido na tabela
            });
        }
        tabela.putClientProperty("lista_series", series);
    }

    private List<Serie> obterSeriesDaTabela(JTable tabela) {
        Object valor = tabela.getClientProperty("lista_series");
        if (valor instanceof List<?> lista) {
            @SuppressWarnings("unchecked")
            List<Serie> series = (List<Serie>) lista;
            return series;
        }
        return java.util.Collections.emptyList();
    }

    // Método auxiliar de tradução
    private String traduzirEstado(String estadoJson) {
        if (estadoJson == null) return "Desconhecido";
        
        return switch (estadoJson.trim().toLowerCase()) {
            case "running" -> "Ainda transmitindo";
            case "ended" -> "Já concluída";
            case "canceled", "cancelled" -> "Cancelada";
            case "in development" -> "Em desenvolvimento";
            default -> estadoJson; // Caso venha algo novo, mantém o original
        };
    }

    private String formatarData(String data) {
        if (data == null || data.isBlank() || data.equals("N/A")) {
            return "N/A";
        }

        try {
            LocalDate dataLocal = LocalDate.parse(data);
            return dataLocal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return data;
        }
    }

    private void exibirDetalhes(JTable tabela) {
        int row = tabela.getSelectedRow();
        
        // VALIDAÇÃO: Se não selecionou nada, avisa e para a execução
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma série na tabela para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Serie> series = obterSeriesDaTabela(tabela);
        Serie s = series.get(row);

        String info = String.format(
            "Nome: %s\nIdioma: %s\nGêneros: %s\nNota: %.1f\nStatus: %s\nEstreia: %s / Fim: %s\nEmissora: %s",
            s.getNome(), s.getIdioma(), s.getGeneros(), s.getNotaGeral(), traduzirEstado(s.getEstado()), 
            formatarData(s.getDataEstreia()), formatarData(s.getDataTermino()), s.getEmissora()
        );
        JOptionPane.showMessageDialog(this, info, "Detalhes da Série", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adicionarSerie(JTable tabela, int listaDestino) {
        int row = tabela.getSelectedRow();
        
        // VALIDAÇÃO: Impede o clique sem seleção
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma série na tabela antes de adicioná-la a uma lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Serie> series = obterSeriesDaTabela(tabela);
            Serie s = series.get(row);

            if (listaDestino == 1) usuarioService.adicionarFavorito(s);
            else if (listaDestino == 2) usuarioService.adicionarAssistido(s);
            else if (listaDestino == 3) usuarioService.adicionarDesejaAssistir(s);

            atualizarTodasTabelas();
            
            JOptionPane.showMessageDialog(this, "\"" + s.getNome() + "\" foi adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (ExceptionManager e) {
            exibirErro(e.getMessage());
        }
    }

    private void removerDaLista(JTable tabela, String tipoLista) {
        int row = tabela.getSelectedRow();
        
        // VALIDAÇÃO: Impede tentar remover o que não foi selecionado
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione a série que você deseja remover da lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return; 
        }

        try {
            List<Serie> series = obterSeriesDaTabela(tabela);
            Serie s = series.get(row);

            if (tipoLista.equals("Favoritos")) usuarioService.removerFavorito(s);
            else if (tipoLista.equals("Assistidas")) usuarioService.removerAssistido(s);
            else if (tipoLista.equals("Deseja Assistir")) usuarioService.removerDesejaAssistir(s);

            atualizarTodasTabelas();
            JOptionPane.showMessageDialog(this, "\"" + s.getNome() + "\" foi removida da lista.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (ExceptionManager e) {
            exibirErro(e.getMessage());
        }
    }

    private void ordenarLista(String tipoLista, String criterio) {
        // Mapeia o texto do combo para as constantes da Factory
        String constCrit = SerieComparatorFactory.ALFABETICA;
        if (criterio.contains("Nota")) constCrit = SerieComparatorFactory.NOTA;
        else if (criterio.contains("Status")) constCrit = SerieComparatorFactory.ESTADO;
        else if (criterio.contains("Estreia")) constCrit = SerieComparatorFactory.DATA_ESTREIA;

        if (tipoLista.equals("Favoritos")) usuarioService.ordenarListaFavoritos(constCrit);
        else if (tipoLista.equals("Assistidas")) usuarioService.ordenarListaAssistidos(constCrit);
        else if (tipoLista.equals("Deseja Assistir")) usuarioService.ordenarListaDesejaAssistir(constCrit);

        atualizarTodasTabelas();
    }

    private void atualizarTodasTabelas() {
        Usuario u = usuarioService.getUsuarioAtual();
        if (u != null) {
            popularTabela(tabelaFavoritos, u.getFavoritos());
            popularTabela(tabelaAssistidas, u.getAssistidas());
            popularTabela(tabelaDesejos, u.getDesejaAssistir());
            lblStatus.setText(" Usuário: " + u.getNome() + " | Listas atualizadas.");
        }
    }

    private void verificarUsuario() {
        if (usuarioService.getUsuarioAtual() == null) {
            String nome = JOptionPane.showInputDialog(this, "Qual seu apelido?");
            if (nome == null || nome.trim().isEmpty()) nome = "Usuário Local";
            usuarioService.inicializarUsuario(nome);
        }
    }

    private void exibirErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso do Sistema", JOptionPane.WARNING_MESSAGE);
    }
}