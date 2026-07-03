package view;

import controller.SistemaController;
import model.Usuario;
import util.Tradutor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Tela principal do TV Tracker, exibida logo após o login.
 * Contém o menu lateral (sidebar) de navegação à esquerda e um painel
 * de conteúdo dinâmico à direita, que troca de tela conforme o item
 * do menu selecionado (Início, Buscar Séries, Favoritos, etc.).
 *
 * Também é responsável por controlar a troca de idioma (PT/EN/ES):
 * é aqui que fica a lista central de textos que precisam ser
 * pré-traduzidos (todosOsTextos()) antes de qualquer tela ser exibida.
 */
public class TelaPrincipal extends JFrame {

    // Paleta de cores do tema escuro, compartilhada com as demais telas
    // (por isso os campos são "static" e não "private", para outras
    // classes do pacote view acessarem, ex: TelaBusca.COR_FUNDO)
    static final Color COR_FUNDO      = new Color(15, 15, 28);
    static final Color COR_SIDEBAR    = new Color(20, 20, 38);
    static final Color COR_CARD       = new Color(30, 30, 52);
    static final Color COR_CARD2      = new Color(35, 35, 60);
    static final Color COR_PRIMARIA   = new Color(99, 102, 241);
    static final Color COR_PRIMARIA2  = new Color(139, 92, 246);
    static final Color COR_SUCESSO    = new Color(52, 211, 153);
    static final Color COR_AVISO      = new Color(251, 191, 36);
    static final Color COR_PERIGO     = new Color(248, 113, 113);
    static final Color COR_TEXTO      = new Color(220, 220, 240);
    static final Color COR_SUBTEXTO   = new Color(140, 140, 170);
    static final Color COR_HOVER      = new Color(99, 102, 241, 40);
    static final Color COR_ATIVO      = new Color(99, 102, 241, 80);
    static final Color COR_BORDA      = new Color(60, 60, 90);
    static final Color COR_TABELA_ALT = new Color(28, 28, 50);

    // Controller usado para pedir ações (sair do sistema, etc.)
    private final SistemaController controller;
    // Usuário logado, usado para mostrar nome e estatísticas
    private final Usuario usuario;

    // Painel à direita onde as telas de conteúdo (Busca, Favoritos, etc.)
    // são inseridas e trocadas dinamicamente
    private JPanel painelConteudo;
    // Array com os botões do menu lateral, na mesma ordem de TEXTOS_MENU_PT
    private JButton[] botoesMenu;
    // Guarda qual item do menu está selecionado no momento (para destacar
    // visualmente e para saber qual tela recarregar ao trocar de idioma)
    private int indiceAtivo = 0;

    // Textos originais (em PT) dos itens do menu lateral, na ordem em
    // que aparecem na tela. O índice de cada texto aqui corresponde
    // ao índice usado em selecionarMenu(indice) e mostrarPainelXxx().
    private static final String[] TEXTOS_MENU_PT = {
        "Início",
        "Buscar Séries",
        "Favoritos",
        "Já Assistidas",
        "Desejo Assistir",
    };

    // Todos os textos que precisam de tradução prévia.
    // IMPORTANTE: qualquer texto novo usado com t(...)/Tradutor.getCached(...)
    // em qualquer tela do sistema DEVE ser adicionado aqui, pois é este
    // lote que efetivamente aciona a tradução pela API (getCached() sozinho
    // não traduz nada — apenas lê o que já foi traduzido antes e guardado
    // em cache). Esta lista é usada tanto ao abrir a TelaPrincipal quanto
    // toda vez que o usuário troca de idioma (ver trocarIdioma()).
    private String[] todosOsTextos() {
        return new String[]{
            // Menu lateral / tela principal
            "Início", "Buscar Séries", "Favoritos",
            "Já Assistidas", "Desejo Assistir",
            "Sair", "🚪  Sair", "Olá, ", "Bem-vindo, ",
            "Deseja sair do sistema?",
            "Use o menu lateral para buscar séries e gerenciar suas listas.",
            "Idioma / Language",

            // Textos usados em TelaListaBase (telas de Favoritos, Já
            // Assistidas e Desejo Assistir compartilham essa classe base)
            "Pesquisar...", "Nome", "Idioma", "Nota", "Status", "Estreia",
            "Ordenar", "Melhor Nota", "Pior Nota", "Ver Detalhes", "Remover",
            "Selecione uma serie para ver os detalhes.",
            "Selecione uma serie para remover.",
            "desta lista?",

            // Textos usados em TelaBusca
            "Buscar Series", "Buscar",
            "Digite o nome de uma serie e pressione Buscar",
            "Pesquisar Serie",

            // Textos usados em TelaDetalhesSerie
            "Detalhes", "Gêneros", "Emissora", "Data de Estreia",
            "Data de Término", "Em andamento", "Resumo",
            "Sem resumo disponível.", "Assistida", "Desejo", "Voltar",
            "adicionada aos Favoritos!", "adicionada às Assistidas!",
            "adicionada à lista Desejo Assistir!",

            // Textos usados em MensagensUtil (títulos e mensagens dos
            // diálogos de erro/aviso/sucesso/confirmação exibidos em
            // qualquer tela do sistema)
            "Erro", "Aviso", "Informação", "Sucesso", "Confirmação",
            "Sem conexão com a internet.\nVerifique sua conexão e tente novamente.",
            "A API TVMaze está indisponível no momento.\nTente novamente mais tarde.",
            "Nenhuma série encontrada para o termo buscado.\nTente outro nome.",
            "O campo '", "' não pode estar vazio."
        };
    }

    public TelaPrincipal(SistemaController controller, Usuario usuario) {
        this.controller = controller;
        this.usuario = usuario;
        configurarJanela();

        // Pré-traduz todos os textos fixos ANTES de montar a interface.
        // Isso garante que quando construirInterface() for chamado, todo
        // Tradutor.getCached(...) usado pelas telas já vai encontrar a
        // tradução pronta no cache (se o idioma não for português).
        Tradutor.traduzirLote(todosOsTextos(), () -> {
            construirInterface();
            mostrarPainelInicio();
            setVisible(true);
        });
    }

    /** Configura propriedades básicas da janela principal */
    private void configurarJanela() {
        setTitle("TV Tracker — " + usuario.getNome());
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // centraliza na tela
        // DO_NOTHING_ON_CLOSE: não fecha automaticamente ao clicar no X,
        // pois queremos confirmar/salvar antes (ver o WindowListener abaixo)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);

        // Intercepta o clique no botão de fechar (X) da janela para
        // garantir que os dados sejam salvos antes de encerrar o programa
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.sair();
            }
        });
    }

    /** Monta o esqueleto da tela: sidebar à esquerda + painel de conteúdo à direita */
    private void construirInterface() {
        setLayout(new BorderLayout(0, 0));

        JPanel sidebar = criarSidebar();
        add(sidebar, BorderLayout.WEST);

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(COR_FUNDO);
        add(painelConteudo, BorderLayout.CENTER);
    }

    // ─────────────────────────── SIDEBAR ───────────────────────────

    /**
     * Monta o menu lateral completo: logo + nome do usuário no topo,
     * botões de navegação no meio, e seletor de idioma + botão Sair no rodapé.
     */
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(COR_SIDEBAR);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA));

        // ---- Topo — Logo e nome do usuário ----
        JPanel topo = new JPanel();
        topo.setBackground(COR_SIDEBAR);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBorder(new EmptyBorder(28, 20, 16, 20));

        JLabel logo = new JLabel("📺 TV Tracker");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(COR_TEXTO);
        logo.setAlignmentX(LEFT_ALIGNMENT);
        topo.add(logo);

        topo.add(Box.createVerticalStrut(4));

        // "Olá, " é traduzido; o nome do usuário nunca é traduzido (é um nome próprio)
        JLabel nomeUsuario = new JLabel(
            Tradutor.getCached("Olá, ") + usuario.getNome()
        );
        nomeUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nomeUsuario.setForeground(COR_SUBTEXTO);
        nomeUsuario.setAlignmentX(LEFT_ALIGNMENT);
        topo.add(nomeUsuario);

        sidebar.add(topo, BorderLayout.NORTH);

        // ---- Menu de navegação (botões Início, Buscar, Favoritos, etc.) ----
        JPanel menuNav = new JPanel();
        menuNav.setBackground(COR_SIDEBAR);
        menuNav.setLayout(new BoxLayout(menuNav, BoxLayout.Y_AXIS));
        menuNav.setBorder(new EmptyBorder(8, 12, 8, 12));

        botoesMenu = new JButton[TEXTOS_MENU_PT.length];
        for (int i = 0; i < TEXTOS_MENU_PT.length; i++) {
            final int idx = i; // precisa ser "effectively final" para usar dentro da lambda abaixo
            // Usa a versão já traduzida do cache (ou o próprio texto original se o idioma for PT)
            String labelTraduzido = Tradutor.getCached(TEXTOS_MENU_PT[i]);
            JButton btn = criarBotaoMenu(labelTraduzido);
            btn.addActionListener(e -> selecionarMenu(idx));
            botoesMenu[i] = btn;
            menuNav.add(btn);
            menuNav.add(Box.createVerticalStrut(4)); // pequeno espaço entre os botões
        }

        sidebar.add(menuNav, BorderLayout.CENTER);

        // ---- Rodapé — Seletor de idioma + botão Sair ----
        JPanel rodape = new JPanel();
        rodape.setBackground(COR_SIDEBAR);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA), // linha divisória no topo do rodapé
            new EmptyBorder(12, 12, 16, 12)
        ));

        // Seletor de idioma (bandeirinhas PT/EN/ES)
        JPanel painelIdioma = criarSeletorIdioma();
        painelIdioma.setAlignmentX(LEFT_ALIGNMENT);
        rodape.add(painelIdioma);
        rodape.add(Box.createVerticalStrut(10));

        // Botão "Sair" — pede confirmação antes de encerrar o programa
        JButton btnSair = criarBotaoMenu(Tradutor.getCached("🚪  Sair"));
        btnSair.addActionListener(e -> {
            String pergunta = Tradutor.getCached("Deseja sair do sistema?");
            String titulo   = Tradutor.getCached("Sair");
            if (JOptionPane.showConfirmDialog(
                    TelaPrincipal.this, pergunta, titulo,
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                controller.sair();
            }
        });
        btnSair.setAlignmentX(LEFT_ALIGNMENT);
        rodape.add(btnSair);

        sidebar.add(rodape, BorderLayout.SOUTH);
        return sidebar;
    }

    /**
     * Monta os três botões (bandeiras) de seleção de idioma: 🇧🇷 PT, 🇺🇸 EN, 🇪🇸 ES.
     * Usa um ButtonGroup para garantir que só um fique selecionado por vez.
     */
    private JPanel criarSeletorIdioma() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        painel.setOpaque(false);

        JLabel lbl = new JLabel("🌐");
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        lbl.setForeground(COR_SUBTEXTO);
        painel.add(lbl);

        ButtonGroup grupo = new ButtonGroup();
        // Cada linha: {emoji da bandeira, código do idioma}
        String[][] opcoes = {
            {"🇧🇷", Tradutor.PT},
            {"🇺🇸", Tradutor.EN},
            {"🇪🇸", Tradutor.ES}
        };

        for (String[] op : opcoes) {
            // JToggleButton customizado para desenhar um fundo arredondado,
            // destacado (COR_PRIMARIA) quando selecionado
            JToggleButton btn = new JToggleButton(op[0]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isSelected() ? COR_PRIMARIA : new Color(35, 35, 60));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            btn.setForeground(COR_TEXTO);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(3, 6, 3, 6));

            // Marca o botão do idioma atualmente selecionado
            if (Tradutor.getIdioma().equals(op[1])) btn.setSelected(true);

            final String codigo = op[1];
            btn.addActionListener(e -> trocarIdioma(codigo));

            grupo.add(btn);
            painel.add(btn);
        }

        return painel;
    }

    /**
     * Troca o idioma ativo do sistema e reconstrói toda a interface para
     * refletir a mudança.
     * Passos: limpa o cache antigo -> define o novo idioma -> pré-traduz
     * novamente todos os textos fixos -> reconstrói a sidebar e o painel
     * de conteúdo, voltando para a mesma tela que estava selecionada.
     */
    private void trocarIdioma(String codigo) {
        Tradutor.limparCache(); // descarta traduções do idioma anterior
        Tradutor.setIdioma(codigo);

        // Pré-traduz tudo de novo (agora no novo idioma) e só então reconstrói a tela
        Tradutor.traduzirLote(todosOsTextos(), () -> {
            getContentPane().removeAll();
            construirInterface();
            selecionarMenu(indiceAtivo); // volta para a mesma aba que estava aberta
            revalidate();
            repaint();
        });
    }

    // ─────────────────────────── MENU ───────────────────────────

    /**
     * Cria um botão de menu lateral customizado: sem fundo/borda padrão do
     * Swing, com destaque (cor de fundo) quando ativo ou ao passar o mouse.
     */
    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // "bg" é uma propriedade customizada (client property) usada
                // para guardar a cor de fundo atual do botão (ativo/hover/normal)
                Color bg = (Color) getClientProperty("bg");
                if (bg != null) {
                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(COR_SUBTEXTO);
        btn.setBackground(new Color(0, 0, 0, 0)); // transparente (o desenho customizado cuida do fundo)
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Efeito visual de "hover": destaca o botão levemente ao passar o mouse,
        // mas só se ele não estiver marcado como "ativo" (aba selecionada atual)
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.getClientProperty("ativo") == null) {
                    btn.putClientProperty("bg", COR_HOVER);
                    btn.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn.getClientProperty("ativo") == null) {
                    btn.putClientProperty("bg", null);
                    btn.repaint();
                }
            }
        });

        return btn;
    }

    /**
     * Chamado ao clicar em um item do menu lateral: desmarca visualmente
     * o item anterior, marca o novo item como ativo e mostra o painel
     * correspondente (Início, Busca, Favoritos, Assistidas ou Desejo Assistir).
     */
    private void selecionarMenu(int indice) {
        // Desativa visualmente o botão que estava ativo antes
        if (botoesMenu != null && indiceAtivo < botoesMenu.length) {
            botoesMenu[indiceAtivo].setForeground(COR_SUBTEXTO);
            botoesMenu[indiceAtivo].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            botoesMenu[indiceAtivo].putClientProperty("ativo", null);
            botoesMenu[indiceAtivo].putClientProperty("bg", null);
            botoesMenu[indiceAtivo].repaint();
        }

        indiceAtivo = indice;

        // Ativa visualmente (negrito + destaque) o botão recém-selecionado
        botoesMenu[indice].setForeground(COR_TEXTO);
        botoesMenu[indice].setFont(new Font("Segoe UI", Font.BOLD, 14));
        botoesMenu[indice].putClientProperty("ativo", true);
        botoesMenu[indice].putClientProperty("bg", COR_ATIVO);
        botoesMenu[indice].repaint();

        // Troca o painel de conteúdo conforme o índice do item clicado
        switch (indice) {
            case 0 -> mostrarPainelInicio();
            case 1 -> mostrarPainelBusca();
            case 2 -> mostrarPainelFavoritos();
            case 3 -> mostrarPainelAssistidas();
            case 4 -> mostrarPainelDesejoAssistir();
        }
    }

    // ─────────────────────────── PAINÉIS ───────────────────────────

    /**
     * Monta e exibe o painel "Início": mensagem de boas-vindas, três cards
     * com contagem de favoritos/assistidas/desejo assistir, e uma dica de uso.
     */
    private void mostrarPainelInicio() {
        painelConteudo.removeAll(); // limpa qualquer painel anterior

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Título de boas-vindas (traduzido, com o nome do usuário fora da tradução)
        JLabel titulo = new JLabel(
            Tradutor.getCached("Bem-vindo, ") + usuario.getNome() + "!"
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(COR_TEXTO);
        painel.add(titulo, BorderLayout.NORTH);

        // Cards de estatísticas rápidas (3 colunas lado a lado)
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(COR_FUNDO);
        cards.setBorder(new EmptyBorder(24, 0, 0, 0));

        cards.add(criarCardEstatistica("⭐",
            Tradutor.getCached("Favoritos"),
            String.valueOf(usuario.getFavoritos().size()),
            COR_PRIMARIA));

        cards.add(criarCardEstatistica("✅",
            Tradutor.getCached("Já Assistidas"),
            String.valueOf(usuario.getAssistidas().size()),
            COR_SUCESSO));

        cards.add(criarCardEstatistica("📋",
            Tradutor.getCached("Desejo Assistir"),
            String.valueOf(usuario.getDesejoAssistir().size()),
            COR_AVISO));

        painel.add(cards, BorderLayout.CENTER);

        // Caixa de dica na parte inferior da tela
        JPanel dicaPanel = new JPanel(new BorderLayout());
        dicaPanel.setBackground(COR_CARD);
        dicaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COR_BORDA, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel dica = new JLabel(
            Tradutor.getCached("Use o menu lateral para buscar séries e gerenciar suas listas.")
        );
        dica.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dica.setForeground(COR_SUBTEXTO);
        dicaPanel.add(dica, BorderLayout.CENTER);

        JPanel sul = new JPanel(new BorderLayout());
        sul.setBackground(COR_FUNDO);
        sul.setBorder(new EmptyBorder(16, 0, 0, 0));
        sul.add(dicaPanel);
        painel.add(sul, BorderLayout.SOUTH);

        painelConteudo.add(painel, BorderLayout.CENTER);
        painelConteudo.revalidate(); // recalcula o layout
        painelConteudo.repaint();    // redesenha a tela

        selecionarBotaoAtivo(0); // garante que o botão "Início" fique destacado no menu
    }

    /**
     * Cria um "card" de estatística (ícone + número + título), usado na tela Início.
     * Tem uma faixa colorida fina no topo, na cor recebida por parâmetro.
     */
    private JPanel criarCardEstatistica(String icone, String tituloCard, String valor, Color cor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fundo do card com cantos arredondados
                g2.setColor(COR_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                // Faixa colorida decorativa no topo do card
                g2.setColor(cor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcone.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblIcone);

        card.add(Box.createVerticalStrut(12));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(cor);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblValor);

        JLabel lblTitulo = new JLabel(tituloCard);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(COR_SUBTEXTO);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblTitulo);

        return card;
    }

    // ─────────────────────────── NAVEGAÇÃO ───────────────────────────
    // Os quatro métodos abaixo seguem sempre o mesmo padrão: limpam o
    // painel de conteúdo, instanciam a tela correspondente (que constrói
    // sua própria interface no construtor) e a adicionam ao centro da tela.

    /** Troca o painel de conteúdo para a tela de Busca de Séries */
    public void mostrarPainelBusca() {
        painelConteudo.removeAll();
        // TelaBusca também usará Tradutor.getCached() internamente para seus textos
        TelaBusca telaBusca = new TelaBusca(controller, this);
        painelConteudo.add(telaBusca, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        selecionarBotaoAtivo(1);
    }

    /** Troca o painel de conteúdo para a tela de Favoritos */
    public void mostrarPainelFavoritos() {
        painelConteudo.removeAll();
        TelaFavoritos tela = new TelaFavoritos(controller, this);
        painelConteudo.add(tela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        selecionarBotaoAtivo(2);
    }

    /** Troca o painel de conteúdo para a tela de Já Assistidas */
    public void mostrarPainelAssistidas() {
        painelConteudo.removeAll();
        TelaAssistidas tela = new TelaAssistidas(controller, this);
        painelConteudo.add(tela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        selecionarBotaoAtivo(3);
    }

    /** Troca o painel de conteúdo para a tela de Desejo Assistir */
    public void mostrarPainelDesejoAssistir() {
        painelConteudo.removeAll();
        TelaDesejoAssistir tela = new TelaDesejoAssistir(controller, this);
        painelConteudo.add(tela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        selecionarBotaoAtivo(4);
    }

    /**
     * Atualiza apenas a aparência (destaque) dos botões do menu lateral,
     * sem trocar o painel de conteúdo — usado pelos métodos mostrarPainelXxx()
     * para manter o botão correto destacado após a troca de tela.
     */
    private void selecionarBotaoAtivo(int indice) {
        if (botoesMenu == null) return;
        for (int i = 0; i < botoesMenu.length; i++) {
            if (i == indice) {
                botoesMenu[i].setForeground(COR_TEXTO);
                botoesMenu[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
                botoesMenu[i].putClientProperty("ativo", true);
                botoesMenu[i].putClientProperty("bg", COR_ATIVO);
            } else {
                botoesMenu[i].setForeground(COR_SUBTEXTO);
                botoesMenu[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
                botoesMenu[i].putClientProperty("ativo", null);
                botoesMenu[i].putClientProperty("bg", null);
            }
            botoesMenu[i].repaint();
        }
        indiceAtivo = indice;
    }

    /**
     * Recarrega a tela atualmente exibida (chamando selecionarMenu de novo
     * com o mesmo índice). Útil, por exemplo, depois de remover uma série
     * de uma lista, para que a contagem no título e a tabela se atualizem.
     */
    public void atualizarPainelAtual() {
        selecionarMenu(indiceAtivo);
    }
}
