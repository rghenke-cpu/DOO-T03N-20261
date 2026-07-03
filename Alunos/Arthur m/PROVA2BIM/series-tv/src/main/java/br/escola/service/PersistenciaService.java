package br.escola.service;

import br.escola.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

// Responsável por salvar e carregar os dados do usuário em formato JSON usando Jackson
public class PersistenciaService {

    private static final String ARQUIVO = "dados.json";

    // ObjectMapper é a classe do Jackson que converte objetos Java em JSON e vice-versa
    private final ObjectMapper objectMapper;

    public PersistenciaService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Salva o objeto Usuario em um arquivo JSON formatado
    // Lança IOException se houver problema ao escrever o arquivo
    public void salvar(Usuario usuario) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO), usuario);
    }

    // Carrega o arquivo JSON e reconstrói o objeto Usuario
    // Lança IOException se houver problema ao ler o arquivo
    public Usuario carregar() throws IOException {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return null;
        }
        return objectMapper.readValue(arquivo, Usuario.class);
    }
}