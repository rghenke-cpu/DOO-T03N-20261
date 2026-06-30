package fag.service;

import fag.model.Serie;
import fag.model.Usuario;
import fag.util.JsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.ArrayList;

// Classe responsável por salvar e carregar os dados do usuário
public class PersistenciaService {

    private final Path caminhoArquivo;

    public PersistenciaService() {
        this.caminhoArquivo = Paths.get("data", "usuario.json");
    }

    // Carrega o usuário salvo no arquivo JSON. Se o arquivo não existir ou estiver com erro, retorna null.
    public Usuario carregarUsuario() {
        try {
            // Se o arquivo JSON ainda não existir, significa que é a primeira execução do sistema.
            if (!Files.exists(caminhoArquivo)) {
                return null;
            }

            byte[] bytes = Files.readAllBytes(caminhoArquivo);
            String json = new String(bytes, StandardCharsets.UTF_8);

            Usuario usuario = JsonUtil.converterJsonParaUsuario(json);

            if (usuario != null) {
                usuario.garantirListasCriadas();
            }

            return usuario;

        } catch (Exception e) {
            // Tratamento de exceção para evitar que um problema com o json feche o programa
            System.out.println("Erro ao carregar arquivo JSON: " + e.getMessage());
            return null;
        }
    }

    // Salva o usuário e suas listas no arquivo JSON.
    public void salvarUsuario(Usuario usuario) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inválido para salvar.");
        }

        usuario.garantirListasCriadas();

        if (caminhoArquivo.getParent() != null) {
            // Cria a pasta "data" caso ela ainda não exista.
            Files.createDirectories(caminhoArquivo.getParent());
        }

        // Converte o objeto Usuario para texto JSON antes de salvar.
        String json = JsonUtil.converterUsuarioParaJson(usuario);

        Files.write(caminhoArquivo, json.getBytes(StandardCharsets.UTF_8));
    }

    // Usuário teste
    public Usuario criarUsuarioComDadosPreCarregados(String nome) {
        Usuario usuario = new Usuario(nome);

        ArrayList<String> generosDrama = new ArrayList<String>();
        generosDrama.add("Drama");
        generosDrama.add("Crime");

        ArrayList<String> generosFantasia = new ArrayList<String>();
        generosFantasia.add("Drama");
        generosFantasia.add("Fantasia");

        ArrayList<String> generosComedia = new ArrayList<String>();
        generosComedia.add("Comédia");
        generosComedia.add("Romance");

        Serie breakingBad = new Serie(
                "Breaking Bad",
                "English",
                generosDrama,
                9.2,
                "Ended",
                "2008-01-20",
                "2013-09-29",
                "AMC"
        );

        Serie gameOfThrones = new Serie(
                "Game of Thrones",
                "English",
                generosFantasia,
                8.9,
                "Ended",
                "2011-04-17",
                "2019-05-19",
                "HBO"
        );

        Serie friends = new Serie(
                "Friends",
                "English",
                generosComedia,
                8.5,
                "Ended",
                "1994-09-22",
                "2004-05-06",
                "NBC"
        );

        usuario.getFavoritos().add(breakingBad);
        usuario.getJaAssistidas().add(gameOfThrones);
        usuario.getDesejoAssistir().add(friends);

        return usuario;
    }
}