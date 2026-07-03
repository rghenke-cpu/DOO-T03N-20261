package repository;

import model.Usuario;
import service.PersistenciaService;

/**
 * Repositório (padrão Repository) responsável por dar acesso aos dados
 * do usuário para o resto do sistema, escondendo os detalhes de "como"
 * e "onde" esses dados são guardados.
 *
 * Fica entre o Controller e o PersistenciaService: o Controller não
 * precisa saber que existe um arquivo JSON, apenas pede o usuário
 * "atual" ou manda "salvar" através deste repositório.
 */
public class UsuarioRepository {

    // Serviço que realmente lê/escreve o arquivo JSON em disco
    private final PersistenciaService persistenciaService;
    // Guarda em memória o usuário que está logado no momento
    private Usuario usuarioAtual;

    public UsuarioRepository() {
        this.persistenciaService = new PersistenciaService();
    }

    /**
     * Carrega ou cria um perfil de usuário com base no nome fornecido.
     * Regra: se já existir um arquivo salvo E o nome digitado bater
     * (ignorando maiúsculas/minúsculas) com o nome salvo, carrega os
     * dados existentes (mantendo favoritos, assistidas, etc). Caso
     * contrário, cria um usuário novo do zero.
     *
     * @param nome nome/apelido digitado pelo usuário na tela de login
     * @return Usuario carregado ou recém-criado
     */
    public Usuario carregarOuCriarPerfil(String nome) {
        try {
            // Só tenta carregar se realmente existir um arquivo de dados salvo
            if (persistenciaService.existeDadosSalvos()) {
                Usuario salvo = persistenciaService.carregarUsuario();
                // Compara o nome salvo com o nome digitado agora
                if (salvo != null && nome.equalsIgnoreCase(salvo.getNome())) {
                    usuarioAtual = salvo;
                    return usuarioAtual;
                }
            }
        } catch (Exception e) {
            // Se der qualquer erro ao carregar (arquivo corrompido, etc.),
            // apenas avisa no console e segue o fluxo criando um usuário novo
            System.err.println("Aviso: não foi possível carregar dados existentes: " + e.getMessage());
        }

        // Não havia dados salvos, ou o nome não bateu: cria um perfil novo
        usuarioAtual = new Usuario(nome.trim());
        return usuarioAtual;
    }

    /**
     * Salva os dados do usuário atual no disco, delegando para o
     * PersistenciaService.
     *
     * @throws Exception em caso de erro na gravação
     */
    public void salvar() throws Exception {
        if (usuarioAtual != null) {
            persistenciaService.salvarUsuario(usuarioAtual);
        }
    }

    /** Retorna o usuário atualmente carregado em memória */
    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    /** Define manualmente o usuário atual (usado em casos específicos) */
    public void setUsuarioAtual(Usuario usuario) {
        this.usuarioAtual = usuario;
    }

    /** Verifica se existe algum arquivo de dados salvo em disco */
    public boolean existeDadosSalvos() {
        return persistenciaService.existeDadosSalvos();
    }

    /**
     * Tenta carregar os dados salvos, sem lançar exceção para quem chamou.
     * Se der erro, apenas loga no console e retorna null.
     */
    public Usuario tentarCarregarDados() {
        try {
            return persistenciaService.carregarUsuario();
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            return null;
        }
    }
}
