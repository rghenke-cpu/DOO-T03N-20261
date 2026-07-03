package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Usuario;
import util.JsonUtil;

import java.io.File;

/**
 * Serviço responsável por gravar e ler os dados do usuário em um arquivo
 * JSON local ("dados_usuario.json"), funcionando como uma persistência
 * simples em disco (sem banco de dados). Usa a biblioteca Jackson para
 * fazer toda a conversão objeto <-> JSON automaticamente.
 */
public class PersistenciaService {

    // Nome do arquivo onde os dados são salvos, criado na raiz do projeto
    private static final String ARQUIVO_DADOS = "dados_usuario.json";
    // Objeto do Jackson responsável por serializar/desserializar
    private final ObjectMapper mapper;

    public PersistenciaService() {
        // Reutiliza o ObjectMapper já configurado e centralizado no JsonUtil,
        // evitando criar instâncias repetidas (ObjectMapper é "caro" de configurar)
        this.mapper = JsonUtil.getMapper();
    }

    /**
     * Salva os dados do usuário no arquivo JSON, com indentação legível
     * (fácil de abrir e conferir manualmente em um editor de texto).
     *
     * @param usuario objeto Usuario a ser persistido
     * @throws Exception em caso de erro na gravação
     */
    public void salvarUsuario(Usuario usuario) throws Exception {
        // Proteção simples: não tenta salvar se o usuário for nulo
        if (usuario == null) {
            System.err.println("Tentativa de salvar usuário nulo ignorada.");
            return;
        }

        try {
            // writerWithDefaultPrettyPrinter() formata o JSON com quebras de
            // linha e indentação. writeValue grava direto no arquivo informado.
            // O Jackson serializa o objeto Usuario inteiro automaticamente,
            // incluindo as três listas de Series dentro dele.
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File(ARQUIVO_DADOS), usuario);

            System.out.println("Dados salvos com sucesso em: " + ARQUIVO_DADOS);
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
            // Relança como uma exceção mais amigável para quem chamou o método
            throw new Exception("Não foi possível salvar os dados: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados do usuário a partir do arquivo JSON salvo anteriormente.
     * Retorna null se o arquivo não existir (indicando que é o primeiro acesso
     * ao sistema, sem dados salvos ainda).
     *
     * @return Usuario carregado, ou null se não houver dados salvos
     * @throws Exception em caso de erro na leitura ou JSON inválido/corrompido
     */
    public Usuario carregarUsuario() throws Exception {
        File arquivo = new File(ARQUIVO_DADOS);

        if (!arquivo.exists()) {
            System.out.println("Arquivo de dados não encontrado. Primeiro acesso.");
            return null;
        }

        try {
            // mapper.readValue lê o arquivo inteiro e já devolve um objeto
            // Usuario totalmente montado, recriando as listas de Series
            Usuario usuario = mapper.readValue(arquivo, Usuario.class);
            System.out.println("Dados carregados com sucesso para: " + usuario.getNome());
            return usuario;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            // O texto do arquivo não é um JSON válido (sintaxe quebrada)
            System.err.println("Arquivo JSON corrompido: " + e.getMessage());
            throw new Exception("Arquivo de dados corrompido. Os dados serão reiniciados.");
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            // O JSON é válido, mas a estrutura não bate com a classe Usuario
            System.err.println("Estrutura JSON incompatível: " + e.getMessage());
            throw new Exception("Formato de dados inválido. Os dados serão reiniciados.");
        } catch (Exception e) {
            // Qualquer outro erro inesperado de leitura
            System.err.println("Erro ao ler arquivo de dados: " + e.getMessage());
            throw new Exception("Não foi possível ler os dados salvos: " + e.getMessage());
        }
    }

    /**
     * Verifica se existe algum arquivo de dados salvo e se ele não está vazio.
     *
     * @return true se o arquivo existe e tem conteúdo
     */
    public boolean existeDadosSalvos() {
        File arquivo = new File(ARQUIVO_DADOS);
        return arquivo.exists() && arquivo.length() > 0;
    }

    /**
     * Apaga o arquivo de dados do disco, fazendo um "reset" completo
     * (não é chamado em nenhum lugar da interface atualmente, mas fica
     * disponível como utilitário).
     */
    public void resetarDados() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            arquivo.delete();
            System.out.println("Dados resetados.");
        }
    }
}
