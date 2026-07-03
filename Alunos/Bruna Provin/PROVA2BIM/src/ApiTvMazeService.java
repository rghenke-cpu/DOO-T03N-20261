package service;

import model.Serie;
import util.JsonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por toda a comunicação com a API pública e gratuita
 * TVMaze (https://www.tvmaze.com/api), que fornece dados de séries de TV.
 * Aqui é feita a requisição HTTP crua; a conversão do JSON de resposta
 * para objetos Serie fica a cargo da classe util.JsonUtil.
 */
public class ApiTvMazeService {

    // Endereço base de todos os endpoints da API
    private static final String BASE_URL = "https://api.tvmaze.com";
    // Tempo máximo de espera (em milissegundos) para conectar ou ler a resposta
    private static final int TIMEOUT_MS = 8000;

    /**
     * Busca séries pelo nome usando a API TVMaze.
     * Endpoint: GET /search/shows?q={nome}
     * A API retorna uma lista de resultados com um "score" de relevância
     * e os dados da série dentro de "show".
     *
     * @param nomeSerie nome ou parte do nome da série
     * @return lista de séries encontradas (vazia se não encontrar nada)
     * @throws Exception em caso de erro de rede ou API
     */
    public List<Serie> buscarSeriesPorNome(String nomeSerie) throws Exception {
        // Evita fazer requisição desnecessária se o campo de busca estiver vazio
        if (nomeSerie == null || nomeSerie.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // URLEncoder garante que espaços e caracteres especiais no nome da série
        // sejam convertidos corretamente para uma URL válida (ex: espaço vira %20)
        String query = URLEncoder.encode(nomeSerie.trim(), StandardCharsets.UTF_8.toString());
        String urlStr = BASE_URL + "/search/shows?q=" + query;

        String json = realizarRequisicao(urlStr);
        // Delega o parse do JSON (que tem um formato específico de busca) para o JsonUtil
        return JsonUtil.jsonApiParaListaSeries(json);
    }

    /**
     * Busca os detalhes completos de uma série específica pelo seu ID na API.
     * Endpoint: GET /shows/{id}
     * Diferente da busca por nome, aqui a resposta já é o objeto "show" puro,
     * sem o wrapper de score/relevância.
     *
     * @param id identificador da série na TVMaze
     * @return objeto Serie com dados completos
     * @throws Exception em caso de erro de rede ou API
     */
    public Serie buscarDetalhesShow(int id) throws Exception {
        String urlStr = BASE_URL + "/shows/" + id;
        String json = realizarRequisicao(urlStr);
        return JsonUtil.parsearShowApi(json);
    }

    /**
     * Realiza uma requisição HTTP GET genérica e retorna o corpo da resposta
     * como uma única String (o JSON bruto). Método privado, usado internamente
     * pelos outros métodos públicos desta classe.
     *
     * @param urlStr URL completa da requisição
     * @return corpo da resposta como String
     * @throws Exception em caso de falha na conexão ou resposta de erro da API
     */
    private String realizarRequisicao(String urlStr) throws Exception {
        HttpURLConnection conexao = null;
        try {
            URL url = new URL(urlStr);
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setConnectTimeout(TIMEOUT_MS); // tempo máx. para abrir a conexão
            conexao.setReadTimeout(TIMEOUT_MS);     // tempo máx. para ler a resposta
            conexao.setRequestProperty("Accept", "application/json");
            // Identifica o cliente (boa prática, algumas APIs exigem User-Agent)
            conexao.setRequestProperty("User-Agent", "TVTracker-Java/1.0");

            int codigoResposta = conexao.getResponseCode();

            // 404 na TVMaze normalmente significa "nada encontrado" — tratamos
            // como lista vazia em vez de erro, devolvendo um array JSON vazio
            if (codigoResposta == HttpURLConnection.HTTP_NOT_FOUND) {
                return "[]";
            }

            // Qualquer outro código diferente de 200 (OK) é considerado erro
            if (codigoResposta != HttpURLConnection.HTTP_OK) {
                throw new Exception("Erro na API: código " + codigoResposta);
            }

            // Lê o corpo da resposta linha por linha, concatenando tudo em um StringBuilder
            BufferedReader leitor = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
            leitor.close();

            return resposta.toString();

        } finally {
            // O "finally" garante que a conexão seja sempre fechada,
            // mesmo que ocorra uma exceção durante a requisição
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    /**
     * Faz uma chamada de teste (busca a série de id 1) só para checar
     * se a API está respondendo normalmente.
     *
     * @return true se a API responder corretamente, false em caso de erro
     */
    public boolean verificarConexao() {
        try {
            realizarRequisicao(BASE_URL + "/shows/1");
            return true;
        } catch (Exception e) {
            System.err.println("API inacessível: " + e.getMessage());
            return false;
        }
    }
}
