import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonStorageServiceImpl implements IStorageService {

    // Nome do arquivo onde os dados locais serão salvos
    private static final String ARQUIVO_DADOS = "dados_usuario.json";
    private final ObjectMapper objectMapper;

    public JsonStorageServiceImpl() {
        this.objectMapper = new ObjectMapper();
        // Configuração para deixar o JSON com identação 
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void salvarDados(Usuario usuario) throws ExceptionManager {
        try {
            File arquivo = new File(ARQUIVO_DADOS);
            objectMapper.writeValue(arquivo, usuario);
        } catch (IOException e) {
            // Em vez de estourar o app ou dar printStackTrace, envia para classe de tratamento de exceções
            throw new ExceptionManager("Erro ao salvar os dados no arquivo local.", e);
        }
    }

    @Override
    public Usuario carregarDados() throws ExceptionManager {
        try {
            File arquivo = new File(ARQUIVO_DADOS);
            
            // Se o arquivo ainda não existe (primeira vez abrindo o app), retorna null
            // para que o sistema saiba que precisa pedir o nome/apelido do usuário.
            if (!arquivo.exists()) {
                return null;
            }
            
            // Converte o JSON de volta para o objeto Usuario e suas listas de séries
            return objectMapper.readValue(arquivo, Usuario.class);
        } catch (IOException e) {
            // Captura erros se o arquivo estiver corrompido ou inacessível
            throw new ExceptionManager("Erro ao ler o arquivo de dados local. O arquivo pode estar corrompido.", e);
        }
    }
}