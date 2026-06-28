package tvmanager.ui;

import tvmanager.api.TVMazeClient;
import tvmanager.model.Usuario;
import tvmanager.util.GerenciadorSeries;
import tvmanager.util.GerenciadorSeries.TipoLista;
import tvmanager.util.ImageLoader;
import tvmanager.util.PersistenciaJSON;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Janela principal do TV Manager.
 * Gerencia a navegação entre os painéis via sidebar.
 */
public class JanelaPrincipal extends JFrame {

    private final TVMazeClient client;
    private final PersistenciaJSON persistencia;
    private final GerenciadorSeries gerenciador;

    // Painéis de lista (precisam de referência para atualizar)
    private PainelLista painelFavoritos;
    private PainelLista painelAssistidas;
    private PainelLista painelQueroAssistir;
    private PainelPerfil painelPerfil;

    // Botões da sidebar (campos para poder mudar o estilo ativo/inativo)
    private JButton btnBusca;
    private JButton btnFavoritos;
    private JButton btnAssistidas;
    private JButton btnQuero;
    private JButton btnPerfil;

    private JLabel lblSaudacao;

    public JanelaPrincipal() {
        client = new TVMazeClient();
        persistencia = new PersistenciaJSON();
        gerenciador = new GerenciadorSeries(persistencia);

        setTitle("TV Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setMinimumSize(new Dimension(780, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.BG_PRIMARIO);

        construir();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                ImageLoader.desligar();
            }
        });
    }

    private void construir() {
        setLayout(new BorderLayout());

        // Conteúdo principal
        JPanel conteudo = new JPanel(new CardLayout());
        conteudo.setBackground(Tema.BG_PRIMARIO);

        PainelBusca painelBusca = new PainelBusca(client, gerenciador);
        painelFavoritos   = new PainelLista(client, gerenciador, TipoLista.FAVORITOS,      "★ Favoritos");
        painelAssistidas  = new PainelLista(client, gerenciador, TipoLista.ASSISTIDAS,     "✔ Já Assistidas");
        painelQueroAssistir = new PainelLista(client, gerenciador, TipoLista.QUERO_ASSISTIR, "⊕ Quero Assistir");
        painelPerfil      = new PainelPerfil(persistencia);

        // Ao alterar qualquer lista na busca, atualiza os painéis correspondentes
        painelBusca.setOnListasAlteradas(() -> {
            painelFavoritos.atualizar();
            painelAssistidas.atualizar();
            painelQueroAssistir.atualizar();
        });

        // Ao salvar perfil, atualiza saudação na sidebar
        painelPerfil.setOnUsuarioAlterado(this::atualizarSaudacao);

        conteudo.add(painelBusca,         "busca");
        conteudo.add(painelFavoritos,     "favoritos");
        conteudo.add(painelAssistidas,    "assistidas");
        conteudo.add(painelQueroAssistir, "quero");
        conteudo.add(painelPerfil,        "perfil");

        // Sidebar
        JPanel sidebar = criarSidebar();
        add(sidebar, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);

        CardLayout cl = (CardLayout) conteudo.getLayout();

        btnBusca.addActionListener(e     -> { cl.show(conteudo, "busca");      ativar(btnBusca); });
        btnFavoritos.addActionListener(e -> { painelFavoritos.atualizar();     cl.show(conteudo, "favoritos");  ativar(btnFavoritos); });
        btnAssistidas.addActionListener(e-> { painelAssistidas.atualizar();    cl.show(conteudo, "assistidas"); ativar(btnAssistidas); });
        btnQuero.addActionListener(e     -> { painelQueroAssistir.atualizar(); cl.show(conteudo, "quero");      ativar(btnQuero); });
        btnPerfil.addActionListener(e    -> { cl.show(conteudo, "perfil");     ativar(btnPerfil); });

        ativar(btnBusca);
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Tema.BG_SECUNDARIO);
        sidebar.setPreferredSize(new Dimension(175, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.BORDA));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(Tema.BG_SECUNDARIO);
        logoPanel.setBorder(new EmptyBorder(22, 0, 18, 0));

        JLabel logo = new JLabel("TV");
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        logo.setForeground(Tema.ACENTO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoSub = new JLabel("MANAGER");
        logoSub.setFont(new Font("SansSerif", Font.BOLD, 11));
        logoSub.setForeground(Tema.TEXTO_MUTED);
        logoSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(logoSub);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Tema.BG_SECUNDARIO);
        nav.setBorder(new EmptyBorder(4, 10, 4, 10));

        btnBusca     = navBtn("🔍  Buscar");
        btnFavoritos = navBtn("★  Favoritos");
        btnAssistidas= navBtn("✔  Já assistidas");
        btnQuero     = navBtn("⊕  Quero assistir");
        btnPerfil    = navBtn("◉  Meu perfil");

        for (JButton btn : new JButton[]{btnBusca, btnFavoritos, btnAssistidas, btnQuero, btnPerfil}) {
            nav.add(btn);
            nav.add(Box.createVerticalStrut(4));
        }

        sidebar.add(nav, BorderLayout.CENTER);

        // Rodapé: saudação + api
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBackground(Tema.BG_SECUNDARIO);
        rodape.setBorder(new EmptyBorder(8, 12, 12, 12));

        lblSaudacao = new JLabel(" ");
        lblSaudacao.setFont(Tema.FONTE_PEQUENA);
        lblSaudacao.setForeground(Tema.TEXTO_MUTED);
        lblSaudacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        atualizarSaudacao();

        JLabel api = new JLabel("via TVMaze API");
        api.setFont(Tema.FONTE_PEQUENA);
        api.setForeground(Tema.CINZA_MEDIO);
        api.setAlignmentX(Component.LEFT_ALIGNMENT);

        rodape.add(lblSaudacao);
        rodape.add(Box.createVerticalStrut(4));
        rodape.add(api);
        sidebar.add(rodape, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton navBtn(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(Tema.FONTE_CORPO);
        btn.setForeground(Tema.TEXTO_MUTED);
        btn.setBackground(Tema.BG_SECUNDARIO);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 0)),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void ativar(JButton ativo) {
        for (JButton btn : new JButton[]{btnBusca, btnFavoritos, btnAssistidas, btnQuero, btnPerfil}) {
            boolean isAtivo = btn == ativo;
            btn.setBackground(isAtivo ? Tema.BG_CARD : Tema.BG_SECUNDARIO);
            btn.setForeground(isAtivo ? Tema.TEXTO_PRIMARIO : Tema.TEXTO_MUTED);
            btn.setBorder(BorderFactory.createCompoundBorder(
                isAtivo ? BorderFactory.createLineBorder(Tema.BORDA)
                        : BorderFactory.createLineBorder(new Color(0, 0, 0, 0)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        }
    }

    private void atualizarSaudacao() {
        Usuario u = persistencia.getUsuario();
        String nome = u.getNomeExibicao();
        lblSaudacao.setText("Olá, " + nome + "!");
    }
}
