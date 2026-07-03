package br.escola.service;

import br.escola.model.Serie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Responsável por buscar séries na API pública do TVMaze
public class TVMazeService {

    private final HttpClient httpClient;

    // ObjectMapper do Jackson para converter o JSON da resposta em objetos
    private final ObjectMapper objectMapper;

    public TVMazeService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Busca séries pelo nome e retorna uma lista de objetos Serie
    // Lança IOException se houver problema de leitura e InterruptedException se a requisição for interrompida
    public List<Serie> buscarPorNome(String nome) throws IOException, InterruptedException {
        List<Serie> series = new ArrayList<>();

        String url = "https://api.tvmaze.com/search/shows?q=" + URLEncoder.encode(nome, StandardCharsets.UTF_8);

        // Constrói a requisição HTTP do tipo GET
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Envia a requisição e recebe a resposta como String
        HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());

        // Verifica se a resposta foi bem-sucedida (código 200 = OK)
        if (resposta.statusCode() == 200) {
            JsonNode raiz = objectMapper.readTree(resposta.body());

            // A API retorna um array de resultados
            if (raiz.isArray()) {
                for (JsonNode item : raiz) {
                    JsonNode show = item.path("show");
                    if (!show.isMissingNode()) {
                        Serie serie = parsearSerie(show);
                        series.add(serie);
                    }
                }
            }
        }

        return series;
    }

    // Converte um nó JSON de uma série em um objeto Serie
    // path() retorna um nó vazio se o campo não existir (não lança exceção)
    private Serie parsearSerie(JsonNode show) {
        int id = show.path("id").asInt();
        String nome = show.path("name").asText("Desconhecido");
        String status = show.path("status").asText("N/A");
        String dataEstreia = show.path("premiered").asText("N/A");
        String dataTermino = show.path("ended").asText("N/A");
        String idioma = show.path("language").asText("N/A");

        double nota = show.path("rating").path("average").asDouble(0.0);

        String emissora = show.path("network").path("name").asText(null);
        if (emissora == null) {
            emissora = show.path("webChannel").path("name").asText("N/A");
        }

        List<String> listaGeneros = new ArrayList<>();
        JsonNode generosNode = show.path("genres");
        if (generosNode.isArray()) {
            for (JsonNode g : generosNode) {
                listaGeneros.add(g.asText());
            }
        }
        String generos = String.join(", ", listaGeneros);

        String sumario = show.path("summary").asText("N/A");
        if (sumario != null) {
            sumario = sumario.replaceAll("<[^>]*>", "");
        }

        return new Serie(id, nome, status, dataEstreia, dataTermino, nota, emissora, generos, sumario, idioma);
    }
}