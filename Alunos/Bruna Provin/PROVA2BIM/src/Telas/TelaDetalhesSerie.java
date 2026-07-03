package view;

import controller.SistemaController;
import model.Serie;
import util.MensagensUtil;
import util.Tradutor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Janela (JDialog) de detalhes completos de uma série: nome, status,
 * idioma, gêneros, nota, emissora, datas e resumo, além dos botões
 * para adicionar a série às listas de Favoritos, Assistida ou Desejo
 * Assistir. É modal (bloqueia a janela principal enquanto aberta) e
 * pode ser aberta tanto pela tela de Busca quanto pelas telas de lista
 * (Favoritos/Assistidas/Desejo Assistir).
 */
public class TelaDetalhesSerie extends JDialog {

    private final SistemaController controller;
    private final TelaPrincipal telaPrincipal;
    // Série cujos detalhes estão sendo exibidos nesta janela
    private final Serie serie;

    public TelaDetalhesSerie(SistemaController controller, TelaPrincipal telaPrincipal, Serie serie) {
        // super(...): configura o JDialog como filho de nenhuma janela específica
        // (Frame nulo), com o título "Detalhes — NomeDaSerie" e modal = true
        // (o "true" no final bloqueia a interação com a janela principal
        // enquanto esta estiver aberta)
        super((Frame) null, t("Detalhes") + " — " + serie.getNome(), true);
        this.controller = controller;
        this.telaPrincipal = telaPrincipal;
        this.serie = serie;
        configurarDialog();
        construirInterface();
    }

    /** Atalho interno (estático, pois é usado até no construtor via super()) para tradução em cache */
    private static String t(String texto) {
        return Tradutor.getCached(texto);
    }

    /** Configura tamanho, posição e cor de fundo da janela de diálogo */
    private void configurarDialog() {
        setSize(620, 660);
        setLocationRelativeTo(telaPrincipal); // centraliza sobre a janela principal
        setResizable(false);
        getContentPane().setBackground(TelaPrincipal.COR_FUNDO);
    }

    /**
     * Monta o conteúdo da janela: nome + status no topo, card com as
     * informações técnicas, card com o resumo/sinopse, e uma barra de
     * botões de ação no rodapé.
     */
    private void construirInterface() {
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(TelaPrincipal.COR_FUNDO);
        painel.setBorder(new EmptyBorder(24, 28, 16, 28));

        // Nome da série em destaque
        JLabel lblNome = new JLabel(serie.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNome.setForeground(TelaPrincipal.COR_TEXTO);
        lblNome.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(lblNome);

        painel.add(Box.createVerticalStrut(6));

        // "Badge" (selo) colorido mostrando o status (Em exibição/Encerrada/etc.)
        JLabel lblStatus = criarBadgeStatus(serie.getStatus());
        lblStatus.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(lblStatus);

        painel.add(Box.createVerticalStrut(20));

        // Card com as informações técnicas (idioma, gêneros, nota, etc.)
        JPanel cardInfo = criarCardInfo();
        cardInfo.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(cardInfo);

        painel.add(Box.createVerticalStrut(16));

        // Card com o resumo/sinopse da série
        JPanel cardResumo = criarCardResumo();
        cardResumo.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(cardResumo);

        // Envolve tudo em um scroll, caso o conteúdo não caiba na altura da janela
        JScrollPane scroll = new JScrollPane(painel);
        scroll.setBackground(TelaPrincipal.COR_FUNDO);
        scroll.getViewport().setBackground(TelaPrincipal.COR_FUNDO);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        JPanel rodape = criarRodape();
        add(rodape, BorderLayout.SOUTH);
    }

    /**
     * Cria o card com as informações técnicas da série, organizadas em
     * um grid de 2 colunas (rótulo à esquerda, valor à direita).
     */
    private JPanel criarCardInfo() {
        JPanel card = new JPanel(new GridLayout(0, 2, 12, 10)); // 0 linhas = quantidade automática
        card.setBackground(TelaPrincipal.COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TelaPrincipal.COR_BORDA, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        adicionarCampo(card, t("Idioma"), serie.getIdioma());
        adicionarCampo(card, t("Gêneros"), serie.getGeneros());
        adicionarCampo(card, t("Nota"), serie.getNota() > 0 ? String.format("%.1f / 10", serie.getNota()) : "N/A");
        adicionarCampo(card, t("Emissora"), serie.getEmissora());
        adicionarCampo(card, t("Data de Estreia"), serie.getDataEstreia() != null ? serie.getDataEstreia() : "N/A");
        // Se não tiver data de término, mostra "Em andamento" em vez de "N/A"
        adicionarCampo(card, t("Data de Término"), serie.getDataTermino() != null ? serie.getDataTermino() : t("Em andamento"));

        return card;
    }

    /**
     * Adiciona um par rótulo/valor ao painel (ex: "Idioma:" / "English"),
     * usado para montar as linhas do card de informações.
     */
    private void adicionarCampo(JPanel painel, String rotulo, String valor) {
        JLabel lblRotulo = new JLabel(rotulo + ":");
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRotulo.setForeground(TelaPrincipal.COR_SUBTEXTO);
        painel.add(lblRotulo);

        JLabel lblValor = new JLabel(valor != null && !valor.isEmpty() ? valor : "N/A");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValor.setForeground(TelaPrincipal.COR_TEXTO);
        painel.add(lblValor);
    }

    /** Cria o card com o título "Resumo" e o texto da sinopse da série, com quebra de linha automática */
    private JPanel criarCardResumo() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TelaPrincipal.COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TelaPrincipal.COR_BORDA, 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblTitulo = new JLabel(t("Resumo"));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(TelaPrincipal.COR_SUBTEXTO);
        card.add(lblTitulo, BorderLayout.NORTH);

        // JTextArea em vez de JLabel porque o resumo pode ser longo e
        // precisa quebrar linha automaticamente (word wrap)
        JTextArea txtResumo = new JTextArea();
        txtResumo.setText(serie.getResumo() != null ? serie.getResumo() : t("Sem resumo disponível."));
        txtResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtResumo.setForeground(TelaPrincipal.COR_TEXTO);
        txtResumo.setBackground(TelaPrincipal.COR_CARD);
        txtResumo.setEditable(false); // apenas leitura, não é um campo editável
        txtResumo.setLineWrap(true);      // quebra linha automaticamente
        txtResumo.setWrapStyleWord(true); // quebra por palavra inteira, não no meio dela
        txtResumo.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(txtResumo, BorderLayout.CENTER);

        return card;
    }

    /**
     * Cria o "badge" (selinho arredondado colorido) que mostra o status
     * da série, com cor diferente conforme o status: verde para em
     * exibição, cinza para encerrada, vermelho para os demais casos.
     */
    private JLabel criarBadgeStatus(String status) {
        String traduzido = TelaBusca.traduzirStatus(status);
        // JLabel customizado que desenha um fundo arredondado semitransparente
        // atrás do texto, com borda da mesma cor
        JLabel badge = new JLabel("  " + traduzido + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color cor = switch (status != null ? status : "") {
                    case "Running" -> TelaPrincipal.COR_SUCESSO;
                    case "Ended" -> TelaPrincipal.COR_SUBTEXTO;
                    default -> TelaPrincipal.COR_PERIGO;
                };
                // Fundo semitransparente (alpha 40 de 255) na cor do status
                g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // Borda sólida na mesma cor
                g2.setColor(cor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        Color cor = switch (status != null ? status : "") {
            case "Running" -> TelaPrincipal.COR_SUCESSO;
            case "Ended" -> TelaPrincipal.COR_SUBTEXTO;
            default -> TelaPrincipal.COR_PERIGO;
        };
        badge.setForeground(cor);
        badge.setOpaque(false);
        return badge;
    }

    /**
     * Cria a barra de botões no rodapé: adicionar aos Favoritos, marcar
     * como Assistida, adicionar à lista Desejo Assistir, e Voltar
     * (fecha a janela de detalhes).
     */
    private JPanel criarRodape() {
        JPanel rodape = new JPanel();
        rodape.setBackground(TelaPrincipal.COR_SIDEBAR);
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, TelaPrincipal.COR_BORDA),
            new EmptyBorder(14, 20, 14, 20)
        ));
        rodape.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));

        // Botão "Favoritos": adiciona a série à lista de favoritos e mostra confirmação
        JButton btnFavorito = TelaBusca.criarBotao(t("Favoritos"), TelaPrincipal.COR_PRIMARIA);
        btnFavorito.addActionListener(e -> {
            controller.adicionarFavorito(serie);
            MensagensUtil.sucesso(this, "\"" + serie.getNome() + "\" " + t("adicionada aos Favoritos!"));
        });
        rodape.add(btnFavorito);

        // Botão "Assistida": marca a série como já assistida
        JButton btnAssistida = TelaBusca.criarBotao(t("Assistida"), TelaPrincipal.COR_SUCESSO);
        btnAssistida.addActionListener(e -> {
            controller.adicionarAssistida(serie);
            MensagensUtil.sucesso(this, "\"" + serie.getNome() + "\" " + t("adicionada às Assistidas!"));
        });
        rodape.add(btnAssistida);

        // Botão "Desejo": adiciona a série à lista "desejo assistir"
        JButton btnDesejo = TelaBusca.criarBotao(t("Desejo"), TelaPrincipal.COR_AVISO);
        btnDesejo.setForeground(new Color(20, 20, 20)); // texto escuro, pois o fundo (amarelo) é claro
        btnDesejo.addActionListener(e -> {
            controller.adicionarDesejoAssistir(serie);
            MensagensUtil.sucesso(this, "\"" + serie.getNome() + "\" " + t("adicionada à lista Desejo Assistir!"));
        });
        rodape.add(btnDesejo);

        rodape.add(Box.createHorizontalStrut(16)); // espaço extra antes do botão Voltar

        // Botão "Voltar": simplesmente fecha esta janela de diálogo
        JButton btnVoltar = TelaBusca.criarBotao(t("Voltar"), TelaPrincipal.COR_CARD2);
        btnVoltar.addActionListener(e -> dispose());
        rodape.add(btnVoltar);

        return rodape;
    }
}
