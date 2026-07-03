package br.escola.ui;

import br.escola.service.UsuarioService;

import javax.swing.*;
import java.awt.*;

// Tela principal do sistema — funciona como menu inicial
// É a primeira janela que o usuário vê
public class TelaPrincipal extends JFrame {

    private final UsuarioService usuarioService;

    public TelaPrincipal(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        configurarJanela();
        construirLayout();
    }

    private void configurarJanela() {
        setTitle("Minhas Séries de TV");
        setSize(400, 350);
        // DO_NOTHING_ON_CLOSE: impede fechar sem salvar — tratado no WindowListener
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarSaida();
            }
        });
    }

    private void construirLayout() {
        JPanel painel = new JPanel(new BorderLayout());

        // Cabeçalho com título e nome do usuário
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Minhas Séries de TV", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel lblUsuario = new JLabel("Olá, " + usuarioService.getUsuario().getNome() + "!", SwingConstants.CENTER);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));

        cabecalho.add(lblTitulo, BorderLayout.CENTER);
        cabecalho.add(lblUsuario, BorderLayout.SOUTH);

        // Botões do menu
        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 0, 10));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        JButton btnBuscar = new JButton("Buscar Séries");
        JButton btnListas = new JButton("Minhas Listas");
        JButton btnSair = new JButton("Salvar e Sair");

        btnBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        btnListas.setFont(new Font("Arial", Font.BOLD, 14));
        btnSair.setFont(new Font("Arial", Font.BOLD, 14));

        btnBuscar.addActionListener(e -> new TelaBusca(usuarioService).setVisible(true));

        btnListas.addActionListener(e -> new TelaListas(usuarioService).setVisible(true));

        btnSair.addActionListener(e -> confirmarSaida());

        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnListas);
        painelBotoes.add(btnSair);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(painelBotoes, BorderLayout.CENTER);
        add(painel);
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja salvar e sair?",
                "Confirmar saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcao == JOptionPane.YES_OPTION) {
            try {
                usuarioService.salvar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
    }
}