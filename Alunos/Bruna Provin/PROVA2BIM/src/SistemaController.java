package controller;

import model.Serie;
import model.Usuario;
import repository.UsuarioRepository;
import service.ApiTvMazeService;
import view.*;

import javax.swing.*;
import java.util.List;

/**
 * Controlador principal do sistema TV Tracker (padrão MVC).
 * É o "cérebro" que conecta as telas (view) com os dados e regras de
 * negócio (model + repository + service). As telas nunca acessam o
 * repositório ou a API diretamente — sempre passam por este controller,
 * o que deixa o código mais organizado e fácil de manter.
 */
public class SistemaController {

    // Responsável por carregar/salvar os dados do usuário (persistência)
    private final UsuarioRepository usuarioRepository;
    // Responsável por buscar dados de séries na API TVMaze
    private final ApiTvMazeService apiService;

    // Referências às janelas principais, guardadas para poder fechar uma
    // e abrir a outra durante o fluxo de login
    private TelaLogin telaLogin;
    private TelaPrincipal telaPrincipal;

    public SistemaController() {
        this.usuarioRepository = new UsuarioRepository();
        this.apiService = new ApiTvMazeService();
    }

    /** Inicia o sistema exibindo a tela de login.
     *  SwingUtilities.invokeLater garante que a criação da interface
     *  gráfica aconteça na thread correta do Swing (Event Dispatch Thread). */
    public void iniciar() {
        SwingUtilities.invokeLater(() -> {
            telaLogin = new TelaLogin(this);
            telaLogin.setVisible(true);
        });
    }

    /**
     * Processa o login do usuário.
     * Cria ou carrega o perfil (via UsuarioRepository) e abre a tela principal.
     *
     * @param nome nome/apelido digitado pelo usuário
     */
    public void processarLogin(String nome) {
        // Validação simples: não deixa prosseguir com nome vazio
        if (nome == null || nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(telaLogin,
                "Por favor, informe seu nome ou apelido.",
                "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pede ao repositório para carregar um perfil existente com esse nome,
        // ou criar um novo caso não exista
        Usuario usuario = usuarioRepository.carregarOuCriarPerfil(nome.trim());

        // Fecha a tela de login e abre a tela principal já com o usuário carregado
        telaLogin.dispose();
        telaPrincipal = new TelaPrincipal(this, usuario);
        telaPrincipal.setVisible(true);
    }

    /**
     * Busca séries na API TVMaze pelo nome.
     * Apenas repassa a chamada para o ApiTvMazeService.
     *
     * @param nomeSerie nome da série a buscar
     * @return lista de séries encontradas
     * @throws Exception em caso de erro de rede
     */
    public List<Serie> buscarSeries(String nomeSerie) throws Exception {
        return apiService.buscarSeriesPorNome(nomeSerie);
    }

    // ==================== AÇÕES SOBRE AS LISTAS DO USUÁRIO ====================
    // Cada método abaixo segue o mesmo padrão: pega o usuário atual (getUsuario()),
    // chama o método correspondente do model (que mexe na lista em memória) e,
    // em seguida, persiste a alteração em disco chamando salvarDados().

    /**
     * Adiciona uma série à lista de favoritos do usuário e salva.
     *
     * @param serie série a adicionar
     */
    public void adicionarFavorito(Serie serie) {
        getUsuario().adicionarFavorito(serie);
        salvarDados();
    }

    /**
     * Remove uma série da lista de favoritos e salva.
     *
     * @param serie série a remover
     */
    public void removerFavorito(Serie serie) {
        getUsuario().removerFavorito(serie);
        salvarDados();
    }

    /**
     * Adiciona uma série à lista de assistidas e salva.
     *
     * @param serie série a adicionar
     */
    public void adicionarAssistida(Serie serie) {
        getUsuario().adicionarAssistida(serie);
        salvarDados();
    }

    /**
     * Remove uma série da lista de assistidas e salva.
     *
     * @param serie série a remover
     */
    public void removerAssistida(Serie serie) {
        getUsuario().removerAssistida(serie);
        salvarDados();
    }

    /**
     * Adiciona uma série à lista de desejo assistir e salva.
     *
     * @param serie série a adicionar
     */
    public void adicionarDesejoAssistir(Serie serie) {
        getUsuario().adicionarDesejoAssistir(serie);
        salvarDados();
    }

    /**
     * Remove uma série da lista de desejo assistir e salva.
     *
     * @param serie série a remover
     */
    public void removerDesejoAssistir(Serie serie) {
        getUsuario().removerDesejoAssistir(serie);
        salvarDados();
    }

    /**
     * Salva os dados do usuário em disco (arquivo JSON), através do repositório.
     * Em caso de erro, apenas loga no console sem interromper o fluxo do
     * usuário (para não travar a interface por causa de um problema de disco).
     */
    public void salvarDados() {
        try {
            usuarioRepository.salvar();
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    /**
     * Encerra o sistema: salva os dados uma última vez e finaliza o
     * programa (System.exit fecha a JVM por completo).
     */
    public void sair() {
        salvarDados();
        System.exit(0);
    }

    /** Retorna o usuário atualmente logado no sistema */
    public Usuario getUsuario() {
        return usuarioRepository.getUsuarioAtual();
    }

    /** Retorna o serviço de acesso à API TVMaze, caso alguma tela precise usá-lo diretamente */
    public ApiTvMazeService getApiService() {
        return apiService;
    }
}
