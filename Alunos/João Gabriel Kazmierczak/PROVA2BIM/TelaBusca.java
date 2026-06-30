package fag.view;

import fag.model.Serie;
import fag.model.Usuario;
import fag.service.ApiService;
import fag.service.PersistenciaService;
import fag.service.SerieService;
import fag.util.EstiloUtil;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

// Tela responsável por buscar séries na API do TVMaze e adicionar séries às listas.
public class TelaBusca extends JDialog {

    private final Usuario usuario;
    private final PersistenciaService persistenciaService;
    private final SerieService serieService;
    private final ApiService apiService;

    private JTextField campoBusca;
    private JButton botaoBuscar;
    private DefaultListModel<Serie> modeloResultados;
    private JList<Serie> listaResultados;
    private JTextArea areaDetalhes;
    private JComboBox<String> comboListas;

    public TelaBusca(JFrame telaPai, Usuario usuario, PersistenciaService persistenciaService, SerieService serieService) {
        super(telaPai, "Buscar séries", true);

        this.usuario = usuario;
        this.persistenciaService = persistenciaService;
        this.serieService = serieService;
        this.apiService = new ApiService();

        configurarJanela();
        montarComponentes();
    }

    private void configurarJanela() {
        setSize(900, 540);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(EstiloUtil.COR_FUNDO);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarFecharJanela();
            }
        });
    }

    private void montarComponentes() {
        JLabel titulo = new JLabel("Buscar séries na API do TVMaze", JLabel.CENTER);
        EstiloUtil.estilizarTitulo(titulo);

        campoBusca = new JTextField(30);
        EstiloUtil.estilizarCampoTexto(campoBusca);

        botaoBuscar = new JButton("Buscar");
        EstiloUtil.estilizarBotao(botaoBuscar);

        botaoBuscar.addActionListener(e -> buscarSeries());
        campoBusca.addActionListener(e -> buscarSeries());

        JLabel labelBusca = new JLabel("Nome da série:");
        EstiloUtil.estilizarLabel(labelBusca);

        JPanel painelBusca = new JPanel(new FlowLayout());
        EstiloUtil.estilizarPainelInterno(painelBusca);
        painelBusca.add(labelBusca);
        painelBusca.add(campoBusca);
        painelBusca.add(botaoBuscar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        EstiloUtil.estilizarPainel(painelTopo);
        painelTopo.add(titulo, BorderLayout.NORTH);
        painelTopo.add(painelBusca, BorderLayout.CENTER);

        modeloResultados = new DefaultListModel<Serie>();
        listaResultados = new JList<Serie>(modeloResultados);
        EstiloUtil.estilizarLista(listaResultados);

        areaDetalhes = new JTextArea();
        areaDetalhes.setEditable(false);
        areaDetalhes.setLineWrap(true);
        areaDetalhes.setWrapStyleWord(true);
        EstiloUtil.estilizarAreaTexto(areaDetalhes);

        listaResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhesSerieSelecionada();
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaResultados);
        JScrollPane scrollDetalhes = new JScrollPane(areaDetalhes);
        EstiloUtil.estilizarScroll(scrollLista);
        EstiloUtil.estilizarScroll(scrollDetalhes);

        JSplitPane painelCentral = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollLista,
                scrollDetalhes
        );

        painelCentral.setDividerLocation(300);
        painelCentral.setBackground(EstiloUtil.COR_FUNDO);

        comboListas = new JComboBox<String>(new String[]{
                SerieService.LISTA_FAVORITOS,
                SerieService.LISTA_JA_ASSISTIDAS,
                SerieService.LISTA_DESEJO_ASSISTIR
        });
        EstiloUtil.estilizarCombo(comboListas);

        JButton botaoAdicionar = new JButton("Adicionar à lista");
        JButton botaoFechar = new JButton("Fechar");

        EstiloUtil.estilizarBotao(botaoAdicionar);
        EstiloUtil.estilizarBotaoSair(botaoFechar);

        botaoAdicionar.addActionListener(e -> adicionarSerieSelecionada());
        botaoFechar.addActionListener(e -> confirmarFecharJanela());

        JLabel labelAdicionar = new JLabel("Adicionar em:");
        EstiloUtil.estilizarLabel(labelAdicionar);

        JPanel painelInferior = new JPanel(new FlowLayout());
        EstiloUtil.estilizarPainelInterno(painelInferior);
        painelInferior.add(labelAdicionar);
        painelInferior.add(comboListas);
        painelInferior.add(botaoAdicionar);
        painelInferior.add(botaoFechar);

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    // Busca séries pelo nome digitado pelo usuário.
    private void buscarSeries() {
        String termoBusca = campoBusca.getText();

        // Validação para impedir que o usuário faça uma busca vazia.
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome de uma série para buscar.");
            return;
        }

        botaoBuscar.setEnabled(false);
        modeloResultados.clear();
        areaDetalhes.setText("Buscando séries na API do TVMaze...");

        // SwingWorker executa a busca em segundo plano para a tela não travar durante a consulta na API.
        SwingWorker<ArrayList<Serie>, Void> trabalhador = new SwingWorker<ArrayList<Serie>, Void>() {
            @Override
            // Executa a busca na API fora da interface gráfica.
            protected ArrayList<Serie> doInBackground() throws Exception {
                return apiService.buscarSeriesPorNome(termoBusca);
            }

            @Override
            // Atualiza a tela com os resultados encontrados ou mostra mensagem de erro.
            protected void done() {
                try {
                    ArrayList<Serie> seriesEncontradas = get();

                    modeloResultados.clear();

                    if (seriesEncontradas.isEmpty()) {
                        areaDetalhes.setText("Nenhuma série encontrada.");
                    } else {
                        for (Serie serie : seriesEncontradas) {
                            modeloResultados.addElement(serie);
                        }

                        areaDetalhes.setText("Selecione uma série para ver os detalhes.");
                    }

                } catch (Exception e) {
                    areaDetalhes.setText("");
                    JOptionPane.showMessageDialog(
                            TelaBusca.this,
                            "Erro ao buscar séries:\n" + e.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    botaoBuscar.setEnabled(true);
                }
            }
        };

        trabalhador.execute();
    }

    // Exibe os detalhes da série selecionada na lista de resultados.
    private void mostrarDetalhesSerieSelecionada() {
        Serie serieSelecionada = listaResultados.getSelectedValue();

        if (serieSelecionada == null) {
            areaDetalhes.setText("");
            return;
        }

        areaDetalhes.setText(serieSelecionada.getDetalhesFormatados());
    }

    // Adiciona a série selecionada na lista escolhida pelo usuário.
    private void adicionarSerieSelecionada() {
        try {
            Serie serieSelecionada = listaResultados.getSelectedValue();

            if (serieSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma série primeiro.");
                return;
            }

            String tipoLista = (String) comboListas.getSelectedItem();

            serieService.adicionarSerie(usuario, serieSelecionada, tipoLista);
            persistenciaService.salvarUsuario(usuario);

            JOptionPane.showMessageDialog(this, "Série adicionada com sucesso.");

        } catch (Exception e) {
            // Tratamento de exceção para casos como série duplicada ou nenhuma série selecionada.
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível adicionar a série:\n" + e.getMessage(),
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void confirmarFecharJanela() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja fechar esta janela?",
                "Confirmar fechamento",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}