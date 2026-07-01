import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TvMazeApiClientImpl implements ITvApiClient {

    private final ObjectMapper objectMapper;

    public TvMazeApiClientImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Serie> buscarSeriesPorNome(String nome) throws ExceptionManager {
        List<Serie> resultado = new ArrayList<>();
        HttpURLConnection conexao = null;

        try {
            // Codifica o nome para evitar quebras por conta de espaços e acentos na URL
            String nomeCodificado = URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
            String urlAlvo = "https://api.tvmaze.com/search/shows?q=" + nomeCodificado;

            URL url = URI.create(urlAlvo).toURL();
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setConnectTimeout(5000);
            conexao.setReadTimeout(5000);

            int status = conexao.getResponseCode();
            if (status != 200) {
                throw new ExceptionManager("Erro na comunicação com a API. Código HTTP: " + status);
            }

            // Lê a resposta da requisição
            BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder respostaJson = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                respostaJson.append(linha);
            }
            leitor.close();

            // Mapeamento dinâmico com Jackson para navegar na árvore JSON
            JsonNode arrayRaiz = objectMapper.readTree(respostaJson.toString());
            
            for (JsonNode item : arrayRaiz) {
                JsonNode jsonShow = item.get("show");
                if (jsonShow != null) {
                    Serie serie = new Serie();
                    
                    // Extração dos campos conforme a estrutura da API do TVMaze
                    serie.setNome(jsonShow.path("name").asText("Sem Nome"));
                    serie.setIdioma(jsonShow.path("language").asText("Desconhecido"));
                    serie.setEstado(jsonShow.path("status").asText("Desconhecido"));
                    serie.setDataEstreia(jsonShow.path("premiered").asText("N/A"));
                    serie.setDataTermino(jsonShow.path("ended").asText("N/A"));

                    // Gêneros (Array de Strings)
                    List<String> generos = new ArrayList<>();
                    JsonNode arrayGeneros = jsonShow.path("genres");
                    if (arrayGeneros.isArray()) {
                        for (JsonNode g : arrayGeneros) {
                            generos.add(g.asText());
                        }
                    }
                    serie.setGeneros(generos);

                    // Nota Geral (Dentro do objeto rating)
                    JsonNode rating = jsonShow.path("rating");
                    if (!rating.isMissingNode() && rating.has("average") && !rating.get("average").isNull()) {
                        serie.setNotaGeral(rating.get("average").asDouble());
                    } else {
                        serie.setNotaGeral(0.0);
                    }

                    // Emissora (Verifica Network ou WebChannel)
                    String nomeEmissora = "Desconhecida";
                    if (jsonShow.has("network") && !jsonShow.get("network").isNull()) {
                        nomeEmissora = jsonShow.path("network").path("name").asText("Desconhecida");
                    } else if (jsonShow.has("webChannel") && !jsonShow.get("webChannel").isNull()) {
                        nomeEmissora = jsonShow.path("webChannel").path("name").asText("Desconhecida");
                    }
                    serie.setEmissora(nomeEmissora);

                    resultado.add(serie);
                }
            }

        } catch (Exception e) {
            throw new ExceptionManager("Falha ao buscar séries remotamente: " + e.getMessage(), e);
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }

        return resultado;
    }
}