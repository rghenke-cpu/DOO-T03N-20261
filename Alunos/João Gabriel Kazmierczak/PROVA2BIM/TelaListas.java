package fag.view;

import fag.model.Serie;
import fag.model.Usuario;
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
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

// Tela responsável por exibir, ordenar e remover séries de uma lista específica.
public class TelaListas extends JDialog {

    private final Usuario usuario;
    private final String tipoLista;
    private final PersistenciaService persistenciaService;
    private final SerieService serieService;

    private DefaultListModel<Serie> modeloLista;
    private JList<Serie> listaSeries;
    private JTextArea areaDetalhes;
    private JComboBox<String> comboOrdenacao;

    public TelaListas(JFrame telaPai, Usuario usuario, String tipoLista,
                      PersistenciaService persistenciaService, SerieService serieService) {
        super(telaPai, tipoLista, true);

        this.usuario = usuario;
        this.tipoLista = tipoLista;
        this.persistenciaService = persistenciaService;
        this.serieService = serieService;

        configurarJanela();
        montarComponentes();
        atualizarLista();
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
        JLabel titulo = new JLabel(tipoLista, JLabel.CENTER);
        EstiloUtil.estilizarTitulo(titulo);

        comboOrdenacao = new JComboBox<String>(new String[]{
                SerieService.ORDENAR_ALFABETICA,
                SerieService.ORDENAR_NOTA,
                SerieService.ORDENAR_ESTADO,
                SerieService.ORDENAR_ESTREIA
        });
        EstiloUtil.estilizarCombo(comboOrdenacao);

        JButton botaoOrdenar = new JButton("Ordenar");
        EstiloUtil.estilizarBotao(botaoOrdenar);
        botaoOrdenar.addActionListener(e -> ordenarLista());

        JLabel labelOrdenar = new JLabel("Ordenar por:");
        EstiloUtil.estilizarLabel(labelOrdenar);

        JPanel painelSuperior = new JPanel(new FlowLayout());
        EstiloUtil.estilizarPainelInterno(painelSuperior);
        painelSuperior.add(labelOrdenar);
        painelSuperior.add(comboOrdenacao);
        painelSuperior.add(botaoOrdenar);

        JPanel painelTopo = new JPanel(new BorderLayout());
        EstiloUtil.estilizarPainel(painelTopo);
        painelTopo.add(titulo, BorderLayout.NORTH);
        painelTopo.add(painelSuperior, BorderLayout.CENTER);

        modeloLista = new DefaultListModel<Serie>();
        listaSeries = new JList<Serie>(modeloLista);
        EstiloUtil.estilizarLista(listaSeries);

        areaDetalhes = new JTextArea();
        areaDetalhes.setEditable(false);
        areaDetalhes.setLineWrap(true);
        areaDetalhes.setWrapStyleWord(true);
        EstiloUtil.estilizarAreaTexto(areaDetalhes);

        listaSeries.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhesSerieSelecionada();
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaSeries);
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

        JButton botaoRemover = new JButton("Remover série");
        JButton botaoFechar = new JButton("Fechar");

        EstiloUtil.estilizarBotao(botaoRemover);
        EstiloUtil.estilizarBotaoSair(botaoFechar);

        botaoRemover.addActionListener(e -> removerSerieSelecionada());
        botaoFechar.addActionListener(e -> confirmarFecharJanela());

        JPanel painelInferior = new JPanel(new FlowLayout());
        EstiloUtil.estilizarPainelInterno(painelInferior);
        painelInferior.add(botaoRemover);
        painelInferior.add(botaoFechar);

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    // Atualiza visualmente a lista exibida na tela.
    private void atualizarLista() {
        modeloLista.clear();

        try {
            ArrayList<Serie> lista = serieService.obterListaPorTipo(usuario, tipoLista);

            for (Serie serie : lista) {
                modeloLista.addElement(serie);
            }

            if (lista.isEmpty()) {
                areaDetalhes.setText("Essa lista está vazia.");
            } else {
                areaDetalhes.setText("Selecione uma série para ver os detalhes.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao atualizar lista:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Ordena a lista atual conforme o critério escolhido e salva a alteração no JSON.
    private void ordenarLista() {
        try {
            String criterio = (String) comboOrdenacao.getSelectedItem();

            ArrayList<Serie> lista = serieService.obterListaPorTipo(usuario, tipoLista);
            serieService.ordenarLista(lista, criterio);

            persistenciaService.salvarUsuario(usuario);
            atualizarLista();

            JOptionPane.showMessageDialog(this, "Lista ordenada com sucesso.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao ordenar lista:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Remove a série selecionada da lista
    private void removerSerieSelecionada() {
        try {
            Serie serieSelecionada = listaSeries.getSelectedValue();

            if (serieSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma série para remover.");
                return;
            }

            // Confirma com o usuário antes de remover a série da lista.
            int confirmacao = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja remover a série \"" + serieSelecionada.getNome() + "\"?",
                    "Confirmar remoção",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            serieService.removerSerie(usuario, serieSelecionada, tipoLista);
            persistenciaService.salvarUsuario(usuario);
            atualizarLista();

            JOptionPane.showMessageDialog(this, "Série removida com sucesso.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao remover série:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Mostra os detalhes da série selecionada na área de texto.
    private void mostrarDetalhesSerieSelecionada() {
        Serie serieSelecionada = listaSeries.getSelectedValue();

        if (serieSelecionada == null) {
            areaDetalhes.setText("");
            return;
        }

        areaDetalhes.setText(serieSelecionada.getDetalhesFormatados());
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