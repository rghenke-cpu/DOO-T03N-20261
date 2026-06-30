package fag.view;

import fag.model.Usuario;
import fag.service.PersistenciaService;
import fag.service.SerieService;
import fag.util.EstiloUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Tela inicial do sistema, onde o usuário acessa busca, listas e alteração de nome.
public class TelaPrincipal extends JFrame {

    private final Usuario usuario;
    private final PersistenciaService persistenciaService;
    private final SerieService serieService;

    private JLabel labelUsuario;

    public TelaPrincipal(Usuario usuario, PersistenciaService persistenciaService) {
        this.usuario = usuario;
        this.persistenciaService = persistenciaService;
        this.serieService = new SerieService();

        configurarJanela();
        montarComponentes();
    }

    // Configura as informações básicas da janela principal.
    private void configurarJanela() {
        setTitle("Minhas Séries de TV");
        setSize(520, 390);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(EstiloUtil.COR_FUNDO);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarESair();
            }
        });
    }

    // Cria os botões e componentes visuais da tela principal.
    private void montarComponentes() {
        setLayout(new BorderLayout(10, 10));

        labelUsuario = new JLabel("Minhas Séries de TV - Usuário: " + usuario.getNome(), JLabel.CENTER);
        EstiloUtil.estilizarTitulo(labelUsuario);

        JButton botaoBuscar = new JButton("Buscar séries");
        JButton botaoFavoritos = new JButton("Ver favoritos");
        JButton botaoJaAssistidas = new JButton("Ver já assistidas");
        JButton botaoDesejoAssistir = new JButton("Ver desejo assistir");
        JButton botaoAlterarNome = new JButton("Alterar nome/apelido");
        JButton botaoSair = new JButton("Salvar e sair");

        EstiloUtil.estilizarBotao(botaoBuscar);
        EstiloUtil.estilizarBotao(botaoFavoritos);
        EstiloUtil.estilizarBotao(botaoJaAssistidas);
        EstiloUtil.estilizarBotao(botaoDesejoAssistir);
        EstiloUtil.estilizarBotao(botaoAlterarNome);
        EstiloUtil.estilizarBotaoSair(botaoSair);

        botaoBuscar.addActionListener(e -> abrirTelaBusca());
        botaoFavoritos.addActionListener(e -> abrirTelaListas(SerieService.LISTA_FAVORITOS));
        botaoJaAssistidas.addActionListener(e -> abrirTelaListas(SerieService.LISTA_JA_ASSISTIDAS));
        botaoDesejoAssistir.addActionListener(e -> abrirTelaListas(SerieService.LISTA_DESEJO_ASSISTIR));
        botaoAlterarNome.addActionListener(e -> alterarNomeUsuario());
        botaoSair.addActionListener(e -> confirmarESair());

        JPanel painelBotoes = new JPanel(new GridLayout(6, 1, 10, 10));
        EstiloUtil.estilizarPainel(painelBotoes);

        painelBotoes.add(botaoBuscar);
        painelBotoes.add(botaoFavoritos);
        painelBotoes.add(botaoJaAssistidas);
        painelBotoes.add(botaoDesejoAssistir);
        painelBotoes.add(botaoAlterarNome);
        painelBotoes.add(botaoSair);

        add(labelUsuario, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);
    }

    // Abre a tela responsável por buscar séries na API.
    private void abrirTelaBusca() {
        try {
            TelaBusca telaBusca = new TelaBusca(this, usuario, persistenciaService, serieService);
            telaBusca.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao abrir a tela de busca", e);
        }
    }

    // Abre a tela de uma das listas: favoritos, já assistidas ou desejo assistir.
    private void abrirTelaListas(String tipoLista) {
        try {
            TelaListas telaListas = new TelaListas(this, usuario, tipoLista, persistenciaService, serieService);
            telaListas.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao abrir lista", e);
        }
    }

    // Permite alterar o nome ou apelido do usuário e salva a alteração no JSON.
    private void alterarNomeUsuario() {
        try {
            String novoNome = JOptionPane.showInputDialog(
                    this,
                    "Digite o novo nome ou apelido:",
                    usuario.getNome()
            );

            if (novoNome == null || novoNome.trim().isEmpty()) {
                return;
            }

            usuario.setNome(novoNome.trim());
            persistenciaService.salvarUsuario(usuario);
            labelUsuario.setText("Minhas Séries de TV - Usuário: " + usuario.getNome());

            JOptionPane.showMessageDialog(this, "Nome alterado com sucesso.");

        } catch (Exception e) {
            mostrarErro("Erro ao alterar nome", e);
        }
    }

    // Mostra confirmação antes de fechar o sistema.
    private void confirmarESair() {
        if (EstiloUtil.confirmarFechamento(this)) {
            fecharSistema();
        }
    }

    // Antes de fechar o sistema, tenta salvar os dados do usuário no arquivo JSON.
    private void fecharSistema() {
        try {
            persistenciaService.salvarUsuario(usuario);
        } catch (Exception e) {
            mostrarErro("Erro ao salvar dados antes de sair", e);
        }

        dispose();
        System.exit(0);
    }

    // Exibe mensagens de erro na tela sem encerrar o programa inesperadamente.
    private void mostrarErro(String mensagem, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                mensagem + ":\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}