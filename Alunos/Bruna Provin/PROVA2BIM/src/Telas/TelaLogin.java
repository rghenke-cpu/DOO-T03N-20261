package view;

import controller.SistemaController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Tela inicial do sistema: pede o nome/apelido do usuário para
 * criar ou carregar seu perfil. É a primeira janela exibida ao
 * abrir o programa (aberta pelo SistemaController.iniciar()).
 */
public class TelaLogin extends JFrame {

    // Paleta de cores do tema escuro usada nesta tela
    private static final Color COR_FUNDO     = new Color(15, 15, 28);
    private static final Color COR_CARD      = new Color(35, 35, 60);
    private static final Color COR_PRIMARIA  = new Color(99, 102, 241);
    private static final Color COR_PRIMARIA2 = new Color(139, 92, 246);
    private static final Color COR_TEXTO     = new Color(220, 220, 240);
    private static final Color COR_SUBTEXTO  = new Color(140, 140, 170);
    private static final Color COR_INPUT_BG  = new Color(45, 45, 75);
    private static final Color COR_INPUT_BRD = new Color(80, 80, 120);

    // Referência ao controller para avisar quando o login for confirmado
    private final SistemaController controller;
    // Campo de texto onde o usuário digita o nome/apelido
    private JTextField campoNome;

    public TelaLogin(SistemaController controller) {
        this.controller = controller;
        configurarJanela();
        construirInterface();
        setVisible(true);
    }

    /** Configura propriedades básicas da janela (tamanho, título, cor de fundo, etc.) */
    private void configurarJanela() {
        setTitle("TV Tracker - Login");
        setSize(480, 480);
        setLocationRelativeTo(null); // centraliza a janela na tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fechar esta janela encerra o programa
        setResizable(false); // não deixa o usuário redimensionar
        getContentPane().setBackground(COR_FUNDO);
    }

    /** Monta o layout geral da tela: um único "card" central usando GridBagLayout */
    private void construirInterface() {
        getContentPane().removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel card = criarCard();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1; // ocupa todo o espaço disponível
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(40, 50, 40, 50); // margens ao redor do card
        add(card, gbc);

        revalidate();
        repaint();
    }

    /**
     * Cria o "cartão" central com cantos arredondados que contém o
     * formulário de login (título, campo de nome, botão Entrar).
     */
    private JPanel criarCard() {
        // JPanel customizado que sobrescreve paintComponent para desenhar
        // um retângulo com cantos arredondados (Swing não tem isso pronto)
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false); // deixa o fundo padrão transparente (usamos o desenho customizado acima)
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // empilha os componentes verticalmente
        card.setBorder(new EmptyBorder(36, 40, 36, 40)); // espaçamento interno (padding)

        // Título "TV Tracker"
        JLabel titulo = new JLabel("TV Tracker", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(COR_TEXTO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        card.add(titulo);

        card.add(Box.createVerticalStrut(6)); // espaçamento vertical

        // Subtítulo explicativo
        JLabel sub = new JLabel("Gerencie suas series favoritas", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(COR_SUBTEXTO);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        card.add(sub);

        card.add(Box.createVerticalStrut(30));

        // Linha separadora horizontal
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_INPUT_BRD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);

        card.add(Box.createVerticalStrut(24));

        // Rótulo do campo de nome
        JLabel label = new JLabel("Nome ou apelido");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COR_SUBTEXTO);
        label.setAlignmentX(CENTER_ALIGNMENT);
        card.add(label);

        card.add(Box.createVerticalStrut(8));

        // Campo de texto para digitar o nome/apelido
        campoNome = new JTextField();
        campoNome.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campoNome.setForeground(COR_TEXTO);
        campoNome.setBackground(COR_INPUT_BG);
        campoNome.setCaretColor(COR_TEXTO); // cor do cursor piscante
        campoNome.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COR_INPUT_BRD, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        campoNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        campoNome.setAlignmentX(CENTER_ALIGNMENT);
        // Pressionar Enter dentro do campo também confirma o login
        campoNome.addActionListener(e -> realizarLogin());
        card.add(campoNome);

        card.add(Box.createVerticalStrut(24));

        // Botão principal "Entrar"
        JButton btnEntrar = criarBotaoPrimario("Entrar");
        btnEntrar.setAlignmentX(CENTER_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnEntrar.addActionListener(e -> realizarLogin());
        card.add(btnEntrar);

        card.add(Box.createVerticalStrut(20));

        // Texto de rodapé explicando o comportamento do login
        JLabel rodape = new JLabel(
            "Seu perfil sera criado ou carregado automaticamente",
            SwingConstants.CENTER
        );
        rodape.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rodape.setForeground(new Color(100, 100, 130));
        rodape.setAlignmentX(CENTER_ALIGNMENT);
        card.add(rodape);

        return card;
    }

    /**
     * Cria um botão com fundo em gradiente (efeito visual customizado),
     * usado no botão principal "Entrar".
     */
    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradiente da esquerda (COR_PRIMARIA) para a direita (COR_PRIMARIA2)
                GradientPaint gradient = new GradientPaint(
                    0, 0, COR_PRIMARIA, getWidth(), 0, COR_PRIMARIA2
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g); // desenha o texto do botão por cima do gradiente
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false); // deixa o desenho customizado ser o único fundo
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // cursor de "mãozinha" ao passar o mouse
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Pega o texto digitado no campo de nome e envia para o controller
     * processar o login (criar ou carregar o perfil correspondente).
     */
    private void realizarLogin() {
        String nome = campoNome.getText().trim();
        controller.processarLogin(nome);
    }
}
